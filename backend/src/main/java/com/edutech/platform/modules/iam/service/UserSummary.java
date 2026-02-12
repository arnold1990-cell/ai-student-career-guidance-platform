package com.edutech.platform.modules.iam.service;

import com.edutech.platform.modules.iam.domain.UserRole;

import java.util.UUID;

public class UserSummary {
    private UUID id;
    private String email;
    private UserRole role;

    public UserSummary(UUID id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}
