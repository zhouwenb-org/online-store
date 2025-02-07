package com.example.onlinestore.controller;

import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.UserPageRequest;
import com.example.onlinestore.dto.UserVO;
import com.example.onlinestore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void whenListUsersWithValidParams_thenReturnSuccess() throws Exception {
        // 准备测试数据
        UserPageRequest request = new UserPageRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        UserVO user1 = new UserVO();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        UserVO user2 = new UserVO();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        PageResponse<UserVO> response = new PageResponse<>();
        response.setRecords(Arrays.asList(user1, user2));
        response.setTotal(2);
        response.setPageNum(1);
        response.setPageSize(10);

        // 设置mock行为
        when(userService.listUsers(any(UserPageRequest.class))).thenReturn(response);

        // 执行测试
        mockMvc.perform(get("/api/users")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("X-Token", "test-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records.length()").value(2))
                .andExpect(jsonPath("$.records[0].id").value(1))
                .andExpect(jsonPath("$.records[0].username").value("user1"))
                .andExpect(jsonPath("$.records[1].id").value(2))
                .andExpect(jsonPath("$.records[1].username").value("user2"))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void whenPageSizeExceedsLimit_thenReturnBadRequest() throws Exception {
        // 执行测试：页大小超过最大值
        mockMvc.perform(get("/api/users")
                .param("pageNum", "1")
                .param("pageSize", "101")
                .header("X-Token", "test-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPageNumberIsInvalid_thenReturnBadRequest() throws Exception {
        // 执行测试：页码小于1
        mockMvc.perform(get("/api/users")
                .param("pageNum", "0")
                .param("pageSize", "10")
                .header("X-Token", "test-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDatabaseError_thenReturnInternalError() throws Exception {
        // 设置模拟行为
        when(userService.listUsers(any())).thenThrow(new RuntimeException("Database error"));
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