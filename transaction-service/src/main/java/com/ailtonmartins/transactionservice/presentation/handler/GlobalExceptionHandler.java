package com.ailtonmartins.transactionservice.presentation.handler;

import com.ailtonmartins.transactionservice.domain.exception.TransactionAlreadyFinishedException;
import com.ailtonmartins.transactionservice.domain.exception.TransactionNotFoundException;
import com.ailtonmartins.transactionservice.presentation.dto.response.ErrorResponse;
import com.ailtonmartins.transactionservice.presentation.dto.response.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(
            TransactionNotFoundException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(TransactionAlreadyFinishedException.class)
    public ResponseEntity<ErrorResponse> handleTransactionAlreadyFinished(
            TransactionAlreadyFinishedException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse response = ErrorResponse.withFields(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Requisicao invalida",
                request.getRequestURI(),
                fields
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "Parametro invalido: " + exception.getName(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }
}
