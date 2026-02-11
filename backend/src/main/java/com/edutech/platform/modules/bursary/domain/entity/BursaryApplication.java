package com.edutech.platform.modules.bursary.domain.entity;

import com.edutech.platform.modules.student.domain.entity.StudentProfile;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="bursary_applications")
public class BursaryApplication extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="bursary_id", nullable=false)
    private Bursary bursary;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_profile_id", nullable=false)
    private StudentProfile studentProfile;
    private String status;
}
