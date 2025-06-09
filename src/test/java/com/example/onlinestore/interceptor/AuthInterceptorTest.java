package com.example.onlinestore.interceptor;

import com.example.onlinestore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

import com.example.onlinestore.model.User;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private java.io.PrintWriter printWriter;

    @Mock
    private org.springframework.context.MessageSource messageSource;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    private Object handler;

    @BeforeEach
    void setUp() throws Exception {
        handler = new Object();
        lenient().when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Unauthorized");
        lenient().when(response.getWriter()).thenReturn(printWriter);
    }

    @Nested
    class PreHandleTests {

        @Test
        void testPreHandle_ValidToken() throws Exception {
            String token = "valid-token";
            when(request.getHeader("X-Token")).thenReturn(token);
            when(userService.getUserByToken(token)).thenReturn(new User());

            boolean result = authInterceptor.preHandle(request, response, handler);

            assertTrue(result);
            verify(userService).getUserByToken(token);
        }

        @Test
        void testPreHandle_InvalidToken() throws Exception {
            String token = "invalid-token";
            when(request.getHeader("X-Token")).thenReturn(token);
            when(userService.getUserByToken(token)).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, handler);

            assertFalse(result);
            verify(userService).getUserByToken(token);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        @Test
        void testPreHandle_NoAuthorizationHeader() throws Exception {
            when(request.getHeader("X-Token")).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, handler);

            assertFalse(result);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(userService, never()).getUserByToken(anyString());
        }

        @Test
        void testPreHandle_InvalidAuthorizationFormat() throws Exception {
            when(request.getHeader("X-Token")).thenReturn("");
            when(userService.getUserByToken("")).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, handler);

            assertFalse(result);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(userService).getUserByToken("");
        }

        @Test
        void testPreHandle_EmptyToken() throws Exception {
            when(request.getHeader("X-Token")).thenReturn("");
            when(userService.getUserByToken("")).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, handler);

            assertFalse(result);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(userService).getUserByToken("");
        }

        @Test
        void testPreHandle_ServiceException() throws Exception {
            String token = "valid-token";
            when(request.getHeader("X-Token")).thenReturn(token);
            when(userService.getUserByToken(token)).thenThrow(new RuntimeException("Service error"));

            assertThrows(RuntimeException.class, () -> {
                authInterceptor.preHandle(request, response, handler);
            });

            verify(userService).getUserByToken(token);
        }
    }
}
