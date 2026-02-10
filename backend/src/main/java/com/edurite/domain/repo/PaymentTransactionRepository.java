package com.edurite.domain.repo;

import com.edurite.domain.model.PaymentTransaction;import org.springframework.data.jpa.repository.JpaRepository; public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {}
