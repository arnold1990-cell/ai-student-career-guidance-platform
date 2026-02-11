package com.edutech.platform.modules.company.infrastructure.repository;

import com.edutech.platform.modules.company.domain.entity.CompanyProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    Optional<CompanyProfile> findByOwnerUserId(Long ownerUserId);
}
