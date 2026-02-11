package com.edutech.platform.modules.company.domain.entity;

import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="company_profiles")
public class CompanyProfile extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="owner_user_id", nullable=false)
    private User ownerUser;
    private String companyName;
    private String status;
}
