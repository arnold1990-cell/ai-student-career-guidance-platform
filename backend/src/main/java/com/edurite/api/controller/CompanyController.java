package com.edurite.api.controller;

import com.edurite.application.service.CompanyService;
import com.edurite.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController @RequiredArgsConstructor
public class CompanyController {
    private final CompanyService service;
    @GetMapping("/api/v1/companies/me/profile") public CompanyProfile profile(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid){ return service.profile(uid); }
    @PutMapping("/api/v1/companies/me/profile") public CompanyProfile save(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@RequestBody CompanyProfile p){ return service.saveProfile(uid,p); }
    @GetMapping("/api/v1/companies/me/dashboard") public Map<String,Object> dashboard(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid){ return service.dashboard(uid); }
    @PostMapping("/api/v1/companies/me/bursaries") public Bursary create(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@RequestBody Bursary b){ return service.createBursary(uid,b); }
    @PutMapping("/api/v1/companies/me/bursaries/{id}") public Bursary update(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@PathVariable Long id,@RequestBody Bursary b){ return service.updateBursary(uid,id,b); }
    @PostMapping("/api/v1/companies/me/bursaries/{id}/submit-for-approval") public Bursary submit(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@PathVariable Long id){ return service.submit(uid,id); }
    @PostMapping("/api/v1/companies/me/bursaries/{id}/close") public Bursary close(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@PathVariable Long id){ return service.close(uid,id); }
    @GetMapping("/api/v1/companies/me/bursaries") public List<Bursary> list(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid){ return service.myBursaries(uid); }
    @GetMapping("/api/v1/companies/me/bursaries/{id}/applications") public List<BursaryApplication> apps(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@PathVariable Long id){ return service.bursaryApplications(uid,id); }
    @GetMapping("/api/v1/companies/me/students/search") public List<StudentProfile> search(@RequestParam(required=false) String location){ return service.searchStudents(location); }
    @GetMapping("/api/v1/companies/me/ai/candidates") public List<String> ai(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@RequestParam Long bursaryId){ return service.aiCandidates(uid,bursaryId); }
    @PostMapping("/api/v1/companies/me/students/{studentId}/shortlist") public CompanyShortlist shortlist(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@PathVariable Long studentId){ return service.shortlist(uid,studentId); }
    @PostMapping("/api/v1/companies/me/messages/thread") public MessageThread thread(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@RequestParam Long studentId){ return service.createThread(uid,studentId); }
    @GetMapping("/api/v1/companies/me/messages/threads") public List<MessageThread> threads(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid){ return service.threads(uid); }
    @PostMapping("/api/v1/companies/me/messages/threads/{threadId}") public Message send(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@PathVariable Long threadId,@RequestBody Map<String,String> req){ return service.send(uid,threadId,req.get("body")); }
    @PostMapping("/api/v1/companies/me/invitations") public InvitationLink invite(@org.springframework.security.core.annotation.AuthenticationPrincipal Long uid,@RequestParam Long bursaryId){ return service.invite(uid,bursaryId); }
}
