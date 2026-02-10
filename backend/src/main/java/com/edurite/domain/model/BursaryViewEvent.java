package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="bursary_view_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BursaryViewEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long bursaryId; private Long studentId; private Instant viewedAt;
}
