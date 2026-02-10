package com.edurite.api.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {
    public record RegisterStudentRequest(@Email String email, @NotBlank String password, String phone){}
    public record RegisterCompanyRequest(@Email String email, @NotBlank String password, String phone, @NotBlank String companyName, @NotBlank String registrationNumber){}
    public record LoginRequest(@Email String email, @NotBlank String password){}
    public record TokenResponse(String accessToken, String refreshToken){}
    public record RefreshRequest(@NotBlank String refreshToken){}
    public record ForgotPasswordRequest(@Email String email){}
    public record ResetPasswordRequest(@NotBlank String token, @NotBlank String newPassword){}
}
