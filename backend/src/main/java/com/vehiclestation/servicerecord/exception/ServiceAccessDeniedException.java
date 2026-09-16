package com.vehiclestation.servicerecord.exception;

public class ServiceAccessDeniedException extends RuntimeException {
    public ServiceAccessDeniedException(String message) {
        super(message);
    }
}
