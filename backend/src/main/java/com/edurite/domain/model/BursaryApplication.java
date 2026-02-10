package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="bursary_applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BursaryApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long bursaryId; private Long studentId;
    @Enumerated(EnumType.STRING) private Enums.ApplicationStatus status;
    private Instant submittedAt; private Instant updatedAt;
}
