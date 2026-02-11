package com.edutech.platform.modules.subscription.infrastructure.repository;

import com.edutech.platform.modules.subscription.domain.entity.StudentSubscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentSubscriptionRepository extends JpaRepository<StudentSubscription, Long> {
    Optional<StudentSubscription> findByUserIdAndStatus(Long userId, String status);
}
