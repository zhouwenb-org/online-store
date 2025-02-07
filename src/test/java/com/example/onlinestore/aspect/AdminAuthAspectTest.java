package com.example.onlinestore.aspect;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminAuthAspectTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private AdminAuthAspect adminAuthAspect;

    @BeforeEach
    void setUp() {
        // 设置管理员用户名
        adminAuthAspect.adminUsername = "admin";
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void whenUserIsAdmin_thenAllowAccess() {
        // 准备测试数据
        User adminUser = new User();
        adminUser.setUsername("admin");
        UserContext.setCurrentUser(adminUser);

        // 执行测试
        assertDoesNotThrow(() -> adminAuthAspect.checkAdminAuth());
    }

    @Test
    void whenUserIsNotAdmin_thenThrowException() {
        // 准备测试数据
        User normalUser = new User();
        normalUser.setUsername("user");
        UserContext.setCurrentUser(normalUser);

        // 设置错误消息
        when(messageSource.getMessage(eq("error.access.denied"), any(), eq(LocaleContextHolder.getLocale())))
            .thenReturn("访问被拒绝");

        // 执行测试
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adminAuthAspect.checkAdminAuth()
        );

        assertEquals("访问被拒绝", exception.getMessage());
    }

    @Test
    void whenUserIsNull_thenThrowException() {
        // 准备测试数据
        UserContext.setCurrentUser(null);

        // 设置错误消息
        when(messageSource.getMessage(eq("error.access.denied"), any(), eq(LocaleContextHolder.getLocale())))
            .thenReturn("访问被拒绝");

        // 执行测试
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adminAuthAspect.checkAdminAuth()
        );

        assertEquals("访问被拒绝", exception.getMessage());
    }
} 