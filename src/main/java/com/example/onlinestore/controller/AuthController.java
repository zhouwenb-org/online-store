package com.example.onlinestore.controller;

import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            logger.info("用户登录请求: {}", request.getUsername());
            LoginResponse response = userService.login(request);
            logger.info("用户 {} 登录成功", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 业务逻辑异常，返回400
            logger.warn("登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // 系统异常，返回500
            logger.error("系统错误: ", e);
            String errorMessage = messageSource.getMessage("error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(Map.of("message", errorMessage));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Token") String token) {
        try {
            logger.info("用户登出请求，token: {}", token);
            userService.logout(token);
            
            String successMessage = messageSource.getMessage(
                "success.user.logout", null, LocaleContextHolder.getLocale());
            logger.info("用户登出成功");
            return ResponseEntity.ok(Map.of("message", successMessage));
        } catch (IllegalArgumentException e) {
            // 业务逻辑异常，返回400
            logger.warn("登出失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // 系统异常，返回500
            logger.error("登出系统错误: ", e);
            String errorMessage = messageSource.getMessage("error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(Map.of("message", errorMessage));
        }
    }
} 