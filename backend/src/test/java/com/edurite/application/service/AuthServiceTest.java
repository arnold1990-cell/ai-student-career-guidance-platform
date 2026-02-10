package com.edurite.application.service;

import com.edurite.api.dto.AuthDtos.LoginRequest;
import com.edurite.application.exception.AppException;
import com.edurite.domain.model.Enums;
import com.edurite.domain.model.User;
import com.edurite.domain.repo.*;
import com.edurite.integrations.*;
import com.edurite.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository; @Mock StudentProfileRepository studentProfileRepository; @Mock CompanyProfileRepository companyProfileRepository;
    @Mock PasswordEncoder passwordEncoder; @Mock JwtService jwtService; @Mock PasswordResetTokenRepository resetRepo; @Mock EmailGateway emailGateway; @Mock SmsGateway smsGateway;

    @Test
    void loginRejectsInvalidPassword(){
        User u=User.builder().id(1L).email("a@a.com").passwordHash("hash").role(Enums.Role.STUDENT).build();
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("bad","hash")).thenReturn(false);
        AuthService service = new AuthService(userRepository, studentProfileRepository, companyProfileRepository, passwordEncoder, jwtService, resetRepo, emailGateway, smsGateway);
        assertThrows(AppException.class, () -> service.login(new LoginRequest("a@a.com","bad")));
    }
}
