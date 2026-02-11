package com.edutech.platform.modules.student.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentProfileRequest {
    @NotBlank private String interests;
    @NotBlank private String locationPreference;
    private String bio;
}
