package com.carrental.modules.booking.controller;

import com.carrental.modules.booking.dto.BookingDTO;
import com.carrental.modules.booking.service.BookingService;
import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.repository.UserRepository;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserRepository userRepository;

    /**
     * Get all bookings (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getAllBookings(), "Bookings retrieved successfully")
        );
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<BookingDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingById(id), "Booking retrieved successfully")
        );
    }

    /**
     * Get all bookings for a specific user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #userId.toString()")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingsByUser(userId), "Bookings retrieved successfully")
        );
    }

    /**
     * Get bookings by user and status
     */
    @GetMapping("/user/{userId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #userId.toString()")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.getBookingsByCar(carId), "Bookings retrieved successfully")
        );
    }

    /**
     * Get bookings by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
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
        int totalCarCount = bookingService.getTotalCarCount(carId);
        int availableCarCount = bookingService.getAvailableCarCount(carId, pickupDate, dropoffDate);

        Map<String, Object> result = new HashMap<>();
        result.put("available", isAvailable);
        result.put("rentalDays", rentalDays);
        result.put("totalCarCount", totalCarCount);
        result.put("availableCarCount", availableCarCount);

        if (isAvailable) {
            var totalCost = bookingService.calculateBookingCost(carId, pickupDate, dropoffDate);
            result.put("totalCost", totalCost);
        } else {
            result.put("suggestedDateRanges", bookingService.findAvailableDateSuggestions(carId, pickupDate, dropoffDate));
        }

        return ResponseEntity.ok(
                ApiResponse.success(result, "Availability checked successfully")
        );
    }

    /**
     * Create a new booking
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> create(
            @Valid @RequestBody BookingDTO dto,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authenticated user is required to create a booking");
        }

        User user = userRepository.findById(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new IllegalStateException("Authenticated user account was not found"));
        dto.setUserId(user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(bookingService.createBooking(dto), "Booking created successfully")
        );
    }

    /**
     * Confirm a booking (transition from PENDING to CONFIRMED)
     */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingDTO>> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.confirmBooking(id), "Booking confirmed successfully")
        );
    }

    /**
     * Activate a booking (transition from CONFIRMED to ACTIVE)
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingDTO>> activateBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.activateBooking(id), "Booking activated successfully")
        );
    }

    /**
     * Complete a booking (transition from ACTIVE to COMPLETED)
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.completeBooking(id), "Booking completed successfully")
        );
    }

    /**
     * Cancel a booking
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.cancelBooking(id), "Booking cancelled successfully")
        );
    }

    /**
     * Update booking (only PENDING bookings)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<BookingDTO>> update(@PathVariable Long id, @Valid @RequestBody BookingDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success(bookingService.updateBooking(id, dto), "Booking updated successfully")
        );
    }

    /**
     * Delete booking (only CANCELLED bookings)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Booking deleted successfully"));
    }
}