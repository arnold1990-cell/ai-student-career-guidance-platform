package com.edutech.platform.modules.student.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class StudentProfileResponse {
    private Long id;
    private String interests;
    private String locationPreference;
    private String bio;
}
