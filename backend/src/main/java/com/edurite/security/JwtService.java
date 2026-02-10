package com.edurite.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessMs;
    private final long refreshMs;

    public JwtService(@Value("${security.jwt.secret}") String secret, @Value("${security.jwt.access-ms}") long accessMs, @Value("${security.jwt.refresh-ms}") long refreshMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMs = accessMs;
        this.refreshMs = refreshMs;
    }

    public String token(String subject, String role, boolean refresh) {
        long ttl = refresh ? refreshMs : accessMs;
        return Jwts.builder().subject(subject).claims(Map.of("role", role, "type", refresh ? "refresh" : "access"))
                .issuedAt(new Date()).expiration(Date.from(Instant.now().plusMillis(ttl))).signWith(key).compact();
    }

    public Jws<Claims> parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token); }
}
