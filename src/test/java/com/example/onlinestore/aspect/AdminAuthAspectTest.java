package com.example.onlinestore.aspect;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("管理员权限验证切面测试")
public class AdminAuthAspectTest {

    @InjectMocks
    private AdminAuthAspect adminAuthAspect;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        // 使用反射设置adminUsername属性
        ReflectionTestUtils.setField(adminAuthAspect, "adminUsername", "admin");
    }

    @AfterEach
    void tearDown() {
        // 清理UserContext
        UserContext.clear();
    }

    @Test
    @DisplayName("管理员用户可以正常访问")
    void whenAdminUser_thenAccessGranted() {
        // 设置管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        UserContext.setCurrentUser(adminUser);

        // 执行测试 - 不抛出异常即为成功
        assertDoesNotThrow(() -> {
            adminAuthAspect.checkAdminAuth();
        });
    }

    @Test
    @DisplayName("未登录用户访问被拒绝")
    void whenNoUser_thenAccessDenied() {
        // 确保没有用户上下文
        UserContext.clear();

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> adminAuthAspect.checkAdminAuth()
        );
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    @DisplayName("非管理员用户访问被拒绝")
    void whenNonAdminUser_thenAccessDenied() {
        // 准备普通用户
        User normalUser = new User();
        normalUser.setUsername("normal_user");
        UserContext.setCurrentUser(normalUser);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> adminAuthAspect.checkAdminAuth()
        );
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    @DisplayName("空用户名访问被拒绝")
    void whenUserWithNullUsername_thenAccessDenied() {
        // 准备用户名为null的用户
        User userWithNullUsername = new User();
        userWithNullUsername.setUsername(null);
        UserContext.setCurrentUser(userWithNullUsername);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> adminAuthAspect.checkAdminAuth()
        );
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    @DisplayName("空字符串用户名访问被拒绝")
    void whenUserWithEmptyUsername_thenAccessDenied() {
        // 准备用户名为空字符串的用户
        User userWithEmptyUsername = new User();
        userWithEmptyUsername.setUsername("");
        UserContext.setCurrentUser(userWithEmptyUsername);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> adminAuthAspect.checkAdminAuth()
        );
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    @DisplayName("访问被拒绝且支持不同语言环境")
    void whenAccessDeniedWithDifferentLocale_thenReturnLocalizedMessage() {
        // 设置中文语言环境
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        // 设置非管理员用户
        User normalUser = new User();
        normalUser.setUsername("normaluser");
        UserContext.setCurrentUser(normalUser);

        // 设置mock行为 - 只在测试需要时设置
        when(messageSource.getMessage(eq("error.access.denied"), isNull(), eq(Locale.SIMPLIFIED_CHINESE)))
                .thenReturn("访问被拒绝");

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminAuthAspect.checkAdminAuth();
        });

        assertEquals("访问被拒绝", exception.getMessage());
    }
} 