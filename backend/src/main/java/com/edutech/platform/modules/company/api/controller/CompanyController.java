package com.edutech.platform.modules.company.api.controller;

import com.edutech.platform.modules.company.application.service.CompanyService;
import java.util.LinkedHashMap;
import java.util.Map;
import com.edutech.platform.shared.api.ApiResponse;
import com.edutech.platform.shared.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;
    public CompanyController(CompanyService companyService) { this.companyService = companyService; }

    @PostMapping("/onboard")
    public ApiResponse<Map<String, Object>> onboard(@RequestParam String companyName, Authentication auth, HttpServletRequest req) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        com.edutech.platform.modules.company.domain.entity.CompanyProfile saved = companyService.register(user.getUserId(), companyName);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", saved.getId());
        data.put("companyName", saved.getCompanyName());
        data.put("status", saved.getStatus());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Company onboarded", data);
    }

    @GetMapping("/verification-queue") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> queue(HttpServletRequest req) {
        java.util.List<com.edutech.platform.modules.company.domain.entity.VerificationRequest> queue = companyService.pendingQueue();
        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        int i = 0;
        while (i < queue.size()) {
            com.edutech.platform.modules.company.domain.entity.VerificationRequest r = queue.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("status", r.getStatus());
            item.put("companyId", r.getCompanyProfile().getId());
            items.add(item);
            i++;
        }
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Queue", items);
    }

    @PostMapping("/verification-queue/{id}/decision") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> approve(@PathVariable Long id, @RequestParam String decision, HttpServletRequest req) {
        com.edutech.platform.modules.company.domain.entity.VerificationRequest saved = companyService.approve(id, decision);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", saved.getId());
        data.put("status", saved.getStatus());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Decision saved", data);
    }
}
