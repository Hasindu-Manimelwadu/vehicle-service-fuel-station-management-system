package com.vehiclestation.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(
            DuplicateEmailException exception
    ) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", fieldErrors);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<Map<String, Object>> handleInactiveAccount(
            InactiveAccountException exception
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
}