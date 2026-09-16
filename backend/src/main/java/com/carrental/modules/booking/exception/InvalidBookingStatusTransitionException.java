package com.carrental.modules.booking.exception;

/**
 * Exception thrown when attempting an invalid booking status transition
 */
public class InvalidBookingStatusTransitionException extends RuntimeException {
    public InvalidBookingStatusTransitionException(String message) {
        super(message);
    }

    public InvalidBookingStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
