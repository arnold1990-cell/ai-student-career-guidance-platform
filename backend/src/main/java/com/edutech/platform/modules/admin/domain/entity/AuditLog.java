package com.edutech.platform.modules.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="admin_audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long actorUserId;
    private String action;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private java.time.Instant createdAt;
}
