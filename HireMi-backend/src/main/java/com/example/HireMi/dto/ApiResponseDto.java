package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private Object error;

    // Static factory methods
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return new ApiResponseDto<T>(true, message, data, null);
    }

    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<T>(true, message, null, null);
    }

    public static <T> ApiResponseDto<T> error(String error) {
        return new ApiResponseDto<T>(false, null, null, error);
    }

    public static <T> ApiResponseDto<T> error(String message, Object error) {
        return new ApiResponseDto<T>(false, message, null, error);
    }
}
