package com.edurite.domain.repo;

import com.edurite.domain.model.PasswordResetToken;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> { Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash); }
