package com.edurite.domain.repo;

import com.edurite.domain.model.StudentSubscription;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface StudentSubscriptionRepository extends JpaRepository<StudentSubscription, Long> { Optional<StudentSubscription> findTopByStudentIdOrderByStartedAtDesc(Long studentId); }
