package com.edurite.application.service;

import com.edurite.application.exception.AppException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Window> store = new ConcurrentHashMap<>();
    public void check(String key, int max, long seconds){
        var now = Instant.now().getEpochSecond();
        var w = store.computeIfAbsent(key, k-> new Window(now,0));
        synchronized (w){
            if (now - w.start >= seconds){ w.start=now; w.count=0; }
            w.count++;
            if (w.count > max) throw new AppException(429, "Too many requests");
        }
    }
    static class Window { long start; int count; Window(long s,int c){start=s;count=c;} }
}
