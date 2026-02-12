package com.edutech.platform.modules.iam.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.edutech.platform.modules.iam.api.dto.*;
import com.edutech.platform.modules.iam.domain.entity.*;
import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.*;
import com.edutech.platform.shared.exception.ApiException;
import com.edutech.platform.shared.security.JwtService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordResetTokenRepository resetTokenRepository;
    @Mock private AuditLoginRepository auditLoginRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private Role studentRole;

    @BeforeEach
    void setUp() {
        studentRole = new Role();
        studentRole.setId(1L);
        studentRole.setName("STUDENT");
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("  USER@MAIL.com ");
        request.setPassword("pw");
        request.setFullName("User");
        request.setRole("student");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode("pw")).thenReturn("ENC");
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access-token");

        TokenResponse response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isNotBlank();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo("user@mail.com");
        assertThat(saved.getPasswordHash()).isEqualTo("ENC");
        assertThat(saved.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(saved.getRole().getName()).isEqualTo("STUDENT");
    }

    @Test
    void login_withValidCredentials_returnsTokensAndAuditsSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("STUDENT@MAIL.COM");
        request.setPassword("pw");

        Authentication authentication = new UsernamePasswordAuthenticationToken("student@mail.com", "pw");
        User user = new User();
        user.setId(5L);
        user.setEmail("student@mail.com");
        user.setStatus(AccountStatus.ACTIVE);
        user.setRole(studentRole);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail("student@mail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(5L, "student@mail.com", "STUDENT")).thenReturn("jwt");

        TokenResponse response = authService.login(request, "127.0.0.1");

        assertThat(response.getAccessToken()).isEqualTo("jwt");
        assertThat(response.getRefreshToken()).isNotBlank();
        verify(auditLoginRepository).save(argThat(a -> a.isSuccess() && "127.0.0.1".equals(a.getIpAddress())));
    }

    @Test
    void login_withBadCredentials_throwsApiExceptionAndAuditsFailure() {
        LoginRequest request = new LoginRequest();
        request.setEmail("STUDENT@MAIL.COM");
        request.setPassword("bad");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(request, "1.1.1.1"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid credentials");

        verify(auditLoginRepository).save(argThat(a -> !a.isSuccess() && "student@mail.com".equals(a.getEmail())));
    }

    @Test
    void refresh_withValidToken_rotatesRefreshToken() {
        User user = new User();
        user.setId(9L);
        user.setEmail("u@mail.com");
        user.setRole(studentRole);

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("old");

        when(userRepository.findByRefreshToken("old")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(9L, "u@mail.com", "STUDENT")).thenReturn("new-access");

        TokenResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isNotEqualTo("old");
        verify(userRepository).save(user);
    }

    @Test
    void logout_nullsMatchingRefreshTokenOnly() {
        User matching = new User();
        matching.setRefreshToken("target");
        User other = new User();
        other.setRefreshToken("other");

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("target");

        when(userRepository.findAll()).thenReturn(List.of(other, matching));

        authService.logout(request);

        assertThat(matching.getRefreshToken()).isNull();
        verify(userRepository).save(matching);
        verify(userRepository, never()).save(other);
    }

    @Test
    void forgotPassword_persistsResetToken() {
        User user = new User();
        user.setEmail("student@mail.com");
        when(userRepository.findByEmail("student@mail.com")).thenReturn(Optional.of(user));

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(" STUDENT@MAIL.com ");

        String token = authService.forgotPassword(request);

        assertThat(token).isNotBlank();
        verify(resetTokenRepository).save(argThat(t -> t.getUser() == user && !t.isUsed() && t.getExpiresAt().isAfter(Instant.now())));
    }

    @Test
    void resetPassword_updatesPasswordAndMarksTokenUsed() {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("reset-token");
        token.setUser(user);
        token.setUsed(false);
        token.setExpiresAt(Instant.now().plusSeconds(60));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token");
        request.setNewPassword("newPw");

        when(resetTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPw")).thenReturn("ENC_NEW");

        authService.resetPassword(request);

        assertThat(user.getPasswordHash()).isEqualTo("ENC_NEW");
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(resetTokenRepository).save(token);
    }
}
