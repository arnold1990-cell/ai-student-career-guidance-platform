package com.edutech.platform.shared.config;

import com.edutech.platform.modules.iam.domain.entity.Role;
import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.RoleRepository;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevDataSeeder {

    @Bean
    public CommandLineRunner seedUsers(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            seedUser("admin@edutech.local", "Admin User", "ADMIN", roleRepository, userRepository, passwordEncoder);
            seedUser("student@edutech.local", "Student User", "STUDENT", roleRepository, userRepository, passwordEncoder);
            seedUser("company@edutech.local", "Company User", "COMPANY", roleRepository, userRepository, passwordEncoder);
        };
    }

    private void seedUser(String email, String name, String roleName, RoleRepository roleRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setRole(role);
        user.setStatus(AccountStatus.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        userRepository.save(user);
    }
}
