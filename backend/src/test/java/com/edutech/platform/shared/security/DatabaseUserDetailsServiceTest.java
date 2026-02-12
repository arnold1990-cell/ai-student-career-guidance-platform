package com.edutech.platform.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.edutech.platform.modules.iam.domain.entity.Role;
import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private DatabaseUserDetailsService service;

    @Test
    void loadUserByUsername_found_returnsCoreDetails() {
        User user = user("student@mail.com", "HASH", "STUDENT", AccountStatus.ACTIVE);
        when(userRepository.findByEmail("student@mail.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername(" STUDENT@mail.com ");

        assertThat(details.getUsername()).isEqualTo("student@mail.com");
        assertThat(details.getPassword()).isEqualTo("HASH");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_STUDENT");
    }

    @Test
    void loadUserByUsername_nullStatus_notDisabled() {
        User user = user("user@mail.com", "HASH", "COMPANY", null);
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("user@mail.com");

        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_roleWithPrefix_doesNotDuplicatePrefix() {
        User user = user("admin@mail.com", "HASH", "ROLE_ADMIN", AccountStatus.ACTIVE);
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin@mail.com");

        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_missingUser_throwsUsernameNotFound() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    private User user(String email, String passwordHash, String roleName, AccountStatus status) {
        Role role = new Role();
        role.setName(roleName);
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
