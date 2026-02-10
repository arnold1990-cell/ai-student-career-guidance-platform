package com.edurite.domain.repo;

import com.edurite.domain.model.BursaryApplication;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface BursaryApplicationRepository extends JpaRepository<BursaryApplication, Long> { List<BursaryApplication> findByStudentId(Long studentId); List<BursaryApplication> findByBursaryId(Long bursaryId); Optional<BursaryApplication> findByStudentIdAndBursaryId(Long studentId, Long bursaryId); }
