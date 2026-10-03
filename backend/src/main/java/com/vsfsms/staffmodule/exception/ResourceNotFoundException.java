package com.vsfsms.staffmodule.exception;

/** Thrown when a record with the requested ID does not exist. Mapped to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
