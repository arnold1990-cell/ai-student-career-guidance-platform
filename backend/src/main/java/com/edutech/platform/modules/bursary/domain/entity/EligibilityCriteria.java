package com.edutech.platform.modules.bursary.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="bursary_eligibility_criteria")
public class EligibilityCriteria extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name="bursary_id", nullable=false)
    private Bursary bursary;
    private String minimumQualification;
    private String region;
}
