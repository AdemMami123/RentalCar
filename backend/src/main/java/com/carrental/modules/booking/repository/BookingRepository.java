package com.carrental.modules.booking.repository;

import com.carrental.modules.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    boolean existsByBookingNumber(String bookingNumber);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCarId(Long carId);
    List<Booking> findByBookingStatus(Booking.BookingStatus status);

    /**
     * Find bookings that conflict with the requested date range for a specific car.
     * Returns bookings that overlap with the requested pickup and dropoff dates,
     * excluding cancelled bookings.
     */
    @Query("SELECT b FROM Booking b WHERE b.carId = :carId " +
           "AND b.bookingStatus != 'CANCELLED' " +
           "AND ((b.pickupDate < :dropoffDate AND b.dropoffDate > :pickupDate))")
    List<Booking> findConflictingBookings(
        @Param("carId") Long carId,
        @Param("pickupDate") LocalDateTime pickupDate,
        @Param("dropoffDate") LocalDateTime dropoffDate
    );

    /**
     * Find bookings by user and status
     */
    List<Booking> findByUserIdAndBookingStatus(Long userId, Booking.BookingStatus status);

    /**
     * Find all non-cancelled bookings for a car
     */
    @Query("SELECT b FROM Booking b WHERE b.carId = :carId AND b.bookingStatus != 'CANCELLED'")
    List<Booking> findActiveBookingsByCarId(@Param("carId") Long carId);

    /**
     * Find bookings ready for auto-transition to ACTIVE (CONFIRMED status with start date reached)
     */
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'CONFIRMED' AND b.pickupDate <= :now")
    List<Booking> findBookingsReadyForActivation(@Param("now") LocalDateTime now);

    /**
     * Find bookings ready for auto-transition to COMPLETED (ACTIVE status with end date reached)
     */
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'ACTIVE' AND b.dropoffDate <= :now")
    List<Booking> findBookingsReadyForCompletion(@Param("now") LocalDateTime now);
}