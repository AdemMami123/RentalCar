package com.carrental.modules.booking.repository;

import com.carrental.modules.booking.entity.BookingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingLocationRepository extends JpaRepository<BookingLocation, Long> {
    List<BookingLocation> findByBookingIdOrderByLocationTypeAsc(Long bookingId);
}
