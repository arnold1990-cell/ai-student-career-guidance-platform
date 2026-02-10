package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="company_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyProfile {
    @Id
    private Long userId;
    @MapsId @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    private String companyName;
    private String registrationNumber;
    private String industry;
    @Column(columnDefinition="TEXT") private String contactInfo;
    private String officialEmail;
    @Enumerated(EnumType.STRING)
    private Enums.VerificationStatus verificationStatus;
    private Instant submittedAt;
    private Instant reviewedAt;
    @Column(columnDefinition="TEXT") private String reviewNotes;
}
