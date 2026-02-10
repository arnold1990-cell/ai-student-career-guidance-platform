package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING) private Enums.NotificationChannel channel;
    @Enumerated(EnumType.STRING) private Enums.NotificationType type;
    private String title;
    @Column(columnDefinition="TEXT") private String message;
    @Enumerated(EnumType.STRING) private Enums.NotificationStatus status;
    private Instant createdAt;
}
