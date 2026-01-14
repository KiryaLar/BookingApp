package ru.larkin.bookingservice.dto.resp;

import jakarta.validation.constraints.NotNull;

public class AuthResponse {

    @NotNull
    private String accessToken;

    @NotNull
    private TokenTypeEnum tokenType;

    private Integer expiresIn;

    public enum TokenTypeEnum {
        BEARER
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}

