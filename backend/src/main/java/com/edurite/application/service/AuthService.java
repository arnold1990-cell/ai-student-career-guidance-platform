package com.edurite.application.service;

import com.edurite.api.dto.AuthDtos.*;
import com.edurite.application.exception.AppException;
import com.edurite.domain.model.*;
import com.edurite.domain.model.Enums.*;
import com.edurite.domain.repo.*;
import com.edurite.integrations.*;
import com.edurite.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo; private final StudentProfileRepository studentRepo; private final CompanyProfileRepository companyRepo;
    private final PasswordEncoder encoder; private final JwtService jwtService; private final PasswordResetTokenRepository resetRepo; private final EmailGateway emailGateway; private final SmsGateway smsGateway;

    public void registerStudent(RegisterStudentRequest req){
        if (userRepo.findByEmail(req.email()).isPresent()) throw new AppException(409, "Email exists");
        User u = userRepo.save(User.builder().email(req.email()).passwordHash(encoder.encode(req.password())).phone(req.phone()).role(Role.STUDENT).status(UserStatus.ACTIVE).createdAt(Instant.now()).build());
        studentRepo.save(StudentProfile.builder().user(u).preferences("{}").build());
    }
    public void registerCompany(RegisterCompanyRequest req){
        if (userRepo.findByEmail(req.email()).isPresent()) throw new AppException(409, "Email exists");
        User u = userRepo.save(User.builder().email(req.email()).passwordHash(encoder.encode(req.password())).phone(req.phone()).role(Role.COMPANY).status(UserStatus.ACTIVE).createdAt(Instant.now()).build());
        companyRepo.save(CompanyProfile.builder().user(u).companyName(req.companyName()).registrationNumber(req.registrationNumber()).officialEmail(req.email()).verificationStatus(VerificationStatus.PENDING).submittedAt(Instant.now()).build());
    }
    public TokenResponse login(LoginRequest req){
        User u = userRepo.findByEmail(req.email()).orElseThrow(()->new AppException(401,"Invalid credentials"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) throw new AppException(401, "Invalid credentials");
        u.setLastLoginAt(Instant.now()); userRepo.save(u);
        return new TokenResponse(jwtService.token(u.getEmail(), u.getRole().name(), false), jwtService.token(u.getEmail(), u.getRole().name(), true));
    }
    public TokenResponse refresh(String refreshToken){
        var claims = jwtService.parse(refreshToken).getPayload();
        if (!"refresh".equals(claims.get("type"))) throw new AppException(401, "Invalid refresh token");
        User u = userRepo.findByEmail(claims.getSubject()).orElseThrow(()->new AppException(401,"Invalid token"));
        return new TokenResponse(jwtService.token(u.getEmail(), u.getRole().name(), false), refreshToken);
    }
    public void forgotPassword(String email){
        User u = userRepo.findByEmail(email).orElseThrow(()->new AppException(404,"User not found"));
        String raw = java.util.UUID.randomUUID().toString();
        resetRepo.save(PasswordResetToken.builder().userId(u.getId()).tokenHash(hash(raw)).expiresAt(Instant.now().plusSeconds(1800)).used(false).build());
        String link = "https://dev.edurite/reset?token="+raw;
        emailGateway.sendEmail(email,"Reset Password","Use this link: "+link);
        if (u.getPhone()!=null) smsGateway.sendSms(u.getPhone(),"Reset link: "+link);
    }
    public void resetPassword(String token, String newPassword){
        PasswordResetToken t = resetRepo.findByTokenHashAndUsedFalse(hash(token)).orElseThrow(()->new AppException(400,"Invalid token"));
        if (t.getExpiresAt().isBefore(Instant.now())) throw new AppException(400,"Expired token");
        User u = userRepo.findById(t.getUserId()).orElseThrow(); u.setPasswordHash(encoder.encode(newPassword)); userRepo.save(u);
        t.setUsed(true); resetRepo.save(t);
    }
    private String hash(String val){
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(val.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e){ throw new RuntimeException(e); }
    }
}
