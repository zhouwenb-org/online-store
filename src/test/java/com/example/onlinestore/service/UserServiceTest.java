package com.example.onlinestore.service;

import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.UserPageRequest;
import com.example.onlinestore.dto.UserVO;
import com.example.onlinestore.model.User;
import com.example.onlinestore.mapper.UserMapper;
import com.example.onlinestore.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserServiceImpl userService;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";
    private static final String USER_SERVICE_BASE_URL = "http://user-service";

    @BeforeEach
    void setUp() {
        // 设置配置项的值
        ReflectionTestUtils.setField(userService, "adminUsername", ADMIN_USERNAME);
        ReflectionTestUtils.setField(userService, "adminPassword", ADMIN_PASSWORD);
        ReflectionTestUtils.setField(userService, "userServiceBaseUrl", USER_SERVICE_BASE_URL);
    }

    @Test
    void whenAdminLoginWithNewUser_thenCreateUserAndReturnToken() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(ADMIN_USERNAME);
        request.setPassword(ADMIN_PASSWORD);

        // 设置mock行为：用户不存在
        when(userMapper.findByUsername(ADMIN_USERNAME)).thenReturn(null);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = userService.login(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getExpireTime());
        
        // 验证调用
        verify(userMapper).findByUsername(ADMIN_USERNAME);
        verify(userMapper).insertUser(any(User.class));
        verify(userMapper, never()).updateUserToken(any(User.class));
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
        
        // 验证没有调用用户服务
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    void whenAdminLoginWithExistingUser_thenUpdateTokenAndReturn() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(ADMIN_USERNAME);
        request.setPassword(ADMIN_PASSWORD);

        // 设置mock行为：用户已存在
        User existingUser = new User();
        existingUser.setUsername(ADMIN_USERNAME);
        existingUser.setToken("old-token");
        existingUser.setTokenExpireTime(LocalDateTime.now().minusDays(1));
        when(userMapper.findByUsername(ADMIN_USERNAME)).thenReturn(existingUser);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = userService.login(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getExpireTime());
        assertNotEquals("old-token", response.getToken());
        
        // 验证调用
        verify(userMapper).findByUsername(ADMIN_USERNAME);
        verify(userMapper, never()).insertUser(any(User.class));
        verify(userMapper).updateUserToken(any(User.class));
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
        
        // 验证没有调用用户服务
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    void whenNormalUserLoginWithNewUser_thenCreateUserAndReturnToken() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("normal_user");
        request.setPassword("password");

        // 设置mock行为：用户不存在，认证成功
        when(userMapper.findByUsername("normal_user")).thenReturn(null);
        when(restTemplate.postForObject(eq(USER_SERVICE_BASE_URL + "/auth"), any(), eq(Boolean.class)))
            .thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = userService.login(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getExpireTime());
        
        // 验证调用
        verify(userMapper).findByUsername("normal_user");
        verify(userMapper).insertUser(any(User.class));
        verify(userMapper, never()).updateUserToken(any(User.class));
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
        verify(restTemplate).postForObject(eq(USER_SERVICE_BASE_URL + "/auth"), any(), eq(Boolean.class));

        // 验证插入的用户数据
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insertUser(userCaptor.capture());
        User insertedUser = userCaptor.getValue();
        assertEquals("normal_user", insertedUser.getUsername());
        assertEquals(response.getToken(), insertedUser.getToken());
        assertEquals(response.getExpireTime(), insertedUser.getTokenExpireTime());
    }

    @Test
    void whenNormalUserLoginWithExistingUser_thenUpdateTokenAndReturn() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("normal_user");
        request.setPassword("password");

        // 设置mock行为：用户已存在，认证成功
        User existingUser = new User();
        existingUser.setUsername("normal_user");
        existingUser.setToken("old-token");
        existingUser.setTokenExpireTime(LocalDateTime.now().minusDays(1));
        when(userMapper.findByUsername("normal_user")).thenReturn(existingUser);
        when(restTemplate.postForObject(eq(USER_SERVICE_BASE_URL + "/auth"), any(), eq(Boolean.class)))
            .thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = userService.login(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getExpireTime());
        assertNotEquals("old-token", response.getToken());
        
        // 验证调用
        verify(userMapper).findByUsername("normal_user");
        verify(userMapper, never()).insertUser(any(User.class));
        verify(userMapper).updateUserToken(any(User.class));
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
        verify(restTemplate).postForObject(eq(USER_SERVICE_BASE_URL + "/auth"), any(), eq(Boolean.class));

        // 验证更新的用户数据
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateUserToken(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertEquals("normal_user", updatedUser.getUsername());
        assertEquals(response.getToken(), updatedUser.getToken());
        assertEquals(response.getExpireTime(), updatedUser.getTokenExpireTime());
    }

    @Test
    void whenAdminLoginWithWrongPassword_thenThrowException() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(ADMIN_USERNAME);
        request.setPassword("wrong_password");

        // 设置错误消息
        when(messageSource.getMessage(eq("error.invalid.credentials"), isNull(), any(Locale.class)))
            .thenReturn("Invalid username or password");

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
        
        // 验证调用
        verify(userMapper, never()).findByUsername(anyString());
        verify(userMapper, never()).insertUser(any(User.class));
        verify(userMapper, never()).updateUserToken(any(User.class));
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    void whenNormalUserLoginWithWrongPassword_thenThrowException() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("normal_user");
        request.setPassword("wrong_password");

        // 设置mock行为
        when(restTemplate.postForObject(eq(USER_SERVICE_BASE_URL + "/auth"), any(), eq(Boolean.class)))
            .thenReturn(false);
        when(messageSource.getMessage(eq("error.invalid.credentials"), isNull(), any(Locale.class)))
            .thenReturn("Invalid username or password");

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
        
        // 验证调用
        verify(userMapper, never()).findByUsername(anyString());
        verify(userMapper, never()).insertUser(any(User.class));
        verify(userMapper, never()).updateUserToken(any(User.class));
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
        verify(restTemplate).postForObject(eq(USER_SERVICE_BASE_URL + "/auth"), any(), eq(Boolean.class));
    }

    @Test
    void testListUsers_Success() {
        UserPageRequest request = new UserPageRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        List<User> mockUsers = createMockUsers(5);
        when(userMapper.findAllWithPagination(0, 10)).thenReturn(mockUsers);
        when(userMapper.countTotal()).thenReturn(5L);

        PageResponse<UserVO> result = userService.listUsers(request);

        assertNotNull(result);
        assertEquals(5, result.getRecords().size());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(5L, result.getTotal());

        verify(userMapper).findAllWithPagination(0, 10);
        verify(userMapper).countTotal();
    }

    @Test
    void testListUsers_EmptyResult() {
        UserPageRequest request = new UserPageRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        when(userMapper.findAllWithPagination(0, 10)).thenReturn(new ArrayList<>());
        when(userMapper.countTotal()).thenReturn(0L);

        PageResponse<UserVO> result = userService.listUsers(request);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(0L, result.getTotal());

        verify(userMapper).findAllWithPagination(0, 10);
        verify(userMapper).countTotal();
    }

    @Test
    void testGetUserByToken_Success() throws Exception {
        String token = "valid-token";
        String redisKey = "token:" + token;
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setToken(token);
        mockUser.setTokenExpireTime(LocalDateTime.now().plusHours(1));
        
        String userJson = "{\"id\":1,\"username\":\"testuser\",\"token\":\"" + token + "\",\"tokenExpireTime\":\"2024-01-01T12:00:00\"}";
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(userJson);

        User result = userService.getUserByToken(token);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals(token, result.getToken());

        verify(valueOperations).get(redisKey);
    }

    @Test
    void testGetUserByToken_NotFound() {
        String token = "invalid-token";
        String redisKey = "token:" + token;
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(null);

        User result = userService.getUserByToken(token);

        assertNull(result);
        verify(valueOperations).get(redisKey);
    }

    @Test
    void testGetUserByToken_JsonParseError() {
        String token = "invalid-json-token";
        String redisKey = "token:" + token;
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn("invalid-json");

        User result = userService.getUserByToken(token);

        assertNull(result);
        verify(valueOperations).get(redisKey);
    }

    private List<User> createMockUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setToken("token" + i);
            user.setTokenExpireTime(LocalDateTime.now().plusHours(1));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            users.add(user);
        }
        return users;
    }
}     