package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="payment_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentTransaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long studentId; private Long planId; private BigDecimal amount; private String currency;
    @Enumerated(EnumType.STRING) private Enums.PaymentStatus status;
    private String providerRef; private Instant createdAt;
}
