package com.vehiclestation.servicerecord.exception;

public class ServiceRecordNotFoundException extends RuntimeException {
    public ServiceRecordNotFoundException(String message) {
        super(message);
    }
}
