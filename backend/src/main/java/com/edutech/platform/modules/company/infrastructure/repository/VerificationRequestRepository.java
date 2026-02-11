package com.edutech.platform.modules.company.infrastructure.repository;

import com.edutech.platform.modules.company.domain.entity.VerificationRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {
    List<VerificationRequest> findByStatus(String status);
}
