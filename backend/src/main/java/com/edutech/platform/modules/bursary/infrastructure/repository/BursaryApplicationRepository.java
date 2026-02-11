package com.edutech.platform.modules.bursary.infrastructure.repository;

import com.edutech.platform.modules.bursary.domain.entity.BursaryApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BursaryApplicationRepository extends JpaRepository<BursaryApplication, Long> {
}
