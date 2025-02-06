package com.example.onlinestore.dto;

import java.time.LocalDateTime;

public class LoginResponse {
    private String token;
    private LocalDateTime expireTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
} 