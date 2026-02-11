package com.edutech.platform.modules.subscription.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="payment_transactions")
public class PaymentTransaction extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerRef;
    private String status;
    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;
}
