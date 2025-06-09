package com.example.onlinestore.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Nested
    class GetterSetterTests {

        @Test
        void testIdGetterSetter() {
            User user = new User();
            Long id = 1L;

            user.setId(id);

            assertEquals(id, user.getId());
        }

        @Test
        void testUsernameGetterSetter() {
            User user = new User();
            String username = "testuser";

            user.setUsername(username);

            assertEquals(username, user.getUsername());
        }

        @Test
        void testTokenGetterSetter() {
            User user = new User();
            String token = "test-token";

            user.setToken(token);

            assertEquals(token, user.getToken());
        }

        @Test
        void testTokenExpireTimeGetterSetter() {
            User user = new User();
            LocalDateTime expireTime = LocalDateTime.now();

            user.setTokenExpireTime(expireTime);

            assertEquals(expireTime, user.getTokenExpireTime());
        }

        @Test
        void testCreatedAtGetterSetter() {
            User user = new User();
            LocalDateTime createdAt = LocalDateTime.now();

            user.setCreatedAt(createdAt);

            assertEquals(createdAt, user.getCreatedAt());
        }

        @Test
        void testUpdatedAtGetterSetter() {
            User user = new User();
            LocalDateTime updatedAt = LocalDateTime.now();

            user.setUpdatedAt(updatedAt);

            assertEquals(updatedAt, user.getUpdatedAt());
        }
    }

    @Nested
    class ObjectTests {

        @Test
        void testUserCreation() {
            User user = new User();

            assertNotNull(user);
            assertNull(user.getId());
            assertNull(user.getUsername());
            assertNull(user.getToken());
            assertNull(user.getTokenExpireTime());
            assertNull(user.getCreatedAt());
            assertNull(user.getUpdatedAt());
        }

        @Test
        void testUserWithAllFields() {
            User user = new User();
            Long id = 1L;
            String username = "testuser";
            String token = "test-token";
            LocalDateTime expireTime = LocalDateTime.now();
            LocalDateTime now = LocalDateTime.now();

            user.setId(id);
            user.setUsername(username);
            user.setToken(token);
            user.setTokenExpireTime(expireTime);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            assertEquals(id, user.getId());
            assertEquals(username, user.getUsername());
            assertEquals(token, user.getToken());
            assertEquals(expireTime, user.getTokenExpireTime());
            assertEquals(now, user.getCreatedAt());
            assertEquals(now, user.getUpdatedAt());
        }
    }
}
