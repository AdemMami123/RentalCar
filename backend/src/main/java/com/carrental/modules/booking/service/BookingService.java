package com.carrental.modules.booking.service;

import com.carrental.modules.booking.dto.BookingDTO;
import com.carrental.modules.booking.dto.BookingCustomerDTO;
import com.carrental.modules.booking.entity.Booking;
import com.carrental.modules.booking.exception.CarNotAvailableException;
import com.carrental.modules.booking.exception.InvalidBookingStatusTransitionException;
import com.carrental.modules.booking.mapper.BookingMapper;
import com.carrental.modules.booking.repository.BookingRepository;
import com.carrental.modules.car.entity.Car;
import com.carrental.modules.car.repository.CarRepository;
import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.repository.UserRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing car rental bookings with comprehensive business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    /**
     * Get all bookings (admin only)
     */
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
            .map(this::toDTOWithCustomer)
                .toList();
    }

    /**
     * Get booking by ID
     */
    public BookingDTO getBookingById(Long id) {
        return toDTOWithCustomer(
                bookingRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id))
        );
    }

    /**
     * Get all bookings for a specific user
     */
    public List<BookingDTO> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
            .map(this::toDTOWithCustomer)
                .toList();
    }

    /**
     * Get bookings by user and status
     */
    public List<BookingDTO> getBookingsByUserAndStatus(Long userId, String status) {
        Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
        return bookingRepository.findByUserIdAndBookingStatus(userId, bookingStatus).stream()
            .map(this::toDTOWithCustomer)
                .toList();
    }

    /**
     * Get all bookings for a specific car
     */
    public List<BookingDTO> getBookingsByCar(Long carId) {
        return bookingRepository.findByCarId(carId).stream()
            .map(this::toDTOWithCustomer)
                .toList();
    }

    /**
     * Get bookings by status
     */
    public List<BookingDTO> getBookingsByStatus(String status) {
        Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
        return bookingRepository.findByBookingStatus(bookingStatus).stream()
            .map(this::toDTOWithCustomer)
                .toList();
    }

    /**
     * Check if a car is available for the requested date range
     */
    public boolean isCarAvailable(Long carId, LocalDateTime pickupDate, LocalDateTime dropoffDate) {
        return getAvailableCarCount(carId, pickupDate, dropoffDate) > 0;
    }

    public int getTotalCarCount(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
        return "AVAILABLE".equals(car.getStatus().name()) ? car.getFleetQuantity() : 0;
    }

    public int getAvailableCarCount(Long carId, LocalDateTime pickupDate, LocalDateTime dropoffDate) {
        int totalCarCount = getTotalCarCount(carId);
        int bookedCarCount = bookingRepository.findConflictingBookings(carId, pickupDate, dropoffDate).size();
        return Math.max(totalCarCount - bookedCarCount, 0);
    }

    public List<Map<String, String>> findAvailableDateSuggestions(
            Long carId, LocalDateTime pickupDate, LocalDateTime dropoffDate) {
        long rentalDays = Math.max(calculateRentalDays(pickupDate, dropoffDate), 1);
        List<Map<String, String>> suggestions = new ArrayList<>();

        for (int offset = 1; offset <= 90 && suggestions.size() < 5; offset++) {
            LocalDateTime suggestedPickup = pickupDate.plusDays(offset);
            LocalDateTime suggestedDropoff = suggestedPickup.plusDays(rentalDays);
            if (getAvailableCarCount(carId, suggestedPickup, suggestedDropoff) > 0) {
                Map<String, String> suggestion = new LinkedHashMap<>();
                suggestion.put("pickupDate", suggestedPickup.toString());
                suggestion.put("dropoffDate", suggestedDropoff.toString());
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    public boolean isCarAvailableExcludingBooking(Long carId, LocalDateTime pickupDate,
                                                   LocalDateTime dropoffDate, Long bookingId) {
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
        int bookedCarCount = bookingRepository.findConflictingBookingsExcludingBooking(
            carId, pickupDate, dropoffDate, bookingId
        ).size();
        return "AVAILABLE".equals(car.getStatus().name()) && bookedCarCount < car.getFleetQuantity();
    }

    /**
     * Calculate the rental duration in days
     */
    public long calculateRentalDays(LocalDateTime pickupDate, LocalDateTime dropoffDate) {
        return ChronoUnit.DAYS.between(pickupDate.toLocalDate(), dropoffDate.toLocalDate());
    }

    /**
     * Calculate the total cost of the booking
     */
    public BigDecimal calculateBookingCost(Long carId, LocalDateTime pickupDate, LocalDateTime dropoffDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));

        long rentalDays = calculateRentalDays(pickupDate, dropoffDate);
        // Ensure at least 1 day rental
        rentalDays = Math.max(rentalDays, 1);

        return car.getDailyRate().multiply(BigDecimal.valueOf(rentalDays));
    }

    /**
     * Create a new booking with availability check and price calculation
     */
    @Transactional
    public BookingDTO createBooking(BookingDTO dto) {
        log.info("Creating booking for car {} from {} to {}", dto.getCarId(), dto.getPickupDate(), dto.getDropoffDate());

        // Validate dates
        if (dto.getDropoffDate().isBefore(dto.getPickupDate())) {
            throw new IllegalArgumentException("Dropoff date must be after pickup date");
        }

        // Check car availability
        if (!isCarAvailable(dto.getCarId(), dto.getPickupDate(), dto.getDropoffDate())) {
            throw new CarNotAvailableException(
                    "This vehicle is no longer available for the selected dates."
            );
        }

        // Generate booking number if not provided
        if (dto.getBookingNumber() == null || dto.getBookingNumber().isBlank()) {
            dto.setBookingNumber("BK-" + System.currentTimeMillis());
        }

        if (bookingRepository.existsByBookingNumber(dto.getBookingNumber())) {
            throw new IllegalArgumentException("Booking number already exists");
        }

        // Calculate total cost
        BigDecimal totalCost = calculateBookingCost(
                dto.getCarId(),
                dto.getPickupDate(),
                dto.getDropoffDate()
        );
        dto.setTotalCost(totalCost.doubleValue());

        // Set initial status
        if (dto.getBookingStatus() == null) {
            dto.setBookingStatus(Booking.BookingStatus.PENDING.toString());
        }

        Booking booking = bookingMapper.toEntity(dto);
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return toDTOWithCustomer(savedBooking);
    }

    /**
     * Confirm a booking (transition from PENDING to CONFIRMED)
     */
    @Transactional
    public BookingDTO confirmBooking(Long id) {
        log.info("Confirming booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getBookingStatus() != Booking.BookingStatus.PENDING) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking cannot be confirmed from " + booking.getBookingStatus() + " status"
            );
        }

        // Re-check availability to ensure car is still available
        if (!isCarAvailableExcludingBooking(booking.getCarId(), booking.getPickupDate(), booking.getDropoffDate(), booking.getId())) {
            throw new CarNotAvailableException(
                    "This vehicle is no longer available for the selected dates."
            );
        }

        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking confirmed successfully");
        return toDTOWithCustomer(updatedBooking);
    }

    /**
     * Activate a booking (transition from CONFIRMED to ACTIVE)
     */
    @Transactional
    public BookingDTO activateBooking(Long id) {
        log.info("Activating booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getBookingStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking cannot be activated from " + booking.getBookingStatus() + " status"
            );
        }

        booking.setBookingStatus(Booking.BookingStatus.ACTIVE);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking activated successfully");
        return toDTOWithCustomer(updatedBooking);
    }

    /**
     * Complete a booking (transition from ACTIVE to COMPLETED)
     */
    @Transactional
    public BookingDTO completeBooking(Long id) {
        log.info("Completing booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getBookingStatus() != Booking.BookingStatus.ACTIVE) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking cannot be completed from " + booking.getBookingStatus() + " status"
            );
        }

        booking.setBookingStatus(Booking.BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking completed successfully");
        return toDTOWithCustomer(updatedBooking);
    }

    /**
     * Cancel a booking (from PENDING or CONFIRMED only)
     */
    @Transactional
    public BookingDTO cancelBooking(Long id) {
        log.info("Cancelling booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getBookingStatus() != Booking.BookingStatus.PENDING &&
                booking.getBookingStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new InvalidBookingStatusTransitionException(
                    "Booking cannot be cancelled from " + booking.getBookingStatus() + " status"
            );
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking cancelled successfully");
        return toDTOWithCustomer(updatedBooking);
    }

    /**
     * Update booking (only certain fields can be updated depending on status)
     */
    @Transactional
    public BookingDTO updateBooking(Long id, BookingDTO dto) {
        log.info("Updating booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Only allow updates for PENDING bookings
        if (booking.getBookingStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only PENDING bookings can be updated"
            );
        }

        // If dates are being changed, validate new dates
        if (dto.getPickupDate() != null && dto.getDropoffDate() != null) {
            if (dto.getDropoffDate().isBefore(dto.getPickupDate())) {
                throw new IllegalArgumentException("Dropoff date must be after pickup date");
            }

            // Check availability for new dates
            if (!isCarAvailableExcludingBooking(booking.getCarId(), dto.getPickupDate(), dto.getDropoffDate(), booking.getId())) {
                throw new CarNotAvailableException(
                        "This vehicle is no longer available for the selected dates."
                );
            }

            booking.setPickupDate(dto.getPickupDate());
            booking.setDropoffDate(dto.getDropoffDate());

            // Recalculate cost
            BigDecimal newCost = calculateBookingCost(
                    booking.getCarId(),
                    dto.getPickupDate(),
                    dto.getDropoffDate()
            );
            booking.setTotalCost(newCost);
        }

        if (dto.getPickupLocationId() != null) {
            booking.setPickupLocationId(dto.getPickupLocationId());
        }
        if (dto.getDropoffLocationId() != null) {
            booking.setDropoffLocationId(dto.getDropoffLocationId());
        }
        if (dto.getNotes() != null) {
            booking.setNotes(dto.getNotes());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully");
        return toDTOWithCustomer(updatedBooking);
    }

    /**
     * Delete a booking (only CANCELLED bookings can be deleted)
     */
    @Transactional
    public void deleteBooking(Long id) {
        log.info("Deleting booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Only allow deletion of cancelled bookings
        if (booking.getBookingStatus() != Booking.BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Only cancelled bookings can be deleted"
            );
        }

        bookingRepository.delete(booking);
        log.info("Booking deleted successfully");
    }

    /**
     * Auto-transition bookings from CONFIRMED to ACTIVE (scheduler job)
     */
    @Transactional
    public void processAutoActivations() {
        log.info("Processing auto-activations");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> readyForActivation = bookingRepository.findBookingsReadyForActivation(now);

        for (Booking booking : readyForActivation) {
            booking.setBookingStatus(Booking.BookingStatus.ACTIVE);
            bookingRepository.save(booking);
            log.info("Auto-activated booking ID: {}", booking.getId());
        }
    }

    /**
     * Auto-transition bookings from ACTIVE to COMPLETED (scheduler job)
     */
    @Transactional
    public void processAutoCompletions() {
        log.info("Processing auto-completions");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> readyForCompletion = bookingRepository.findBookingsReadyForCompletion(now);

        for (Booking booking : readyForCompletion) {
            booking.setBookingStatus(Booking.BookingStatus.COMPLETED);
            bookingRepository.save(booking);
            log.info("Auto-completed booking ID: {}", booking.getId());
        }
    }

    public boolean isOwner(Long bookingId, String username) {
        try {
            Long userId = Long.valueOf(username);
            return bookingRepository.findById(bookingId)
                    .map(booking -> booking.getUserId().equals(userId))
                    .orElse(false);
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private BookingDTO toDTOWithCustomer(Booking booking) {
        BookingDTO dto = bookingMapper.toDTO(booking);
        userRepository.findById(booking.getUserId()).ifPresent(user ->
                dto.setCustomer(BookingCustomerDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .phone(user.getPhone())
                        .licenseNumber(user.getLicenseNumber())
                        .licenseExpiry(user.getLicenseExpiry())
                        .address(user.getAddress())
                        .city(user.getCity())
                        .country(user.getCountry())
                        .build())
        );
        return dto;
    }
}