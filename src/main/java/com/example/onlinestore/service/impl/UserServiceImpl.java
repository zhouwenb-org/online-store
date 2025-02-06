package com.example.onlinestore.service.impl;

import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.mapper.UserMapper;
import com.example.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String USER_SERVICE_URL = "http://user-service/auth";
    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE_DAYS = 1;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 调用user-service进行认证
        Boolean isAuthenticated = restTemplate.postForObject(USER_SERVICE_URL, request, Boolean.class);
        
        if (isAuthenticated == null || !isAuthenticated) {
            throw new RuntimeException("Authentication failed");
        }

        // 生成token
        String token = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusDays(TOKEN_EXPIRE_DAYS);

        // 保存到数据库
        User user = new User();
        user.setUsername(request.getUsername());
        user.setToken(token);
        user.setTokenExpireTime(expireTime);
        userMapper.updateUserToken(user);

        // 保存到Redis
        String redisKey = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, request.getUsername(), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        // 返回响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpireTime(expireTime);
        return response;
    }
} 