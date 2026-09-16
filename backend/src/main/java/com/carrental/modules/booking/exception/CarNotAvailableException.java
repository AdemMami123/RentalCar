package com.carrental.modules.booking.exception;

/**
 * Exception thrown when a car is not available for the requested booking dates
 */
public class CarNotAvailableException extends RuntimeException {
    public CarNotAvailableException(String message) {
        super(message);
    }

    public CarNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
