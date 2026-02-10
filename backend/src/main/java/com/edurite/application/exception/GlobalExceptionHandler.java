package com.edurite.application.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleApp(AppException ex){ return ResponseEntity.status(ex.getStatus()).body(Map.of("timestamp", Instant.now(), "error", ex.getMessage(), "status", ex.getStatus())); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValid(MethodArgumentNotValidException ex){
        Map<String, String> fields = new HashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors()) fields.put(e.getField(), e.getDefaultMessage());
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "status", 400, "errors", fields));
    }
}
