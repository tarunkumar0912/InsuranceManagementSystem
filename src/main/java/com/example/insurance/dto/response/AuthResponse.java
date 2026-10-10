package com.example.insurance.dto.response;

public class AuthResponse {

    private String message;
    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private Long userId;
    private String email;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(
            String message,
            String accessToken,
            long expiresInSeconds,
            Long userId,
            String email,
            String role) {

        this.message = message;
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresInSeconds = expiresInSeconds;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}