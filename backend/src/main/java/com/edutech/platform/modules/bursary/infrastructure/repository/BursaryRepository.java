package com.edutech.platform.modules.bursary.infrastructure.repository;

import com.edutech.platform.modules.bursary.domain.entity.Bursary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BursaryRepository extends JpaRepository<Bursary, Long> {
    List<Bursary> findByApprovalStatus(String approvalStatus);
    List<Bursary> findByTitleContainingIgnoreCaseAndApprovalStatus(String title, String approvalStatus);
}
