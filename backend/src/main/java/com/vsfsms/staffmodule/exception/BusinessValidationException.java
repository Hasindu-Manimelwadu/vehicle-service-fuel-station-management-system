package com.vsfsms.staffmodule.exception;

/**
 * Thrown when a request is well-formed but breaks a business rule
 * (e.g. check-out before check-in, deductions larger than pay).
 * Mapped to HTTP 400. {@code field} lets the frontend highlight the input.
 */
public class BusinessValidationException extends RuntimeException {
    private final String field;

    public BusinessValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
