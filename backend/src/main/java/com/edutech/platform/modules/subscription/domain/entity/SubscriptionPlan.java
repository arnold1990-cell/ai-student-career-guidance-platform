package com.edutech.platform.modules.subscription.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="subscription_plans")
public class SubscriptionPlan extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true) private String code;
    private String name;
    private double monthlyPrice;
}
