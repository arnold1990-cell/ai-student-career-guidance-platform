package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="company_shortlists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyShortlist {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long companyId; private Long studentId;
}
