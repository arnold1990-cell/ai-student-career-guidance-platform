package com.edutech.platform.modules.iam.infrastructure.repository;

import com.edutech.platform.modules.iam.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
}
