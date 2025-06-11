package com.example.onlinestore.controller;

import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.UserVO;
import com.example.onlinestore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("用户控制器测试")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private UserService userService;

    private PageResponse<UserVO> pageResponse;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setUsername("testuser");
        
        List<UserVO> users = Arrays.asList(userVO);
        pageResponse = new PageResponse<>();
        pageResponse.setRecords(users);
        pageResponse.setTotal(1);
        pageResponse.setPageNum(1);
        pageResponse.setPageSize(10);
    }

    @AfterEach
    void tearDown() {
        // 清理工作（如需要）
    }

    @Nested
    @DisplayName("获取用户列表接口测试")
    class ListUsersTests {
        @Test
        @DisplayName("成功获取用户列表")
        void whenListUsers_thenReturnSuccess() throws Exception {
            // 设置 mock 行为
            when(userService.listUsers(any())).thenReturn(pageResponse);

            // 执行测试
            mockMvc.perform(get("/api/users")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .header("X-Token", "test-token")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.records").isArray())
                    .andExpect(jsonPath("$.records.length()").value(1))
                    .andExpect(jsonPath("$.records[0].id").value(1))
                    .andExpect(jsonPath("$.records[0].username").value("testuser"))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.pageNum").value(1))
                    .andExpect(jsonPath("$.pageSize").value(10));
        }

        @Test
        @DisplayName("页大小超过限制")
        void whenPageSizeExceedsLimit_thenReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/users")
                    .param("pageNum", "1")
                    .param("pageSize", "101")
                    .header("X-Token", "test-token")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("页码无效")
        void whenPageNumberIsInvalid_thenReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/users")
                    .param("pageNum", "0")
                    .param("pageSize", "10")
                    .header("X-Token", "test-token")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("数据库错误")
        void whenDatabaseError_thenReturnInternalError() throws Exception {
            // 设置模拟行为
            when(userService.listUsers(any()))
                .thenThrow(new RuntimeException("Database error"));
            when(messageSource.getMessage(eq("error.system.internal"), eq(null), any(Locale.class)))
                .thenReturn("系统内部错误");

            // 执行请求并验证
            mockMvc.perform(get("/api/users")
                    .param("pageSize", "10")
                    .param("pageNum", "1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }
} 