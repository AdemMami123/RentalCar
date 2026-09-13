package com.carrental.modules.booking.service;

import com.carrental.modules.booking.dto.BookingDTO;
import com.carrental.modules.booking.entity.Booking;
import com.carrental.modules.booking.mapper.BookingMapper;
import com.carrental.modules.booking.repository.BookingRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    public List<BookingDTO> getAllBookings() { return bookingRepository.findAll().stream().map(bookingMapper::toDTO).toList(); }
    public BookingDTO getBookingById(Long id) { return bookingMapper.toDTO(bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id))); }
    public List<BookingDTO> getBookingsByUser(Long userId) { return bookingRepository.findByUserId(userId).stream().map(bookingMapper::toDTO).toList(); }
    public List<BookingDTO> getBookingsByCar(Long carId) { return bookingRepository.findByCarId(carId).stream().map(bookingMapper::toDTO).toList(); }
    @Transactional public BookingDTO createBooking(BookingDTO dto) { if (dto.getDropoffDate().isBefore(dto.getPickupDate())) throw new IllegalArgumentException("Dropoff date must be after pickup date"); if (dto.getBookingNumber() == null || dto.getBookingNumber().isBlank()) dto.setBookingNumber("BK-" + System.currentTimeMillis()); if (bookingRepository.existsByBookingNumber(dto.getBookingNumber())) throw new IllegalArgumentException("Booking number already exists"); return bookingMapper.toDTO(bookingRepository.save(bookingMapper.toEntity(dto))); }
    @Transactional public BookingDTO updateBooking(Long id, BookingDTO dto) { Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id)); bookingMapper.updateEntityFromDTO(dto, booking); return bookingMapper.toDTO(bookingRepository.save(booking)); }
    @Transactional public void deleteBooking(Long id) { Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id)); bookingRepository.delete(booking); }
}