package com.sliit.vehicleservice.fuelinventory.exception;

public class InvalidStockOperationException extends RuntimeException {
    public InvalidStockOperationException(String message) {
        super(message);
    }
}