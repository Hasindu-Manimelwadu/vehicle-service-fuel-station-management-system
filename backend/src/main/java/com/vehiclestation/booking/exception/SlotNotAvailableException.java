package com.vehiclestation.booking.exception;

public class SlotNotAvailableException extends RuntimeException {

    public SlotNotAvailableException(String message) {
        super(message);
    }
}
