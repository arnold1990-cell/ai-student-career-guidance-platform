package com.edutech.platform.modules.talent.domain.entity;

import com.edutech.platform.modules.company.domain.entity.CompanyProfile;
import com.edutech.platform.modules.student.domain.entity.StudentProfile;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="talent_shortlists")
public class Shortlist extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="company_profile_id")
    private CompanyProfile companyProfile;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_profile_id")
    private StudentProfile studentProfile;
}
