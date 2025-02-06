package com.example.onlinestore.service;

import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;

public interface UserService {
    LoginResponse login(LoginRequest request);
} 