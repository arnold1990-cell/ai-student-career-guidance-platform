package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="message_threads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageThread {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long companyId; private Long studentId; private Instant createdAt;
}
