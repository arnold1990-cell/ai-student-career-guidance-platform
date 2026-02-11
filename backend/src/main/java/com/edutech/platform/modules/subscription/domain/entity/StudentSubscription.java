package com.edutech.platform.modules.subscription.domain.entity;

import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="student_subscriptions")
public class StudentSubscription extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="plan_id", nullable=false)
    private SubscriptionPlan plan;
    private String status;
}
