package com.example.onlinestore.mapper;

import com.example.onlinestore.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Sql(scripts = "/sql/clean-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testInsertUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setToken("test-token");
        user.setTokenExpireTime(LocalDateTime.now().plusHours(1));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insertUser(user);

        assertNotNull(user.getId());
        assertTrue(user.getId() > 0);
    }

    @Test
    @Sql(scripts = {"/sql/clean-users.sql", "/sql/insert-test-users.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindByUsername() {
        User user = userMapper.findByUsername("admin");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    @Sql(scripts = {"/sql/clean-users.sql", "/sql/insert-test-users.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindByUsernameNotFound() {
        User user = userMapper.findByUsername("nonexistent");

        assertNull(user);
    }

    @Test
    @Sql(scripts = {"/sql/clean-users.sql", "/sql/insert-test-users.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindWithPagination() {
        List<User> users = userMapper.findAllWithPagination(0, 2);

        assertNotNull(users);
        assertTrue(users.size() <= 2);
    }

    @Test
    @Sql(scripts = {"/sql/clean-users.sql", "/sql/insert-test-users.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testCountTotal() {
        long count = userMapper.countTotal();

        assertTrue(count > 0);
    }
}
