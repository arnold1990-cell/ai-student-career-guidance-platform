package com.edutech.platform.modules.notification.api.controller;

import com.edutech.platform.modules.notification.application.service.NotificationService;
import java.util.LinkedHashMap;
import java.util.Map;
import com.edutech.platform.shared.api.ApiResponse;
import com.edutech.platform.shared.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(Authentication auth, HttpServletRequest req) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        java.util.List<com.edutech.platform.modules.notification.domain.entity.Notification> list = notificationService.listForUser(user.getUserId());
        java.util.List<Map<String, Object>> data = new java.util.ArrayList<>();
        int i = 0;
        while (i < list.size()) {
            com.edutech.platform.modules.notification.domain.entity.Notification n = list.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", n.getId());
            item.put("title", n.getTitle());
            item.put("body", n.getBody());
            item.put("read", n.isReadStatus());
            data.add(item);
            i++;
        }
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Notifications", data);
    }
}
