package com.edutech.platform.modules.student.api.controller;

import com.edutech.platform.modules.student.api.dto.*;
import com.edutech.platform.modules.student.application.service.StudentProfileService;
import com.edutech.platform.shared.api.ApiResponse;
import com.edutech.platform.shared.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students/profile")
public class StudentProfileController {
    private final StudentProfileService service;

    public StudentProfileController(StudentProfileService service) {
        this.service = service;
    }

    @PutMapping
    public ApiResponse<StudentProfileResponse> upsert(@Valid @RequestBody StudentProfileRequest request, Authentication authentication, HttpServletRequest http) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return new ApiResponse<>(Instant.now(), http.getRequestURI(), "Profile saved", service.upsert(user.getUserId(), request));
    }

    @GetMapping
    public ApiResponse<StudentProfileResponse> get(Authentication authentication, HttpServletRequest http) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return new ApiResponse<>(Instant.now(), http.getRequestURI(), "Profile", service.getByUser(user.getUserId()));
    }

    @PostMapping("/documents")
    public ApiResponse<String> uploadMeta(@Valid @RequestBody DocumentRequest request, Authentication authentication, HttpServletRequest http) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return new ApiResponse<>(Instant.now(), http.getRequestURI(), "Upload URL", service.addDocument(user.getUserId(), request));
    }

    @GetMapping("/documents")
    public ApiResponse<List<String>> listDocs(Authentication authentication, HttpServletRequest http) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return new ApiResponse<>(Instant.now(), http.getRequestURI(), "Documents", service.listDocuments(user.getUserId()));
    }
}
