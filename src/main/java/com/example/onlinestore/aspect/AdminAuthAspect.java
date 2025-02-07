package com.example.onlinestore.aspect;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.model.User;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminAuthAspect {

    @Value("${admin.auth.username}")
    protected String adminUsername;

    @Autowired
    private MessageSource messageSource;

    @Before("@annotation(com.example.onlinestore.annotation.RequireAdmin)")
    public void checkAdminAuth() {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null || !adminUsername.equals(currentUser.getUsername())) {
            throw new IllegalArgumentException(messageSource.getMessage(
                "error.access.denied", null, LocaleContextHolder.getLocale()));
        }
    }
} 