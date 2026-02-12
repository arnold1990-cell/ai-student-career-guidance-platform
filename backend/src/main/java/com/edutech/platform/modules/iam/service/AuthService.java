package com.edutech.platform.modules.iam.service;

import com.edutech.platform.modules.iam.domain.User;
import com.edutech.platform.modules.iam.domain.UserRole;
import com.edutech.platform.modules.iam.domain.UserStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.shared.security.JwtService;
import com.edutech.platform.shared.security.LoginAttemptService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserRole role = request.getRole();
        if (role != null && role != UserRole.STUDENT) {
            throw new IllegalArgumentException("Self-registration only supports STUDENT role");
        }
        role = UserRole.STUDENT;

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request, String clientIp) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String loginKey = normalizedEmail + "|" + (clientIp == null ? "unknown" : clientIp);

        if (loginAttemptService.isBlocked(loginKey)) {
            long retryAfter = loginAttemptService.getRetryAfterSeconds(loginKey);
            throw new BadCredentialsException("Too many failed login attempts. Retry in " + retryAfter + " seconds");
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
            );
        } catch (Exception ex) {
            loginAttemptService.recordFailure(loginKey);
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getStatus() == UserStatus.DISABLED) {
            loginAttemptService.recordFailure(loginKey);
            throw new BadCredentialsException("User account disabled");
        }

        loginAttemptService.recordSuccess(loginKey);

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        UserSummary userSummary = new UserSummary(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, "Bearer", jwtService.getAccessExpirySeconds(), userSummary);
    }
}
