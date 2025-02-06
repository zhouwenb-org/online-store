package com.example.onlinestore.controller;

import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLoginSuccess() throws Exception {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("password");

        LoginResponse response = new LoginResponse();
        response.setToken(UUID.randomUUID().toString());
        response.setExpireTime(LocalDateTime.now().plusDays(1));

        // 设置 mock 行为
        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expireTime").exists());
    }

    @Test
    void testLoginFailureInEnglish() throws Exception {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("wrong_password");

        // 获取英文错误消息
        String errorMessage = messageSource.getMessage("error.invalid.credentials", null, Locale.ENGLISH);

        // 设置 mock 行为
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void testLoginFailureInChinese() throws Exception {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("wrong_password");

        // 获取中文错误消息
        String errorMessage = messageSource.getMessage("error.invalid.credentials", null, Locale.SIMPLIFIED_CHINESE);

        // 设置 mock 行为
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "zh-CN")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void testSystemError() throws Exception {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("password");

        // 设置 mock 行为
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // 获取英文错误消息
        String enErrorMessage = messageSource.getMessage("error.system.internal", null, Locale.ENGLISH);
        
        // 测试英文系统错误
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(enErrorMessage));

        // 获取中文错误消息
        String zhErrorMessage = messageSource.getMessage("error.system.internal", null, Locale.SIMPLIFIED_CHINESE);
        
        // 测试中文系统错误
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "zh-CN")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(zhErrorMessage));
    }
} 