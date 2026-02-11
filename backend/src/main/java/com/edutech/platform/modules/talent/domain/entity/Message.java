package com.edutech.platform.modules.talent.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="messages")
public class Message extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="thread_id")
    private MessageThread thread;
    private Long senderUserId;
    @Column(columnDefinition = "TEXT")
    private String body;
}
