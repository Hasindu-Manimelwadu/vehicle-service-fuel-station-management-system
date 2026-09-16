package com.vehiclestation.servicerecord.exception;

public class InvalidServiceStateException extends RuntimeException {
    public InvalidServiceStateException(String message) {
        super(message);
    }
}
