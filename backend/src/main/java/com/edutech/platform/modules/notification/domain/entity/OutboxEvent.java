package com.edutech.platform.modules.notification.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="outbox_events")
public class OutboxEvent extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aggregateType;
    private Long aggregateId;
    private String eventType;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private String status;
}
