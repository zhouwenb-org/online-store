package com.example.onlinestore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    void testMessageSourceLocalization() {
        // 测试英文消息
        String enMessage = messageSource.getMessage("error.invalid.credentials", null, Locale.ENGLISH);
        assertEquals("Invalid username or password", enMessage, "English message should match");

        // 测试中文消息
        String zhMessage = messageSource.getMessage("error.invalid.credentials", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("用户名或密码错误", zhMessage, "Chinese message should match");

        // 测试系统错误消息
        String enSystemError = messageSource.getMessage("error.system.internal", null, Locale.ENGLISH);
        assertEquals("Internal server error", enSystemError, "English system error message should match");

        String zhSystemError = messageSource.getMessage("error.system.internal", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("系统内部错误", zhSystemError, "Chinese system error message should match");
    }

    @Test
    void testValidationMessages() {
        // 测试英文验证消息
        String enPageNumError = messageSource.getMessage("error.page.number.min", null, Locale.ENGLISH);
        assertEquals("Page number must be greater than or equal to 1", enPageNumError);

        String enPageSizeError = messageSource.getMessage("error.page.size.max", null, Locale.ENGLISH);
        assertEquals("Page size must be less than or equal to 100", enPageSizeError);

        // 测试中文验证消息
        String zhPageNumError = messageSource.getMessage("error.page.number.min", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("页码必须大于等于1", zhPageNumError);

        String zhPageSizeError = messageSource.getMessage("error.page.size.max", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("每页大小不能超过100", zhPageSizeError);
    }

    @Test
    void testMessageWithDefaultValue() {
        // 测试使用默认值
        String defaultMessage = "Default Error Message";
        String message = messageSource.getMessage(
            "non.existent.message", 
            null, 
            defaultMessage, 
            Locale.ENGLISH);
        assertEquals(defaultMessage, message);
    }

    @Test
    void testMessageWithParameters() {
        // 测试带参数的消息
        Object[] params = new Object[]{"admin", "read"};
        String message = messageSource.getMessage(
            "error.permission.denied", 
            params, 
            "Permission denied", 
            Locale.ENGLISH);
        assertNotNull(message);
    }

    @Test
    void testNonExistentMessage() {
        // 测试不存在的消息键
        assertThrows(NoSuchMessageException.class, () -> 
            messageSource.getMessage("non.existent.message", null, Locale.ENGLISH));
    }
} 