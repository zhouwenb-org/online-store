package com.example.onlinestore.config;

import com.example.onlinestore.interceptor.AuthInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class WebConfigTest {

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private AuthInterceptor authInterceptor;

    @Test
    void testWebConfigBeanCreation() {
        assertNotNull(webConfig);
    }

    @Test
    void testAuthInterceptorBeanCreation() {
        assertNotNull(authInterceptor);
    }

    @Test
    void testAddInterceptors() {
        InterceptorRegistry registry = new InterceptorRegistry();
        
        assertDoesNotThrow(() -> webConfig.addInterceptors(registry));
    }
}
