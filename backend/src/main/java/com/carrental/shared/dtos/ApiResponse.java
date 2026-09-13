package com.carrental.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
    private boolean success;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message("Success")
                .data(data)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .data(data)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(null)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
