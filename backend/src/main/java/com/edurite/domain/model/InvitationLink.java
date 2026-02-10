package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="invitation_links")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvitationLink {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long companyId; private Long bursaryId; private String token; private Instant createdAt;
}
