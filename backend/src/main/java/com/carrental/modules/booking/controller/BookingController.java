package com.carrental.modules.booking.controller;

import com.carrental.modules.booking.dto.BookingDTO;
import com.carrental.modules.booking.service.BookingService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for booking management
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {
    private final BookingService bookingService;

    /**
     * Get all bookings (admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getAllBookings(), "Bookings retrieved successfully")
        );
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingById(id), "Booking retrieved successfully")
        );
    }

    /**
     * Get all bookings for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingsByUser(userId), "Bookings retrieved successfully")
        );
    }

    /**
     * Get bookings by user and status
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        bookingService.getBookingsByUserAndStatus(userId, status),
                        "Bookings retrieved successfully"
                )
        );
    }

    /**
     * Get all bookings for a specific car
     */
    @GetMapping("/car/{carId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingsByCar(carId), "Bookings retrieved successfully")
        );
    }

    /**
     * Get bookings by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingsByStatus(status), "Bookings retrieved successfully")
        );
    }

    /**
     * Check if a car is available for the requested date range
     */
    @GetMapping("/availability/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAvailability(
            @RequestParam Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dropoffDate) {

        boolean isAvailable = bookingService.isCarAvailable(carId, pickupDate, dropoffDate);
        long rentalDays = bookingService.calculateRentalDays(pickupDate, dropoffDate);

        Map<String, Object> result = new HashMap<>();
        result.put("available", isAvailable);
        result.put("rentalDays", rentalDays);

        if (isAvailable) {
            var totalCost = bookingService.calculateBookingCost(carId, pickupDate, dropoffDate);
            result.put("totalCost", totalCost);
        }

        return ResponseEntity.ok(
                ApiResponse.success(result, "Availability checked successfully")
        );
    }

    /**
     * Create a new booking
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> create(@Valid @RequestBody BookingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(bookingService.createBooking(dto), "Booking created successfully")
        );
    }

    /**
     * Confirm a booking (transition from PENDING to CONFIRMED)
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<BookingDTO>> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.confirmBooking(id), "Booking confirmed successfully")
        );
    }

    /**
     * Activate a booking (transition from CONFIRMED to ACTIVE)
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BookingDTO>> activateBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.activateBooking(id), "Booking activated successfully")
        );
    }

    /**
     * Complete a booking (transition from ACTIVE to COMPLETED)
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.completeBooking(id), "Booking completed successfully")
        );
    }

    /**
     * Cancel a booking
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.cancelBooking(id), "Booking cancelled successfully")
        );
    }

    /**
     * Update booking (only PENDING bookings)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDTO>> update(@PathVariable Long id, @Valid @RequestBody BookingDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.updateBooking(id, dto), "Booking updated successfully")
        );
    }

    /**
     * Delete booking (only CANCELLED bookings)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Booking deleted successfully"));
    }
}