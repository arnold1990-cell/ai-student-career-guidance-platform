package com.edurite.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String email;
    @Column(name="password_hash", nullable=false)
    private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private Enums.Role role;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private Enums.UserStatus status;
    private String phone;
    private Instant createdAt;
    private Instant lastLoginAt;
}
