package com.edutech.platform.modules.iam.application.service;

import com.edutech.platform.modules.iam.api.dto.*;
import com.edutech.platform.modules.iam.domain.entity.*;
import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.*;
import com.edutech.platform.shared.exception.ApiException;
import com.edutech.platform.shared.security.JwtService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final AuditLoginRepository auditLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordResetTokenRepository resetTokenRepository, AuditLoginRepository auditLoginRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.auditLoginRepository = auditLoginRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("User already exists");
        }
        Role role = roleRepository.findByName(request.getRole().toUpperCase()).orElseThrow(() -> new ApiException("Role not found"));
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(AccountStatus.ACTIVE);
        user.setRole(role);
        String refresh = UUID.randomUUID().toString();
        user.setRefreshToken(refresh);
        userRepository.save(user);
        String access = jwtService.generateAccessToken(user.getEmail(), role.getName());
        return new TokenResponse(access, refresh);
    }

    public TokenResponse login(LoginRequest request, String ipAddress) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            auditLogin(request.getEmail(), false, ipAddress);
            throw new ApiException("Invalid credentials");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            auditLogin(request.getEmail(), false, ipAddress);
            throw new ApiException("Invalid credentials");
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException("Account is not active");
        }
        String refresh = UUID.randomUUID().toString();
        user.setRefreshToken(refresh);
        userRepository.save(user);
        auditLogin(request.getEmail(), true, ipAddress);
        return new TokenResponse(jwtService.generateAccessToken(user.getEmail(), user.getRole().getName()), refresh);
    }

    public TokenResponse refresh(RefreshRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new ApiException("Invalid refresh token"));
        String refresh = UUID.randomUUID().toString();
        user.setRefreshToken(refresh);
        userRepository.save(user);
        return new TokenResponse(jwtService.generateAccessToken(user.getEmail(), user.getRole().getName()), refresh);
    }

    public void logout(RefreshRequest request) {
        java.util.List<User> users = userRepository.findAll();
        int i = 0;
        while (i < users.size()) {
            User user = users.get(i);
            if (request.getRefreshToken().equals(user.getRefreshToken())) {
                user.setRefreshToken(null);
                userRepository.save(user);
                return;
            }
            i++;
        }
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ApiException("User not found"));
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setUsed(false);
        token.setExpiresAt(Instant.now().plusSeconds(900));
        resetTokenRepository.save(token);
        return token.getToken();
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = resetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ApiException("Token not found"));
        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("Token invalid or expired");
        }
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsed(true);
        resetTokenRepository.save(token);
    }

    private void auditLogin(String email, boolean success, String ipAddress) {
        AuditLogin audit = new AuditLogin();
        audit.setEmail(email);
        audit.setSuccess(success);
        audit.setIpAddress(ipAddress);
        auditLoginRepository.save(audit);
    }
}
