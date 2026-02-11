package com.edutech.platform.modules.subscription.infrastructure.repository;

import com.edutech.platform.modules.subscription.domain.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}
