package com.edutech.platform.shared.exception;

import com.edutech.platform.shared.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> handleApi(ApiException ex, HttpServletRequest request) {
        ApiResponse<String> response = new ApiResponse<>(Instant.now(), request.getRequestURI(), ex.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = "Validation failed";
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
            message = fieldError.getField() + " " + fieldError.getDefaultMessage();
        }
        ApiResponse<String> response = new ApiResponse<>(Instant.now(), request.getRequestURI(), message, null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Bad credentials for path={} reason={}", request.getRequestURI(), ex.getMessage());
        ApiResponse<String> response = new ApiResponse<>(Instant.now(), request.getRequestURI(), "Invalid credentials", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication exception for path={} reason={}", request.getRequestURI(), ex.getMessage());
        ApiResponse<String> response = new ApiResponse<>(Instant.now(), request.getRequestURI(), "Authentication failed", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleUnknown(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for path={}", request.getRequestURI(), ex);
        ApiResponse<String> response = new ApiResponse<>(Instant.now(), request.getRequestURI(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
