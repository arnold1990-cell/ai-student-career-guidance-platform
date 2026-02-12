package com.edutech.platform.modules.iam.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edutech.platform.modules.iam.api.dto.AuthMeResponse;
import com.edutech.platform.modules.iam.api.dto.TokenResponse;
import com.edutech.platform.modules.iam.application.service.AuthService;
import com.edutech.platform.shared.config.SecurityConfig;
import com.edutech.platform.shared.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private UserDetailsService userDetailsService;

    @Test
    void register_returnsTokenPayload() throws Exception {
        when(authService.register(any())).thenReturn(new TokenResponse("access", "refresh"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "email", "student@mail.com",
                                "password", "password",
                                "fullName", "Student",
                                "role", "STUDENT"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registered"))
                .andExpect(jsonPath("$.data.accessToken").value("access"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh"));
    }

    @Test
    void me_withoutPrincipal_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withPrincipal_returnsCurrentUser() throws Exception {
        when(authService.me("student@mail.com"))
                .thenReturn(new AuthMeResponse(10L, "student@mail.com", "Student", "STUDENT"));

        mockMvc.perform(get("/api/v1/auth/me")
                        .with(user(User.withUsername("student@mail.com").password("pw").roles("STUDENT").build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Current user"))
                .andExpect(jsonPath("$.data.email").value("student@mail.com"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));

        verify(authService).me("student@mail.com");
    }
}
