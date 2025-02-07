package com.example.onlinestore.aspect;

import com.example.onlinestore.dto.UserPageRequest;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("参数验证切面测试")
public class ValidationAspectTest {

    @Mock
    private Validator validator;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private ValidationAspect validationAspect;

    private UserPageRequest request;

    @BeforeEach
    void setUp() {
        request = new UserPageRequest();
        request.setPageNum(0); // 无效的页码
        request.setPageSize(200); // 无效的页大小
    }

    @Nested
    @DisplayName("参数验证测试")
    class ValidationTests {
        @Test
        @DisplayName("验证通过")
        void whenValidationPasses_thenProceedWithMethod() throws Throwable {
            // 准备测试数据
            Object[] args = new Object[]{request};
            when(joinPoint.getArgs()).thenReturn(args);
            when(validator.validate(any())).thenReturn(new HashSet<>());
            when(joinPoint.proceed()).thenReturn(ResponseEntity.ok().build());

            // 执行测试
            Object result = validationAspect.validateParameters(joinPoint);

            // 验证结果
            verify(joinPoint).proceed();
            assertNotNull(result);
            assertEquals(ResponseEntity.ok().build(), result);
        }

        @Test
        @DisplayName("验证失败")
        void whenValidationFails_thenReturnBadRequest() throws Throwable {
            // 准备测试数据
            Object[] args = new Object[]{request};
            when(joinPoint.getArgs()).thenReturn(args);

            Set<ConstraintViolation<Object>> violations = new HashSet<>();
            ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
            violations.add(violation);
            
            when(validator.validate(any())).thenReturn(violations);
            when(violation.getMessage()).thenReturn("error.page.number.min");
            when(messageSource.getMessage(eq("error.page.number.min"), any(), eq(LocaleContextHolder.getLocale())))
                .thenReturn("页码必须大于等于1");

            // 执行测试
            Object result = validationAspect.validateParameters(joinPoint);

            // 验证结果
            verify(joinPoint, never()).proceed();
            assertTrue(result instanceof ResponseEntity);
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            assertEquals(400, response.getStatusCode().value());
            assertEquals("页码必须大于等于1", response.getBody());
        }

        @Test
        @DisplayName("参数为空")
        void whenArgumentIsNull_thenSkipValidation() throws Throwable {
            // 准备测试数据
            Object[] args = new Object[]{null};
            when(joinPoint.getArgs()).thenReturn(args);
            when(joinPoint.proceed()).thenReturn(ResponseEntity.ok().build());

            // 执行测试
            Object result = validationAspect.validateParameters(joinPoint);

            // 验证结果
            verify(validator, never()).validate(any());
            verify(joinPoint).proceed();
            assertEquals(ResponseEntity.ok().build(), result);
        }

        @Test
        @DisplayName("多个验证错误")
        void whenMultipleValidationErrors_thenReturnAllErrors() throws Throwable {
            // 准备测试数据
            Object[] args = new Object[]{request};
            when(joinPoint.getArgs()).thenReturn(args);

            Set<ConstraintViolation<Object>> violations = new HashSet<>();
            ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
            ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);
            violations.add(violation1);
            violations.add(violation2);
            
            when(validator.validate(any())).thenReturn(violations);
            when(violation1.getMessage()).thenReturn("error.page.number.min");
            when(violation2.getMessage()).thenReturn("error.page.size.max");
            
            when(messageSource.getMessage(eq("error.page.number.min"), any(), eq(LocaleContextHolder.getLocale())))
                .thenReturn("页码必须大于等于1");
            when(messageSource.getMessage(eq("error.page.size.max"), any(), eq(LocaleContextHolder.getLocale())))
                .thenReturn("每页大小不能超过100");

            // 执行测试
            Object result = validationAspect.validateParameters(joinPoint);

            // 验证结果
            verify(joinPoint, never()).proceed();
            assertTrue(result instanceof ResponseEntity);
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            assertEquals(400, response.getStatusCode().value());
            String errorMessage = (String) response.getBody();
            assertTrue(errorMessage.contains("页码必须大于等于1"));
            assertTrue(errorMessage.contains("每页大小不能超过100"));
        }

        @Test
        @DisplayName("原方法抛出异常")
        void whenProceedThrowsException_thenRethrowException() throws Throwable {
            // 准备测试数据
            Object[] args = new Object[]{request};
            when(joinPoint.getArgs()).thenReturn(args);
            when(validator.validate(any())).thenReturn(new HashSet<>());
            when(joinPoint.proceed()).thenThrow(new RuntimeException("Test exception"));

            // 执行测试并验证异常
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> validationAspect.validateParameters(joinPoint));
            assertEquals("Test exception", exception.getMessage());
        }
    }
} 