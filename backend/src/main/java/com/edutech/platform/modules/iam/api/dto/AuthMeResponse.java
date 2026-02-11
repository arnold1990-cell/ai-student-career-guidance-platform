package com.edutech.platform.modules.iam.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthMeResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
}
