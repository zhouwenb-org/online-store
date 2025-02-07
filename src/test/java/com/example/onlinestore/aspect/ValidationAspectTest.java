package com.example.onlinestore.aspect;

import com.example.onlinestore.dto.UserPageRequest;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
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
    void whenValidationFails_thenReturnBadRequest() throws Throwable {
        // 准备测试数据
        Object[] args = new Object[]{request};
        when(joinPoint.getArgs()).thenReturn(args);

        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<Object>> violations = mock(Set.class);
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        
        when(violations.isEmpty()).thenReturn(false);
        when(violations.iterator()).thenReturn(Set.of(violation).iterator());
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
} 