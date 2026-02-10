package com.edurite.domain.repo;

import com.edurite.domain.model.CompanyShortlist;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface CompanyShortlistRepository extends JpaRepository<CompanyShortlist, Long> { List<CompanyShortlist> findByCompanyId(Long companyId); }
