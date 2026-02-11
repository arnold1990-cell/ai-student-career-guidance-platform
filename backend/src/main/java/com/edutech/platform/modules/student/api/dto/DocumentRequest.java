package com.edutech.platform.modules.student.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentRequest {
    @NotBlank private String fileName;
    @NotBlank private String contentType;
    private long sizeBytes;
}
