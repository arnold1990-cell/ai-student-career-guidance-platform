package com.edutech.platform.modules.career.domain.entity;

import com.edutech.platform.modules.student.domain.entity.StudentProfile;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity @Table(name="career_recommendations")
public class CareerRecommendation extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_profile_id")
    private StudentProfile studentProfile;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="career_id")
    private Career career;
    private String reason;
}
