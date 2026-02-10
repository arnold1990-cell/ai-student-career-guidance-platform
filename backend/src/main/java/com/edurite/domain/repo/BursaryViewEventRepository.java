package com.edurite.domain.repo;

import com.edurite.domain.model.BursaryViewEvent;import org.springframework.data.jpa.repository.JpaRepository; public interface BursaryViewEventRepository extends JpaRepository<BursaryViewEvent, Long> { long countByBursaryId(Long bursaryId); }
