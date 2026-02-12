package com.edutech.platform.shared.security;

import com.edutech.platform.modules.iam.domain.entity.Role;
import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        User user = userOptional.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        Role role = user.getRole();
        if (role != null && role.getName() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(user.getStatus() != AccountStatus.ACTIVE)
                .build();
    }
}
