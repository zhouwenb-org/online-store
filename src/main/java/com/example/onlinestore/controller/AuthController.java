package com.example.onlinestore.controller;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.LoginResponse;
import com.example.onlinestore.dto.UserVO;
import com.example.onlinestore.model.User;
import com.example.onlinestore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 业务逻辑异常，返回400
            logger.warn("登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 系统异常，返回500
            logger.error("系统错误: ", e);
            return ResponseEntity.internalServerError().body(
                messageSource.getMessage("error.system.internal", null, LocaleContextHolder.getLocale()));
        }
    }

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami() {
        try {
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                logger.warn("未登录用户尝试访问whoami接口");
                return ResponseEntity.status(401).body(
                    messageSource.getMessage("error.unauthorized", null, LocaleContextHolder.getLocale()));
            }
            
            UserVO userVO = new UserVO();
            userVO.setId(currentUser.getId());
            userVO.setUsername(currentUser.getUsername());
            userVO.setCreatedAt(currentUser.getCreatedAt());
            userVO.setUpdatedAt(currentUser.getUpdatedAt());
            
            logger.debug("用户 {} 查询自己的信息", currentUser.getUsername());
            return ResponseEntity.ok(userVO);
        } catch (Exception e) {
            logger.error("获取当前用户信息失败: ", e);
            return ResponseEntity.internalServerError().body(
                messageSource.getMessage("error.system.internal", null, LocaleContextHolder.getLocale()));
        }
    }
} 