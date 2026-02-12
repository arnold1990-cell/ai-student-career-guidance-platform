package com.edutech.platform.shared.exception;

import java.time.OffsetDateTime;

public class ApiErrorResponse {
    private String message;
    private OffsetDateTime timestamp;

    public ApiErrorResponse(String message) {
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public String getMessage() { return message; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
