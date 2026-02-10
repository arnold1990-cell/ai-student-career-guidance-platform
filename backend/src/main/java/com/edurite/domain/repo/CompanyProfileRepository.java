package com.edurite.domain.repo;

import com.edurite.domain.model.CompanyProfile;import com.edurite.domain.model.Enums.VerificationStatus;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> { List<CompanyProfile> findByVerificationStatus(VerificationStatus status); }
