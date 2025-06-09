package com.example.onlinestore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class RedisConfigTest {

    @Autowired
    private RedisConfig redisConfig;

    @Test
    void testRedisConfigBeanCreation() {
        assertNotNull(redisConfig);
    }

    @Test
    void testRedisTemplateBeanCreation() {
        RedisConnectionFactory connectionFactory = null;
        StringRedisTemplate stringRedisTemplate = redisConfig.stringRedisTemplate(connectionFactory);
        
        assertNotNull(stringRedisTemplate);
    }
}
