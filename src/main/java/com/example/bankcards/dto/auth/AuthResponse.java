package com.example.bankcards.dto.auth;

public class AuthResponse {
    private String accessToken;
    private String tokenType;

    public AuthResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }
    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
}
