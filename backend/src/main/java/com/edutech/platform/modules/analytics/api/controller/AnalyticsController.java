package com.edutech.platform.modules.analytics.api.controller;

import com.edutech.platform.shared.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    private final JdbcTemplate jdbcTemplate;

    public AnalyticsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> dashboard(HttpServletRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        Integer userCount = jdbcTemplate.queryForObject("select count(*) from iam_users", Integer.class);
        Integer bursaryCount = jdbcTemplate.queryForObject("select count(*) from bursaries", Integer.class);
        data.put("users", userCount);
        data.put("bursaries", bursaryCount);
        return new ApiResponse<>(Instant.now(), request.getRequestURI(), "Dashboard", data);
    }
}
