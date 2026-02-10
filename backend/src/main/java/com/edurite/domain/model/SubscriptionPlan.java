package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="subscription_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionPlan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) private Enums.SubscriptionName name;
    private BigDecimal price;
    @Enumerated(EnumType.STRING) private Enums.BillingCycle billingCycle;
    @Column(columnDefinition="JSON") private String features;
}
