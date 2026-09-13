package com.carrental.modules.booking.mapper;

import com.carrental.modules.booking.dto.BookingDTO;
import com.carrental.modules.booking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {
    @Mapping(target = "bookingStatus", expression = "java(booking.getBookingStatus() != null ? booking.getBookingStatus().toString() : null)")
    BookingDTO toDTO(Booking booking);
    @Mapping(target = "bookingStatus", expression = "java(dto.getBookingStatus() != null ? Booking.BookingStatus.valueOf(dto.getBookingStatus().toUpperCase()) : Booking.BookingStatus.PENDING)")
    Booking toEntity(BookingDTO dto);
    @Mapping(target = "bookingStatus", expression = "java(dto.getBookingStatus() != null ? Booking.BookingStatus.valueOf(dto.getBookingStatus().toUpperCase()) : booking.getBookingStatus())")
    void updateEntityFromDTO(BookingDTO dto, @MappingTarget Booking booking);
}