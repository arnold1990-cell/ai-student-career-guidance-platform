package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Entity @Table(name="bursaries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bursary {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long companyId;
    private String name;
    @Column(columnDefinition="TEXT") private String description;
    private String fieldOfStudy; private String academicLevel; private LocalDate startDate; private LocalDate endDate;
    private BigDecimal fundingAmount;
    @Column(columnDefinition="TEXT") private String benefits;
    @Enumerated(EnumType.STRING) private Enums.BursaryStatus status;
    @Column(columnDefinition="JSON") private String eligibilityCriteria;
    @Column(columnDefinition="JSON") private String locationFilters;
    @Column(columnDefinition="JSON") private String requiredSubjects;
    private Instant createdAt; private Instant updatedAt;
}
