package com.edutech.platform.modules.student.domain.entity;

import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="student_qualifications")
public class Qualification extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_profile_id", nullable=false)
    private StudentProfile studentProfile;
    private String institution;
    private String title;
}
