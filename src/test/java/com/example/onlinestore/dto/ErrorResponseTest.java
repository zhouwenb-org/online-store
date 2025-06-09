package com.example.onlinestore.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Nested
    class GetterSetterTests {

        @Test
        void testMessageGetterSetter() {
            ErrorResponse errorResponse = new ErrorResponse("Initial message");
            String message = "Test error message";

            errorResponse.setMessage(message);

            assertEquals(message, errorResponse.getMessage());
        }
    }

    @Nested
    class ConstructorTests {

        @Test
        void testParameterizedConstructorOnly() {
            String message = "Test error message";
            ErrorResponse errorResponse = new ErrorResponse(message);

            assertNotNull(errorResponse);
            assertEquals(message, errorResponse.getMessage());
        }


    }

    @Nested
    class ObjectTests {

        @Test
        void testErrorResponseWithMessage() {
            String message = "Internal server error";
            ErrorResponse errorResponse = new ErrorResponse(message);

            assertEquals(message, errorResponse.getMessage());
        }

        @Test
        void testErrorResponseWithNullMessage() {
            ErrorResponse errorResponse = new ErrorResponse(null);

            assertNull(errorResponse.getMessage());
        }

        @Test
        void testErrorResponseWithEmptyMessage() {
            String message = "";
            ErrorResponse errorResponse = new ErrorResponse(message);

            assertEquals(message, errorResponse.getMessage());
            assertTrue(errorResponse.getMessage().isEmpty());
        }
    }
}
