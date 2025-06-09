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

class UserPageRequestTest {

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
            UserPageRequest request = new UserPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testDefaultValues() {
            UserPageRequest request = new UserPageRequest();

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
            assertEquals(1, request.getPageNum());
            assertEquals(10, request.getPageSize());
        }

        @Test
        void testInvalidPageNum_Zero() {
            UserPageRequest request = new UserPageRequest();
            request.setPageNum(0);

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pageNum")));
        }

        @Test
        void testInvalidPageNum_Negative() {
            UserPageRequest request = new UserPageRequest();
            request.setPageNum(-1);

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pageNum")));
        }

        @Test
        void testInvalidPageSize_Zero() {
            UserPageRequest request = new UserPageRequest();
            request.setPageSize(0);

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pageSize")));
        }

        @Test
        void testInvalidPageSize_TooLarge() {
            UserPageRequest request = new UserPageRequest();
            request.setPageSize(101);

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pageSize")));
        }

        @Test
        void testMaxValidPageSize() {
            UserPageRequest request = new UserPageRequest();
            request.setPageSize(100);

            Set<ConstraintViolation<UserPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class GetterSetterTests {

        @Test
        void testPageNumGetterSetter() {
            UserPageRequest request = new UserPageRequest();
            int pageNum = 5;

            request.setPageNum(pageNum);

            assertEquals(pageNum, request.getPageNum());
        }

        @Test
        void testPageSizeGetterSetter() {
            UserPageRequest request = new UserPageRequest();
            int pageSize = 20;

            request.setPageSize(pageSize);

            assertEquals(pageSize, request.getPageSize());
        }
    }
}
