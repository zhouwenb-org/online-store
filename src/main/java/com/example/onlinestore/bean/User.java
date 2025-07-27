package com.example.onlinestore.bean;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1099483498189107702L;
    private Long id;
    private String username;
    private String token;
    private LocalDateTime tokenExpireTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenExpireTime() {
        return tokenExpireTime;
    }

    public void setTokenExpireTime(LocalDateTime tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }
}
