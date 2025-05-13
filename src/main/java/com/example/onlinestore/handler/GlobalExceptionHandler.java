package com.example.onlinestore.handler;

import com.example.onlinestore.dto.Response;
import com.example.onlinestore.utils.ResponseUtils;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Response<String> handleException(Exception e) {
        return ResponseUtils.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
