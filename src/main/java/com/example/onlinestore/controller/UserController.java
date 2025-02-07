package com.example.onlinestore.controller;

import com.example.onlinestore.annotation.RequireAdmin;
import com.example.onlinestore.annotation.ValidateParams;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.UserPageRequest;
import com.example.onlinestore.dto.UserVO;
import com.example.onlinestore.dto.ErrorResponse;
import com.example.onlinestore.model.User;
import com.example.onlinestore.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    @RequireAdmin
    @ValidateParams
    public ResponseEntity<?> listUsers(@Valid UserPageRequest request) {
        try {
            PageResponse<UserVO> response = userService.listUsers(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询用户列表失败", e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }
} 