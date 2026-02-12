package com.edutech.platform.modules.iam.service;

public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private UserSummary user;

    public AuthResponse(String accessToken, String tokenType, long expiresInSeconds, UserSummary user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public UserSummary getUser() { return user; }
}
