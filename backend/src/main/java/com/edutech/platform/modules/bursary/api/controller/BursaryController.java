package com.edutech.platform.modules.bursary.api.controller;

import com.edutech.platform.modules.bursary.application.service.BursaryService;
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
@RequestMapping("/api/v1/bursaries")
public class BursaryController {
    private final BursaryService bursaryService;
    public BursaryController(BursaryService bursaryService) { this.bursaryService = bursaryService; }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public ApiResponse<Map<String, Object>> create(@RequestParam String title, @RequestParam String description, Authentication auth, HttpServletRequest req) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        com.edutech.platform.modules.bursary.domain.entity.Bursary b = bursaryService.create(user.getUserId(), title, description);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", b.getId());
        data.put("title", b.getTitle());
        data.put("status", b.getApprovalStatus());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Bursary created", data);
    }

    @PostMapping("/{id}/approve") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> approve(@PathVariable Long id, @RequestParam String status, HttpServletRequest req) {
        com.edutech.platform.modules.bursary.domain.entity.Bursary b = bursaryService.approve(id, status);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", b.getId());
        data.put("status", b.getApprovalStatus());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Bursary reviewed", data);
    }

    @GetMapping("/search")
    public ApiResponse<List<Map<String, Object>>> search(@RequestParam(required = false) String q, HttpServletRequest req) {
        java.util.List<com.edutech.platform.modules.bursary.domain.entity.Bursary> list = bursaryService.search(q);
        java.util.List<Map<String, Object>> data = new java.util.ArrayList<>();
        int i = 0;
        while (i < list.size()) {
            com.edutech.platform.modules.bursary.domain.entity.Bursary b = list.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", b.getId());
            item.put("title", b.getTitle());
            item.put("description", b.getDescription());
            data.add(item);
            i++;
        }
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Bursaries", data);
    }

    @PostMapping("/{id}/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Map<String, Object>> apply(@PathVariable Long id, Authentication auth, HttpServletRequest req) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        com.edutech.platform.modules.bursary.domain.entity.BursaryApplication app = bursaryService.apply(user.getUserId(), id);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("applicationId", app.getId());
        data.put("status", app.getStatus());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Applied", data);
    }
}
