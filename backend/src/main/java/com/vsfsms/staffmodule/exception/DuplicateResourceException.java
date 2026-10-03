package com.vsfsms.staffmodule.exception;

/** Thrown when a create/update would violate a uniqueness rule. Mapped to HTTP 409. */
public class DuplicateResourceException extends RuntimeException {
    private final String field;

    public DuplicateResourceException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
