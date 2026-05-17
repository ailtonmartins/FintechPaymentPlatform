package com.ailtonmartins.userservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponse> fields
) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse withFields(
            int status,
            String error,
            String message,
            String path,
            List<FieldErrorResponse> fields
    ) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, fields);
    }
}
