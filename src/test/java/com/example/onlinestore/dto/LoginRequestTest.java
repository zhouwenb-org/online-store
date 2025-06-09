package com.example.onlinestore.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    class ValidationTests {

        @Test
        void testValidRequest() {
            LoginRequest request = new LoginRequest();
            request.setUsername("testuser");
            request.setPassword("password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testBlankUsername() {
            LoginRequest request = new LoginRequest();
            request.setUsername("");
            request.setPassword("password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        }

        @Test
        void testNullUsername() {
            LoginRequest request = new LoginRequest();
            request.setUsername(null);
            request.setPassword("password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        }

        @Test
        void testBlankPassword() {
            LoginRequest request = new LoginRequest();
            request.setUsername("testuser");
            request.setPassword("");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        void testNullPassword() {
            LoginRequest request = new LoginRequest();
            request.setUsername("testuser");
            request.setPassword(null);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }
    }

    @Nested
    class GetterSetterTests {

        @Test
        void testUsernameGetterSetter() {
            LoginRequest request = new LoginRequest();
            String username = "testuser";

            request.setUsername(username);

            assertEquals(username, request.getUsername());
        }

        @Test
        void testPasswordGetterSetter() {
            LoginRequest request = new LoginRequest();
            String password = "password123";

            request.setPassword(password);

            assertEquals(password, request.getPassword());
        }
    }
}
