package com.edutech.platform.modules.iam.domain.entity;

import com.edutech.platform.modules.iam.domain.enums.AccountStatus;
import com.edutech.platform.shared.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "iam_users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String fullName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    private String refreshToken;
}
