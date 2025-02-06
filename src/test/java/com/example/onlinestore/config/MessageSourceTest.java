package com.example.onlinestore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
} 