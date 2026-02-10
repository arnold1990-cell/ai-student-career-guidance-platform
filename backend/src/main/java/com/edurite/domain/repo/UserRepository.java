package com.edurite.domain.repo;

import com.edurite.domain.model.User;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface UserRepository extends JpaRepository<User, Long> { Optional<User> findByEmail(String email); }
