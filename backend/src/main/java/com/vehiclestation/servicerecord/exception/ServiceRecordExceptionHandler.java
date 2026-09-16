package com.vehiclestation.servicerecord.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ServiceRecordExceptionHandler {

    @ExceptionHandler(ServiceRecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ServiceRecordNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidServiceStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidState(InvalidServiceStateException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ServiceAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(ServiceAccessDeniedException exception) {
        return response(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
