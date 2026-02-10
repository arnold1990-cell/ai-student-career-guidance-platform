package com.edurite.application.service;

import com.edurite.application.exception.AppException;
import com.edurite.domain.model.*;
import com.edurite.domain.model.Enums.*;
import com.edurite.domain.repo.*;
import com.edurite.integrations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class StudentService {
    private final StudentProfileRepository studentRepo; private final DocumentRepository docRepo; private final BursaryRepository bursaryRepo;
    private final BursaryApplicationRepository appRepo; private final CareerRepository careerRepo; private final AiRecommendationEngine ai;
    private final NotificationRepository notificationRepo; private final BursaryViewEventRepository viewRepo; private final StudentSubscriptionRepository subRepo;

    public StudentProfile profile(Long uid){ return studentRepo.findById(uid).orElseThrow(()->new AppException(404,"Profile missing")); }
    public StudentProfile saveProfile(Long uid, StudentProfile p){ p.setUserId(uid); return studentRepo.save(p); }
    public Document saveDoc(Long uid, DocumentType type, String filename, String mime, long size, String path){ return docRepo.save(Document.builder().ownerUserId(uid).type(type).filename(filename).mimeType(mime).size(size).storagePath(path).uploadedAt(Instant.now()).build()); }
    public Map<String,Object> dashboard(Long uid){
        List<BursaryApplication> apps = appRepo.findByStudentId(uid);
        return Map.of("applicationsByStatus", apps.stream().collect(java.util.stream.Collectors.groupingBy(BursaryApplication::getStatus, java.util.stream.Collectors.counting())),
                "savedOpportunities", apps.stream().filter(a->a.getStatus()==ApplicationStatus.SAVED).count(),
                "skillGaps", ai.skillGapAnalysis(uid));
    }
    public Page<Career> careers(String field, Pageable pageable){ return careerRepo.search(field, pageable); }
    public Page<Bursary> bursaries(Pageable pageable){ return bursaryRepo.byStatus(BursaryStatus.ACTIVE, pageable); }
    public Bursary bursaryDetail(Long uid, Long id){ Bursary b=bursaryRepo.findById(id).orElseThrow(); viewRepo.save(BursaryViewEvent.builder().bursaryId(id).studentId(uid).viewedAt(Instant.now()).build()); return b; }
    public BursaryApplication saveOrApply(Long uid, Long bursaryId, boolean submit){
        BursaryApplication a = appRepo.findByStudentIdAndBursaryId(uid, bursaryId).orElse(BursaryApplication.builder().studentId(uid).bursaryId(bursaryId).build());
        a.setStatus(submit?ApplicationStatus.SUBMITTED:ApplicationStatus.SAVED); if (submit) a.setSubmittedAt(Instant.now()); a.setUpdatedAt(Instant.now());
        return appRepo.save(a);
    }
    public List<BursaryApplication> applications(Long uid){ return appRepo.findByStudentId(uid); }
    public Map<String,Object> subStatus(Long uid){
        var sub=subRepo.findTopByStudentIdOrderByStartedAtDesc(uid).orElse(null);
        return Map.of("active", sub!=null && sub.getStatus()==SubscriptionStatus.ACTIVE, "plan", sub==null?"NONE":sub.getPlanId(), "status", sub==null?"NONE":sub.getStatus().name());
    }
    public void checkPremium(Long uid){
        var s=subRepo.findTopByStudentIdOrderByStartedAtDesc(uid).orElseThrow(()->new AppException(402,"Subscription required"));
        if (s.getPlanId()==1L) throw new AppException(402,"Upgrade to PREMIUM for this endpoint");
    }
    public List<String> aiCareers(Long uid){ return ai.recommendCareers(uid); }
    public List<String> aiBursaries(Long uid){ return ai.recommendBursaries(uid); }
    public String aiSkillGaps(Long uid){ return ai.skillGapAnalysis(uid); }
    public List<Notification> notifications(Long uid){ return notificationRepo.findByUserId(uid); }
}
