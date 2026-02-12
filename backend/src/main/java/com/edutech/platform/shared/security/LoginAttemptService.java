package com.edutech.platform.shared.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(15);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final ConcurrentMap<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        AttemptState state = attempts.get(key);
        if (state == null) {
            return false;
        }

        Instant now = Instant.now();
        if (state.lockedUntil != null && state.lockedUntil.isAfter(now)) {
            return true;
        }

        if (state.lastAttempt != null && state.lastAttempt.plus(ATTEMPT_WINDOW).isBefore(now)) {
            attempts.remove(key);
        }

        return false;
    }

    public long getRetryAfterSeconds(String key) {
        AttemptState state = attempts.get(key);
        if (state == null || state.lockedUntil == null) {
            return 0;
        }
        long seconds = Duration.between(Instant.now(), state.lockedUntil).getSeconds();
        return Math.max(seconds, 0);
    }

    public void recordFailure(String key) {
        attempts.compute(key, (k, existing) -> {
            Instant now = Instant.now();
            AttemptState state = existing;
            if (state == null || state.lastAttempt == null || state.lastAttempt.plus(ATTEMPT_WINDOW).isBefore(now)) {
                state = new AttemptState();
                state.failures = 1;
            } else {
                state.failures = state.failures + 1;
            }

            state.lastAttempt = now;
            if (state.failures >= MAX_ATTEMPTS) {
                state.lockedUntil = now.plus(LOCK_DURATION);
            }

            return state;
        });
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    private static class AttemptState {
        private int failures;
        private Instant lastAttempt;
        private Instant lockedUntil;
    }
}
