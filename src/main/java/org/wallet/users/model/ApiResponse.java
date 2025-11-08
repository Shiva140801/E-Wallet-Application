package org.wallet.users.model;


import org.springframework.http.HttpStatus;


public record ApiResponse<T>(Status status,
                             int code,
                             String message,
                             T data) {

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(Status.SUCCESS, HttpStatus.OK.value(), message, data);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(Status.SUCCESS, HttpStatus.CREATED.value(), message, data);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, T data, String message) {
        return new ApiResponse<>(Status.ERROR, status.value(), message, data);
    }

}

