package com.edutech.platform.shared.api;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private Instant timestamp;
    private String path;
    private String message;
    private T data;
}
