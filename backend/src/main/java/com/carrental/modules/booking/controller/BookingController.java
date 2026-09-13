package com.carrental.modules.booking.controller;

import com.carrental.modules.booking.dto.BookingDTO;
import com.carrental.modules.booking.service.BookingService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/bookings") @RequiredArgsConstructor @CrossOrigin(origins = "http://localhost:4200")
public class BookingController {
    private final BookingService bookingService;
    @GetMapping public ResponseEntity<ApiResponse<List<BookingDTO>>> getAll() { return ResponseEntity.ok(ApiResponse.success(bookingService.getAllBookings(), "Bookings retrieved successfully")); }
    @GetMapping("/{id}") public ResponseEntity<ApiResponse<BookingDTO>> getById(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingById(id), "Booking retrieved successfully")); }
    @GetMapping("/user/{userId}") public ResponseEntity<ApiResponse<List<BookingDTO>>> getByUser(@PathVariable Long userId) { return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsByUser(userId), "Bookings retrieved successfully")); }
    @GetMapping("/car/{carId}") public ResponseEntity<ApiResponse<List<BookingDTO>>> getByCar(@PathVariable Long carId) { return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsByCar(carId), "Bookings retrieved successfully")); }
    @PostMapping public ResponseEntity<ApiResponse<BookingDTO>> create(@Valid @RequestBody BookingDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(bookingService.createBooking(dto), "Booking created successfully")); }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<BookingDTO>> update(@PathVariable Long id, @Valid @RequestBody BookingDTO dto) { return ResponseEntity.ok(ApiResponse.success(bookingService.updateBooking(id, dto), "Booking updated successfully")); }
    @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) { bookingService.deleteBooking(id); return ResponseEntity.ok(ApiResponse.success(null, "Booking deleted successfully")); }
}