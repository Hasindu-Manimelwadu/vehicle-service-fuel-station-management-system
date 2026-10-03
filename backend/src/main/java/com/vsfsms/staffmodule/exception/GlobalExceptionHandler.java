package com.vsfsms.staffmodule.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Turns every error into the same JSON shape:
 * <pre>
 * { "timestamp": "...", "status": 400, "error": "Validation failed",
 *   "fields": { "email": "Email must be a valid email address" } }
 * </pre>
 * "error" is always a human-readable message; "fields" is present when the
 * problem can be pinned to specific inputs.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bean Validation (@NotBlank, @Pattern, ...) failures on @Valid request bodies
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed. Please correct the highlighted fields.", fields);
    }

    // Business-rule failures raised in the service layer
    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), fieldMap(ex.getField(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), fieldMap(ex.getField(), ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // Malformed JSON, bad date/time format, unknown enum value, text in a number field ...
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && !ife.getPath().isEmpty()) {
            String field = ife.getPath().get(ife.getPath().size() - 1).getFieldName();
            String msg = "Invalid value '" + ife.getValue() + "' for " + field;
            return build(HttpStatus.BAD_REQUEST, msg, fieldMap(field, msg));
        }
        if (cause instanceof JsonMappingException jme && !jme.getPath().isEmpty()) {
            String field = jme.getPath().get(jme.getPath().size() - 1).getFieldName();
            String msg = "Invalid value for " + field;
            return build(HttpStatus.BAD_REQUEST, msg, fieldMap(field, msg));
        }
        return build(HttpStatus.BAD_REQUEST, "Request body is missing or is not valid JSON.", null);
    }

    // Last line of defence for DB constraints (unique keys, FKs, CHECKs)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT,
                "The operation conflicts with existing data (duplicate value or invalid reference).", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        // Spring's own errors (405 wrong method, 404 unknown path, 415 ...) keep their status
        if (ex instanceof ErrorResponse er) {
            HttpStatus st = HttpStatus.valueOf(er.getStatusCode().value());
            return build(st, st.getReasonPhrase(), null);
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error: " + ex.getMessage(), null);
    }

    // ------------------------------------------------------------------
    private static Map<String, String> fieldMap(String field, String msg) {
        if (field == null) return null;
        Map<String, String> m = new LinkedHashMap<>();
        m.put(field, msg);
        return m;
    }

    private static ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, Map<String, String> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        if (fields != null && !fields.isEmpty()) body.put("fields", fields);
        return ResponseEntity.status(status).body(body);
    }
}
