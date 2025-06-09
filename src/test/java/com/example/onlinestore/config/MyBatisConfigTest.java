package com.example.onlinestore.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MyBatisConfigTest {

    @Autowired
    private MyBatisConfig myBatisConfig;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void testMyBatisConfigBeanCreation() {
        assertNotNull(myBatisConfig);
    }

    @Test
    void testSqlSessionFactoryBeanCreation() {
        assertNotNull(sqlSessionFactory);
    }
}
