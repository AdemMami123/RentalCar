package com.carrental.modules.booking.repository;

import com.carrental.modules.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    boolean existsByBookingNumber(String bookingNumber);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCarId(Long carId);
    List<Booking> findByBookingStatus(Booking.BookingStatus status);
}