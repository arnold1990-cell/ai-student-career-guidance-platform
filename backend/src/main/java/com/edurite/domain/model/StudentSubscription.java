package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="student_subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentSubscription {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long studentId; private Long planId;
    @Enumerated(EnumType.STRING) private Enums.SubscriptionStatus status;
    private Instant startedAt; private Instant endsAt; private String lastPaymentStatus;
}
