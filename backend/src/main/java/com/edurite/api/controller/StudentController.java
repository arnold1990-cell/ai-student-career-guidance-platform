package com.edurite.api.controller;

import com.edurite.application.service.*;
import com.edurite.domain.model.*;
import com.edurite.domain.model.Enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController @RequiredArgsConstructor
public class StudentController {
    private final StudentService service; private final FileStorageService fileStorageService;

    @GetMapping("/api/v1/students/me/profile") public StudentProfile profile(@AuthenticationPrincipal Long uid){ return service.profile(uid); }
    @PutMapping("/api/v1/students/me/profile") public StudentProfile save(@AuthenticationPrincipal Long uid, @RequestBody StudentProfile p){ return service.saveProfile(uid,p); }
    @PostMapping("/api/v1/students/me/upload/cv") public Document uploadCv(@AuthenticationPrincipal Long uid, @RequestParam MultipartFile file){ String path=fileStorageService.store(file); return service.saveDoc(uid, DocumentType.CV, file.getOriginalFilename(), file.getContentType(), file.getSize(), path); }
    @PostMapping("/api/v1/students/me/upload/transcript") public Document uploadTranscript(@AuthenticationPrincipal Long uid, @RequestParam MultipartFile file){ String path=fileStorageService.store(file); return service.saveDoc(uid, DocumentType.TRANSCRIPT, file.getOriginalFilename(), file.getContentType(), file.getSize(), path); }
    @GetMapping("/api/v1/students/me/dashboard") public Map<String,Object> dashboard(@AuthenticationPrincipal Long uid){ return service.dashboard(uid); }
    @GetMapping("/api/v1/careers") public Page<Career> careers(@RequestParam(required=false) String field, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size){ return service.careers(field, PageRequest.of(page,size)); }
    @GetMapping("/api/v1/students/me/ai/careers") public List<String> aiCareers(@AuthenticationPrincipal Long uid){ service.checkPremium(uid); return service.aiCareers(uid); }
    @GetMapping("/api/v1/bursaries") public Page<Bursary> bursaries(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size){ return service.bursaries(PageRequest.of(page,size)); }
    @GetMapping("/api/v1/bursaries/{id}") public Bursary bursaryDetail(@AuthenticationPrincipal Long uid,@PathVariable Long id){ return service.bursaryDetail(uid,id); }
    @PostMapping("/api/v1/students/me/bursaries/{id}/save") public BursaryApplication saveB(@AuthenticationPrincipal Long uid,@PathVariable Long id){ return service.saveOrApply(uid,id,false); }
    @PostMapping("/api/v1/students/me/bursaries/{id}/apply") public BursaryApplication apply(@AuthenticationPrincipal Long uid,@PathVariable Long id){ return service.saveOrApply(uid,id,true); }
    @GetMapping("/api/v1/students/me/applications") public List<BursaryApplication> apps(@AuthenticationPrincipal Long uid){ return service.applications(uid); }
    @GetMapping("/api/v1/students/me/ai/bursaries") public List<String> aiB(@AuthenticationPrincipal Long uid){ service.checkPremium(uid); return service.aiBursaries(uid); }
    @GetMapping("/api/v1/students/me/ai/skill-gaps") public String gaps(@AuthenticationPrincipal Long uid){ service.checkPremium(uid); return service.aiSkillGaps(uid); }
    @GetMapping("/api/v1/students/me/subscription/status") public Map<String,Object> sub(@AuthenticationPrincipal Long uid){ return service.subStatus(uid); }
    @GetMapping("/api/v1/students/me/notifications") public List<Notification> notifications(@AuthenticationPrincipal Long uid){ return service.notifications(uid); }
}
