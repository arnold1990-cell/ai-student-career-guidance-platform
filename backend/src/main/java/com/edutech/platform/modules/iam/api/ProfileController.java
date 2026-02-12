package com.edutech.platform.modules.iam.api;

import com.edutech.platform.modules.iam.domain.User;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.modules.iam.service.UserSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Authenticated user");
        response.put("user", new UserSummary(user.getId(), user.getEmail(), user.getRole()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/admin/ping")
    public ResponseEntity<Map<String, String>> adminPing() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "ADMIN pong");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/student/ping")
    public ResponseEntity<Map<String, String>> studentPing() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "STUDENT pong");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/company/ping")
    public ResponseEntity<Map<String, String>> companyPing() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "COMPANY pong");
        return ResponseEntity.ok(response);
    }
}
