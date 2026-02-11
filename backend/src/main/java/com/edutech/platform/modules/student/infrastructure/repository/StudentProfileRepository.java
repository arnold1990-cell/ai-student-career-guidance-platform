package com.edutech.platform.modules.student.infrastructure.repository;

import com.edutech.platform.modules.student.domain.entity.StudentProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
}
