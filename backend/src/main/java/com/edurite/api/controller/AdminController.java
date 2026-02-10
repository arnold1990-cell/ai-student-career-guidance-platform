package com.edurite.api.controller;

import com.edurite.application.service.AdminService;
import com.edurite.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController @RequiredArgsConstructor
public class AdminController {
    private final AdminService service;
    @GetMapping("/api/v1/admin/users") public Page<User> users(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return service.users(PageRequest.of(page,size)); }
    @PostMapping("/api/v1/admin/users") public User createUser(@RequestBody User u){ return service.createUser(u); }
    @PutMapping("/api/v1/admin/users") public User updateUser(@RequestParam Long id,@RequestBody User u){ return service.updateUser(id,u); }
    @DeleteMapping("/api/v1/admin/users") public void delete(@RequestParam Long id){ service.deleteUser(id); }
    @PostMapping("/api/v1/admin/users/bulk-upload") public Map<String,Integer> bulk(@RequestParam MultipartFile file) throws IOException { return Map.of("created", service.bulkUpload(file)); }
    @GetMapping("/api/v1/admin/audit-logs") public Page<AuditLog> audit(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return service.audit(PageRequest.of(page,size)); }
    @GetMapping("/api/v1/admin/companies/pending") public List<CompanyProfile> pendingCompanies(){ return service.pendingCompanies(); }
    @PostMapping("/api/v1/admin/companies/{companyId}/approve") public CompanyProfile approveCompany(@PathVariable Long companyId){ return service.approveCompany(companyId); }
    @PostMapping("/api/v1/admin/companies/{companyId}/reject") public CompanyProfile rejectCompany(@PathVariable Long companyId,@RequestBody(required=false) Map<String,String> req){ return service.rejectCompany(companyId, req==null?null:req.get("notes")); }
    @GetMapping("/api/v1/admin/bursaries/pending") public Page<Bursary> pendingBursaries(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return service.pendingBursaries(PageRequest.of(page,size)); }
    @PostMapping("/api/v1/admin/bursaries/{id}/approve") public Bursary approveB(@PathVariable Long id){ return service.approveBursary(id); }
    @PostMapping("/api/v1/admin/bursaries/{id}/reject") public Bursary rejectB(@PathVariable Long id){ return service.rejectBursary(id); }
    @GetMapping("/api/v1/admin/subscriptions") public Page<StudentSubscription> subs(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return service.subscriptions(PageRequest.of(page,size)); }
    @GetMapping("/api/v1/admin/payments") public Page<PaymentTransaction> pays(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return service.payments(PageRequest.of(page,size)); }
    @GetMapping("/api/v1/admin/reports/monthly") public Map<String,Object> report(){ return service.monthlyReport(); }
    @GetMapping("/api/v1/admin/templates") public List<Template> templates(){ return service.templates(); }
    @PostMapping("/api/v1/admin/templates") public Template createTemplate(@RequestBody Template t){ return service.createTemplate(t); }
    @PutMapping("/api/v1/admin/templates") public Template updateTemplate(@RequestParam Long id, @RequestBody Template t){ return service.updateTemplate(id,t); }
    @DeleteMapping("/api/v1/admin/templates") public void deleteTemplate(@RequestParam Long id){ service.deleteTemplate(id); }
}
