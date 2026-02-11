package com.edutech.platform.modules.admin.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="moderation_queue")
public class ModerationQueue extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemType;
    private Long itemId;
    private String status;
}
