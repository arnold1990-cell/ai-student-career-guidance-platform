package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long actorUserId; private String action; private String entityType; private String entityId;
    @Column(columnDefinition="JSON") private String metadata;
    private Instant createdAt;
}
