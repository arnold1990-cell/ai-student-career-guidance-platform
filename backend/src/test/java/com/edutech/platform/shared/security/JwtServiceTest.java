package com.edutech.platform.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() throws Exception {
        setField("secret", "1234567890123456789012345678901234567890123456789012345678901234");
        setField("accessExpirySeconds", 120L);
    }

    @Test
    void generateAndParseAccessToken_roundTripsClaims() {
        String token = jwtService.generateAccessToken(42L, "user@mail.com", "ADMIN");

        Claims claims = jwtService.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("user@mail.com");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.get("userId", Integer.class)).isEqualTo(42);
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = JwtService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(jwtService, value);
    }
}
