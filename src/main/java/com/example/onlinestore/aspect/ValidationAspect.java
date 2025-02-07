package com.example.onlinestore.aspect;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class ValidationAspect {

    @Autowired
    private Validator validator;

    @Autowired
    private MessageSource messageSource;

    @Around("@annotation(com.example.onlinestore.annotation.ValidateParams)")
    public Object validateParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        
        // 遍历所有参数进行校验
        for (Object arg : args) {
            if (arg != null) {
                Set<ConstraintViolation<Object>> violations = validator.validate(arg);
                if (!violations.isEmpty()) {
                    // 获取第一个错误消息
                    ConstraintViolation<Object> violation = violations.iterator().next();
                    String messageKey = violation.getMessage();
                    String errorMessage = messageSource.getMessage(
                        messageKey, null, LocaleContextHolder.getLocale());
                    return ResponseEntity.badRequest().body(errorMessage);
                }
            }
        }
        
        // 校验通过，继续执行原方法
        return joinPoint.proceed();
    }
} 