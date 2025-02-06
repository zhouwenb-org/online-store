package com.example.onlinestore.service.impl;

import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.model.User;
import com.example.onlinestore.mapper.UserMapper;
import com.example.onlinestore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${admin.auth.username}")
    private String adminUsername;

    @Value("${admin.auth.password}")
    private String adminPassword;

    @Value("${service.user.base-url}")
    private String userServiceBaseUrl;

    private static final String AUTH_PATH = "/auth";
    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE_DAYS = 1;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MessageSource messageSource;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 先检查是否是管理员用户
        if (adminUsername.equals(request.getUsername())) {
            // 如果是管理员，验证密码
            if (adminPassword.equals(request.getPassword())) {
                logger.info("管理员快速登录");
                return createLoginResponse(request.getUsername());
            } else {
                logger.warn("管理员密码错误");
                throw new IllegalArgumentException(messageSource.getMessage(
                    "error.invalid.credentials", null, LocaleContextHolder.getLocale()));
            }
        }

        // 非管理员用户，调用user-service进行认证
        String authUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
            .path(AUTH_PATH)
            .toUriString();
        Boolean isAuthenticated = restTemplate.postForObject(authUrl, request, Boolean.class);
        
        if (isAuthenticated == null || !isAuthenticated) {
            throw new IllegalArgumentException(messageSource.getMessage(
                "error.invalid.credentials", null, LocaleContextHolder.getLocale()));
        }

        return createLoginResponse(request.getUsername());
    }

    private LoginResponse createLoginResponse(String username) {
        // 生成token
        String token = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusDays(TOKEN_EXPIRE_DAYS);

        // 查找或创建用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            // 用户不存在，创建新用户
            user = new User();
            user.setUsername(username);
            user.setToken(token);
            user.setTokenExpireTime(expireTime);
            userMapper.insertUser(user);
            logger.info("创建新用户: {}", username);
        } else {
            // 更新现有用户的token
            user.setToken(token);
            user.setTokenExpireTime(expireTime);
            userMapper.updateUserToken(user);
            logger.info("更新用户token: {}", username);
        }

        // 保存到Redis
        String redisKey = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, username, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        // 返回响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpireTime(expireTime);
        return response;
    }
} 