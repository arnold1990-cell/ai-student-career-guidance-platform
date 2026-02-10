package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long threadId; private Long senderUserId;
    @Column(columnDefinition="TEXT") private String body;
    private Instant createdAt;
}
