package com.edutech.platform.modules.bursary.domain.entity;

import com.edutech.platform.modules.company.domain.entity.CompanyProfile;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="bursaries")
public class Bursary extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="company_profile_id")
    private CompanyProfile companyProfile;
    private String title;
    private String description;
    private String approvalStatus;
}
