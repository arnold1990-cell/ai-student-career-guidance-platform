package com.edutech.platform.modules.iam.api.controller;

import com.edutech.platform.modules.iam.api.dto.*;
import com.edutech.platform.modules.iam.application.service.AuthService;
import com.edutech.platform.shared.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpServletRequest) {
        return new ApiResponse<>(Instant.now(), httpServletRequest.getRequestURI(), "Registered", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        return new ApiResponse<>(Instant.now(), httpServletRequest.getRequestURI(), "Logged in", authService.login(request, ip));
    }

    @GetMapping("/me")
    public ApiResponse<AuthMeResponse> me(Authentication authentication, HttpServletRequest request) {
        return new ApiResponse<>(
                Instant.now(),
                request.getRequestURI(),
                "Current user",
                authService.me(authentication.getName())
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request, HttpServletRequest servletRequest) {
        return new ApiResponse<>(Instant.now(), servletRequest.getRequestURI(), "Refreshed", authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody RefreshRequest request, HttpServletRequest servletRequest) {
        authService.logout(request);
        return new ApiResponse<>(Instant.now(), servletRequest.getRequestURI(), "Logged out", "OK");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgot(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest servletRequest) {
        String token = authService.forgotPassword(request);
        return new ApiResponse<>(Instant.now(), servletRequest.getRequestURI(), "Reset token generated", token);
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> reset(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest servletRequest) {
        authService.resetPassword(request);
        return new ApiResponse<>(Instant.now(), servletRequest.getRequestURI(), "Password changed", "OK");
    }
}
