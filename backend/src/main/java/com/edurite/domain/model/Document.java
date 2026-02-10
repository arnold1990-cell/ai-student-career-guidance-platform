package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long ownerUserId;
    @Enumerated(EnumType.STRING) private Enums.DocumentType type;
    private String filename; private String mimeType; private Long size; private String storagePath; private Instant uploadedAt;
}
