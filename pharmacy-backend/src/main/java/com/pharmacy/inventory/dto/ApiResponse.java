package com.pharmacy.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard response envelope. Every controller in the project (all 4 modules)
 * should return ApiResponse<T> so the frontend has one consistent shape to parse.
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
