package com.edutech.platform.modules.iam.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edutech.platform.modules.iam.domain.entity.Role;
import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.RoleRepository;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.shared.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AuthSecurityIntegrationTest.TestProtectedEndpoints.class)
class AuthSecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        ensureRole("ADMIN");
        ensureRole("STUDENT");
        ensureRole("COMPANY");
    }

    @Test
    void register_returns200AndTokenStructure() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "email", "newstudent@mail.com",
                                "password", "password123",
                                "fullName", "New Student",
                                "role", "STUDENT"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.refreshToken").isString());
    }

    @Test
    void login_withValidCredentials_returns200AndToken() throws Exception {
        createUser("student@mail.com", "password123", "STUDENT");

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "email", "student@mail.com",
                                "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        String accessToken = jsonNode.path("data").path("accessToken").asText();
        org.assertj.core.api.Assertions.assertThat(accessToken).isNotBlank();
    }

    @Test
    void me_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withValidToken_returns200UserInfo() throws Exception {
        User user = createUser("student@mail.com", "password123", "STUDENT");
        String token = jwtService.generateAccessToken(user.getId(), user.getEmail(), "STUDENT");

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("student@mail.com"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    void authorization_rules_enforceRoleAccess() throws Exception {
        User admin = createUser("admin@mail.com", "password123", "ADMIN");
        User student = createUser("student@mail.com", "password123", "STUDENT");
        User company = createUser("company@mail.com", "password123", "COMPANY");

        String adminToken = jwtService.generateAccessToken(admin.getId(), admin.getEmail(), "ADMIN");
        String studentToken = jwtService.generateAccessToken(student.getId(), student.getEmail(), "STUDENT");
        String companyToken = jwtService.generateAccessToken(company.getId(), company.getEmail(), "COMPANY");

        mockMvc.perform(get("/api/v1/admin/ping").header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/ping").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("admin-ok"));

        mockMvc.perform(get("/api/v1/students/ping").header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("student-ok"));

        mockMvc.perform(get("/api/v1/companies/ping").header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("company-ok"));
    }

    @Test
    void corsPreflightOptions_isPermitted() throws Exception {
        mockMvc.perform(options("/api/v1/auth/me")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }

    private User createUser(String email, String password, String roleName) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(AccountStatus.ACTIVE);
        user.setRole(ensureRole(roleName));
        return userRepository.save(user);
    }

    @Configuration
    static class TestProtectedEndpoints {
        @RestController
        @RequestMapping("/api/v1")
        static class ProtectedController {
            @GetMapping("/admin/ping")
            public java.util.Map<String, String> admin() { return java.util.Map.of("status", "admin-ok"); }
            @GetMapping("/students/ping")
            public java.util.Map<String, String> student() { return java.util.Map.of("status", "student-ok"); }
            @GetMapping("/companies/ping")
            public java.util.Map<String, String> company() { return java.util.Map.of("status", "company-ok"); }
        }
    }
}
