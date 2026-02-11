package com.edutech.platform.modules.company.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="company_verification_requests")
public class VerificationRequest extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="company_profile_id", nullable=false)
    private CompanyProfile companyProfile;
    private String status;
    private String adminComment;
}
