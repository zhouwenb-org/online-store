package com.example.onlinestore.utils;

import com.example.onlinestore.dto.Response;
import org.apache.http.HttpStatus;

public class ResponseUtils {
    public static <T> Response<T> success(T data) {
        return new Response<>(data, "", HttpStatus.SC_OK);
    }

    public static <T> Response<T> fail(int code, String message) {
        return new Response<>(null, message, code);
    }
}
