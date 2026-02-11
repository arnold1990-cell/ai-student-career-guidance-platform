package com.edutech.platform.modules.subscription.infrastructure.repository;

import com.edutech.platform.modules.subscription.domain.entity.SubscriptionPlan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByCode(String code);
}
