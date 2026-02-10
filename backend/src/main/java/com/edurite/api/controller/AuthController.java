package com.edurite.api.controller;

import com.edurite.api.dto.AuthDtos.*;
import com.edurite.application.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService; private final RateLimitService rateLimitService;
    @PostMapping("/register/student") public void registerStudent(@Valid @RequestBody RegisterStudentRequest req){ authService.registerStudent(req); }
    @PostMapping("/register/company") public void registerCompany(@Valid @RequestBody RegisterCompanyRequest req){ authService.registerCompany(req); }
    @PostMapping("/login") public TokenResponse login(@Valid @RequestBody LoginRequest req){ rateLimitService.check("login:"+req.email(), 10, 60); return authService.login(req); }
    @PostMapping("/refresh") public TokenResponse refresh(@Valid @RequestBody RefreshRequest req){ return authService.refresh(req.refreshToken()); }
    @PostMapping("/forgot-password") public void forgot(@Valid @RequestBody ForgotPasswordRequest req){ rateLimitService.check("forgot:"+req.email(), 5, 60); authService.forgotPassword(req.email()); }
    @PostMapping("/reset-password") public void reset(@Valid @RequestBody ResetPasswordRequest req){ authService.resetPassword(req.token(), req.newPassword()); }
}
