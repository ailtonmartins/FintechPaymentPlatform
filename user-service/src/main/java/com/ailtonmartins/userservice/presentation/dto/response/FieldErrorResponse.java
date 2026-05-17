package com.ailtonmartins.userservice.presentation.dto.response;

public record FieldErrorResponse(
        String field,
        String message
) {
}
