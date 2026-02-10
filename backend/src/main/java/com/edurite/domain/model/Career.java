package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="careers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Career {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String title; private String field; private String demandLevel; private Integer salaryRangeMin; private Integer salaryRangeMax;
    @Column(columnDefinition="TEXT") private String description;
}
