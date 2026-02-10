package com.edurite.application.service;

import com.edurite.application.exception.AppException;
import com.edurite.domain.model.*;
import com.edurite.domain.model.Enums.*;
import com.edurite.domain.repo.*;
import com.edurite.integrations.AiRecommendationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class CompanyService {
    private final CompanyProfileRepository profileRepo; private final BursaryRepository bursaryRepo; private final BursaryApplicationRepository appRepo;
    private final CompanyShortlistRepository shortlistRepo; private final MessageThreadRepository threadRepo; private final MessageRepository messageRepo;
    private final InvitationLinkRepository invitationRepo; private final StudentProfileRepository studentRepo; private final AiRecommendationEngine ai; private final BursaryViewEventRepository viewRepo;

    public CompanyProfile profile(Long uid){ return profileRepo.findById(uid).orElseThrow(); }
    public CompanyProfile saveProfile(Long uid, CompanyProfile p){ p.setUserId(uid); return profileRepo.save(p); }
    private void requireApproved(Long uid){ if (profile(uid).getVerificationStatus()!=VerificationStatus.APPROVED) throw new AppException(403,"Company not approved"); }
    public Bursary createBursary(Long uid, Bursary b){ requireApproved(uid); b.setCompanyId(uid); b.setStatus(BursaryStatus.DRAFT); b.setCreatedAt(Instant.now()); b.setUpdatedAt(Instant.now()); return bursaryRepo.save(b); }
    public Bursary updateBursary(Long uid, Long id, Bursary req){ Bursary b=bursaryRepo.findById(id).orElseThrow(); if(!Objects.equals(b.getCompanyId(),uid)) throw new AppException(403,"Forbidden"); req.setId(id); req.setCompanyId(uid); req.setUpdatedAt(Instant.now()); return bursaryRepo.save(req); }
    public Bursary submit(Long uid, Long id){ Bursary b=bursaryRepo.findById(id).orElseThrow(); if(!Objects.equals(b.getCompanyId(),uid)) throw new AppException(403,"Forbidden"); b.setStatus(BursaryStatus.PENDING_APPROVAL); b.setUpdatedAt(Instant.now()); return bursaryRepo.save(b); }
    public Bursary close(Long uid, Long id){ Bursary b=bursaryRepo.findById(id).orElseThrow(); if(!Objects.equals(b.getCompanyId(),uid)) throw new AppException(403,"Forbidden"); b.setStatus(BursaryStatus.CLOSED); return bursaryRepo.save(b); }
    public List<Bursary> myBursaries(Long uid){ return bursaryRepo.findByCompanyId(uid); }
    public List<BursaryApplication> bursaryApplications(Long uid, Long id){ var b=bursaryRepo.findById(id).orElseThrow(); if(!Objects.equals(b.getCompanyId(),uid)) throw new AppException(403,"Forbidden"); return appRepo.findByBursaryId(id); }
    public List<StudentProfile> searchStudents(String location){ return studentRepo.findAll().stream().filter(s->location==null || location.equalsIgnoreCase(s.getLocation())).toList(); }
    public List<String> aiCandidates(Long uid, Long bursaryId){ return ai.recommendCandidates(uid, bursaryId); }
    public CompanyShortlist shortlist(Long uid, Long studentId){ return shortlistRepo.save(CompanyShortlist.builder().companyId(uid).studentId(studentId).build()); }
    public MessageThread createThread(Long uid, Long studentId){ return threadRepo.save(MessageThread.builder().companyId(uid).studentId(studentId).createdAt(Instant.now()).build()); }
    public List<MessageThread> threads(Long uid){ return threadRepo.findByCompanyId(uid); }
    public Message send(Long uid, Long threadId, String body){ return messageRepo.save(Message.builder().threadId(threadId).senderUserId(uid).body(body).createdAt(Instant.now()).build()); }
    public InvitationLink invite(Long uid, Long bursaryId){ return invitationRepo.save(InvitationLink.builder().companyId(uid).bursaryId(bursaryId).token(UUID.randomUUID().toString()).createdAt(Instant.now()).build()); }
    public Map<String,Object> dashboard(Long uid){
        var bursaries=bursaryRepo.findByCompanyId(uid);
        long applicants=bursaries.stream().mapToLong(b->appRepo.findByBursaryId(b.getId()).size()).sum();
        long views=bursaries.stream().mapToLong(b->viewRepo.countByBursaryId(b.getId())).sum();
        long started=bursaries.stream().flatMap(b->appRepo.findByBursaryId(b.getId()).stream()).filter(a->a.getStatus()==ApplicationStatus.SAVED||a.getStatus()==ApplicationStatus.SUBMITTED).count();
        long completed=bursaries.stream().flatMap(b->appRepo.findByBursaryId(b.getId()).stream()).filter(a->a.getStatus()==ApplicationStatus.SUBMITTED).count();
        return Map.of("bursaries", bursaries.size(), "applicants", applicants, "views", views, "completionRate", started==0?0:((double)completed/started));
    }
}
