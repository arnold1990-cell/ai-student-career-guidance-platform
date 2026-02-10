package com.edurite.domain.repo;

import com.edurite.domain.model.SubscriptionPlan;import com.edurite.domain.model.Enums.SubscriptionName;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> { Optional<SubscriptionPlan> findByName(SubscriptionName name); }
