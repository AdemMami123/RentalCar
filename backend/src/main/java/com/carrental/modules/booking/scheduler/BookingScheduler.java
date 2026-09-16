package com.carrental.modules.booking.scheduler;

import com.carrental.modules.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for automatic booking status transitions
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {
    private final BookingService bookingService;

    /**
     * Run every 5 minutes: transition CONFIRMED bookings to ACTIVE when pickup date is reached
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000) // 5 minutes, 1 minute initial delay
    public void processAutoActivations() {
        try {
            log.debug("Starting automatic booking activations");
            bookingService.processAutoActivations();
        } catch (Exception e) {
            log.error("Error during automatic booking activations", e);
        }
    }

    /**
     * Run every 5 minutes: transition ACTIVE bookings to COMPLETED when dropoff date is reached
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 120000) // 5 minutes, 2 minutes initial delay
    public void processAutoCompletions() {
        try {
            log.debug("Starting automatic booking completions");
            bookingService.processAutoCompletions();
        } catch (Exception e) {
            log.error("Error during automatic booking completions", e);
        }
    }
}
