package com.example.onlinestore.controller;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.model.User;
import com.example.onlinestore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("认证控制器测试")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private UserService userService;

    private LoginRequest request;
    private LoginResponse response;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("password");

        response = new LoginResponse();
        response.setToken("test-token");
        response.setExpireTime(LocalDateTime.now().plusHours(2));
    }

    @Nested
    @DisplayName("登录接口测试")
    class LoginTests {
        @Test
        @DisplayName("登录成功")
        void whenLoginSucceeds_thenReturnToken() throws Exception {
            // 设置 mock 行为
            when(userService.login(any(LoginRequest.class))).thenReturn(response);

            // 执行测试
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(response.getToken()))
                    .andExpect(jsonPath("$.expireTime").exists());
        }

        @Test
        @DisplayName("登录失败 - 英文错误消息")
        void whenLoginFailsInEnglish_thenReturnErrorMessage() throws Exception {
            // 准备测试数据
            request.setPassword("wrong_password");
            String errorMessage = messageSource.getMessage(
                "error.invalid.credentials", null, Locale.ENGLISH);

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
        @DisplayName("登录失败 - 中文错误消息")
        void whenLoginFailsInChinese_thenReturnErrorMessage() throws Exception {
            // 准备测试数据
            request.setPassword("wrong_password");
            String errorMessage = messageSource.getMessage(
                "error.invalid.credentials", null, Locale.SIMPLIFIED_CHINESE);

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
        @DisplayName("系统错误 - 多语言错误消息")
        void whenSystemError_thenReturnLocalizedErrorMessage() throws Exception {
            // 设置 mock 行为
            when(userService.login(any(LoginRequest.class)))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // 测试英文系统错误
            String enErrorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.ENGLISH);
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "en")
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(enErrorMessage));

            // 测试中文系统错误
            String zhErrorMessage = messageSource.getMessage(
                "error.system.internal", null, Locale.SIMPLIFIED_CHINESE);
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept-Language", "zh-CN")
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(zhErrorMessage));
        }
    }

    @Nested
    @DisplayName("whoami接口测试")
    class WhoamiTests {
        
        @Test
        @DisplayName("获取当前用户信息成功")
        void whenGetCurrentUserSucceeds_thenReturnUserInfo() throws Exception {
            // 准备测试数据
            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setUsername("testuser");
            mockUser.setCreatedAt(LocalDateTime.now().minusDays(1));
            mockUser.setUpdatedAt(LocalDateTime.now());
            
            // 设置UserContext
            UserContext.setCurrentUser(mockUser);
            
            try {
                // 执行测试
                mockMvc.perform(get("/api/auth/whoami"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.username").value("testuser"))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.updatedAt").exists());
            } finally {
                // 清理UserContext
                UserContext.clear();
            }
        }
        
        @Test
        @DisplayName("未登录用户访问whoami接口")
        void whenUserNotLoggedIn_thenReturnUnauthorized() throws Exception {
            // 确保UserContext为空
            UserContext.clear();
            
            String errorMessage = messageSource.getMessage(
                "error.unauthorized", null, Locale.ENGLISH);
            
            // 执行测试
            mockMvc.perform(get("/api/auth/whoami")
                    .header("Accept-Language", "en"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(errorMessage));
        }
        
        @Test
        @DisplayName("获取当前用户信息失败 - 系统错误")
        void whenSystemErrorOccurs_thenReturnInternalServerError() throws Exception {
            // 设置一个会导致异常的用户（例如，某些字段为null）
            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setUsername("testuser");
            // 故意不设置createdAt和updatedAt来模拟异常
            
            UserContext.setCurrentUser(mockUser);
            
            try {
                String errorMessage = messageSource.getMessage(
                    "error.system.internal", null, Locale.ENGLISH);
                
                // 执行测试
                mockMvc.perform(get("/api/auth/whoami")
                        .header("Accept-Language", "en"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(content().string(errorMessage));
            } finally {
                // 清理UserContext
                UserContext.clear();
            }
        }
    }
} 