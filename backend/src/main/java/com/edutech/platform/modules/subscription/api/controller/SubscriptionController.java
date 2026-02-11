package com.edutech.platform.modules.subscription.api.controller;

import com.edutech.platform.modules.subscription.application.service.SubscriptionService;
import java.util.LinkedHashMap;
import java.util.Map;
import com.edutech.platform.shared.api.ApiResponse;
import com.edutech.platform.shared.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> subscribe(@RequestParam String planCode, Authentication auth, HttpServletRequest req) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        com.edutech.platform.modules.subscription.domain.entity.StudentSubscription sub = subscriptionService.subscribe(user.getUserId(), planCode);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("subscriptionId", sub.getId());
        data.put("plan", sub.getPlan().getCode());
        data.put("status", sub.getStatus());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Subscribed", data);
    }

    @PostMapping("/cancel")
    public ApiResponse<String> cancel(Authentication auth, HttpServletRequest req) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        subscriptionService.cancel(user.getUserId());
        return new ApiResponse<>(Instant.now(), req.getRequestURI(), "Cancelled", "OK");
    }
}
