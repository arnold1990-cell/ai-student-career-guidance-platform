package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="student_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentProfile {
    @Id
    private Long userId;
    @MapsId @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @Column(columnDefinition="TEXT") private String personalDetails;
    @Column(columnDefinition="TEXT") private String qualifications;
    @Column(columnDefinition="TEXT") private String experience;
    private String location;
    private Long cvDocumentId;
    private Long transcriptDocumentId;
    @Column(columnDefinition="JSON") private String preferences;
}
