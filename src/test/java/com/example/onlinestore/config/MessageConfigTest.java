package com.example.onlinestore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.TestPropertySource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MessageConfigTest {

    @Autowired
    private MessageConfig messageConfig;

    @Autowired
    private MessageSource messageSource;

    @Test
    void testMessageConfigBeanCreation() {
        assertNotNull(messageConfig);
    }

    @Test
    void testMessageSourceBeanCreation() {
        MessageSource configuredMessageSource = messageConfig.messageSource();
        
        assertNotNull(configuredMessageSource);
    }

    @Test
    void testMessageSourceConfiguration() {
        String message = messageSource.getMessage("error.invalid.credentials", null, Locale.ENGLISH);
        
        assertNotNull(message);
    }
}
