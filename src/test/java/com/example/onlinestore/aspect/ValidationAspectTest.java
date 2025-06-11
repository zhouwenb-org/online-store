package com.example.onlinestore.aspect;

import com.example.onlinestore.dto.CreateProductRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("参数验证切面测试")
public class ValidationAspectTest {

    @Mock
    private Validator validator;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private ConstraintViolation<Object> violation;

    @InjectMocks
    private ValidationAspect validationAspect;

    private CreateProductRequest validRequest;
    private CreateProductRequest invalidRequest;

    @BeforeEach
    void setUp() {
        // 准备有效请求
        validRequest = new CreateProductRequest();
        validRequest.setName("测试商品");
        validRequest.setCategory("电子产品");
        validRequest.setPrice(new BigDecimal("199.99"));

        // 准备无效请求
        invalidRequest = new CreateProductRequest();
        invalidRequest.setName(""); // 空名称
        invalidRequest.setCategory("");
        invalidRequest.setPrice(new BigDecimal("0")); // 价格太低
    }

    @Test
    @DisplayName("参数验证通过时继续执行")
    void whenValidationPasses_thenProceed() throws Throwable {
        // 设置mock行为
        when(joinPoint.getArgs()).thenReturn(new Object[]{validRequest});
        when(validator.validate(validRequest)).thenReturn(new HashSet<>());
        when(joinPoint.proceed()).thenReturn("success");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertEquals("success", result);
        verify(joinPoint).proceed();
        verify(validator).validate(validRequest);
    }

    @Test
    @DisplayName("参数验证失败时返回错误响应")
    void whenValidationFails_thenReturnBadRequest() throws Throwable {
        // 准备验证失败的情况
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);
        
        when(joinPoint.getArgs()).thenReturn(new Object[]{invalidRequest});
        when(validator.validate(any())).thenReturn(violations);
        when(violation.getMessage()).thenReturn("error.product.name.empty");
        when(messageSource.getMessage(eq("error.product.name.empty"), isNull(), any(Locale.class)))
                .thenReturn("商品名称不能为空");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(400, response.getStatusCode().value());
        assertEquals("商品名称不能为空", response.getBody());

        // 验证没有继续执行原方法
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("多个参数验证错误时合并错误消息")
    void whenMultipleValidationErrors_thenCombineMessages() throws Throwable {
        // 准备多个验证错误
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        
        ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);
        
        violations.add(violation1);
        violations.add(violation2);
        
        when(joinPoint.getArgs()).thenReturn(new Object[]{invalidRequest});
        when(validator.validate(any())).thenReturn(violations);
        
        when(violation1.getMessage()).thenReturn("error.product.name.empty");
        when(violation2.getMessage()).thenReturn("error.product.price.min");
        
        when(messageSource.getMessage(eq("error.product.name.empty"), isNull(), any(Locale.class)))
                .thenReturn("商品名称不能为空");
        when(messageSource.getMessage(eq("error.product.price.min"), isNull(), any(Locale.class)))
                .thenReturn("商品价格必须大于0.01");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(400, response.getStatusCode().value());
        
        String body = (String) response.getBody();
        assertTrue(body.contains("商品名称不能为空"));
        assertTrue(body.contains("商品价格必须大于0.01"));
    }

    @Test
    @DisplayName("空参数时继续执行")
    void whenNullParameter_thenProceed() throws Throwable {
        // 设置mock行为
        when(joinPoint.getArgs()).thenReturn(new Object[]{null});
        when(joinPoint.proceed()).thenReturn("success");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertEquals("success", result);
        verify(joinPoint).proceed();
        verify(validator, never()).validate(any());
    }

    @Test
    @DisplayName("无参数时继续执行")
    void whenNoParameters_thenProceed() throws Throwable {
        // 设置mock行为
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("success");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertEquals("success", result);
        verify(joinPoint).proceed();
        verify(validator, never()).validate(any());
    }

    @Test
    @DisplayName("混合参数（有效和无效）时验证失败")
    void whenMixedParameters_thenValidateAll() throws Throwable {
        // 准备混合参数，只使用一个会失败的参数
        Object[] args = {invalidRequest};
        
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);
        
        when(joinPoint.getArgs()).thenReturn(args);
        when(validator.validate(any())).thenReturn(violations);
        when(violation.getMessage()).thenReturn("error.product.name.empty");
        when(messageSource.getMessage(eq("error.product.name.empty"), isNull(), any(Locale.class)))
                .thenReturn("商品名称不能为空");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(400, response.getStatusCode().value());

        // 验证参数被验证了
        verify(validator).validate(invalidRequest);
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("本地化错误消息")
    void whenValidationFailsWithDifferentLocale_thenReturnLocalizedMessage() throws Throwable {
        // 设置中文语言环境
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);
        
        when(joinPoint.getArgs()).thenReturn(new Object[]{invalidRequest});
        when(validator.validate(any())).thenReturn(violations);
        when(violation.getMessage()).thenReturn("error.product.name.empty");
        when(messageSource.getMessage(eq("error.product.name.empty"), isNull(), eq(Locale.SIMPLIFIED_CHINESE)))
                .thenReturn("商品名称不能为空");

        // 执行测试
        Object result = validationAspect.validateParameters(joinPoint);

        // 验证结果
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals("商品名称不能为空", response.getBody());

        // 重置语言环境
        LocaleContextHolder.setLocale(Locale.getDefault());
    }
} 