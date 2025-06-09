package com.example.onlinestore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RestTemplateConfigTest {

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    @Test
    void testRestTemplateConfigBeanCreation() {
        assertNotNull(restTemplateConfig);
    }

    @Test
    void testRestTemplateBeanCreation() {
        RestTemplate restTemplate = restTemplateConfig.restTemplate();
        
        assertNotNull(restTemplate);
    }
}
