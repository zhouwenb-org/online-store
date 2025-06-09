package com.example.onlinestore.controller;

import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.UserPageRequest;
import com.example.onlinestore.dto.UserVO;
import com.example.onlinestore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户控制器独立测试")
class UserControllerStandaloneTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Test message");
    }

    @Nested
    @DisplayName("获取用户列表测试")
    class ListUsersTests {

        @Test
        @DisplayName("成功获取用户列表")
        void testListUsers_Success() throws Exception {
            UserVO user1 = new UserVO();
            user1.setId(1L);
            user1.setUsername("user1");

            UserVO user2 = new UserVO();
            user2.setId(2L);
            user2.setUsername("user2");

            PageResponse<UserVO> mockResponse = new PageResponse<>();
            mockResponse.setRecords(Arrays.asList(user1, user2));
            mockResponse.setTotal(2L);
            mockResponse.setPageNum(1);
            mockResponse.setPageSize(10);

            when(userService.listUsers(any(UserPageRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(get("/api/users")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
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

            verify(userService).listUsers(any(UserPageRequest.class));
        }

        @Test
        @DisplayName("页大小超过限制")
        void testListUsers_PageSizeExceedsLimit() throws Exception {
            mockMvc.perform(get("/api/users")
                    .param("pageNum", "1")
                    .param("pageSize", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("页码无效")
        void testListUsers_InvalidPageNumber() throws Exception {
            mockMvc.perform(get("/api/users")
                    .param("pageNum", "0")
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("服务层异常 - IllegalArgumentException")
        void testListUsers_ServiceException() throws Exception {
            when(userService.listUsers(any(UserPageRequest.class)))
                    .thenThrow(new IllegalArgumentException("Invalid parameters"));

            mockMvc.perform(get("/api/users")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid parameters"));

            verify(userService).listUsers(any(UserPageRequest.class));
        }

        @Test
        @DisplayName("系统内部错误")
        void testListUsers_InternalServerError() throws Exception {
            when(userService.listUsers(any(UserPageRequest.class)))
                    .thenThrow(new RuntimeException("Database error"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                    .thenReturn("Internal server error");

            mockMvc.perform(get("/api/users")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Internal server error"));

            verify(userService).listUsers(any(UserPageRequest.class));
            verify(messageSource).getMessage(eq("error.system.internal"), isNull(), any(Locale.class));
        }
    }
}
