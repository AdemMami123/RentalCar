package com.carrental.modules.inspection.service;

import com.carrental.modules.booking.entity.Booking;
import com.carrental.modules.booking.repository.BookingRepository;
import com.carrental.modules.car.entity.Car;
import com.carrental.modules.car.repository.CarRepository;
import com.carrental.modules.inspection.dto.InspectionComparisonDTO;
import com.carrental.modules.inspection.dto.InspectionDTO;
import com.carrental.modules.inspection.entity.*;
import com.carrental.modules.inspection.repository.*;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import com.carrental.shared.services.CloudinaryMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InspectionService {
    private final VehicleInspectionRepository inspectionRepository;
    private final InspectionDamageRepository damageRepository;
    private final InspectionPhotoRepository photoRepository;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final CloudinaryMediaService mediaService;

    @Transactional
    public InspectionDTO createDraft(InspectionDTO request, Long inspectorId) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.bookingId()));
        if (!Objects.equals(booking.getCarId(), request.carId()) && request.carId() != null) {
            throw new IllegalArgumentException("Inspection vehicle does not match the booking vehicle");
        }
        Long carId = booking.getCarId();
        VehicleInspection.InspectionType type = request.inspectionType();
        if (inspectionRepository.findByBookingIdAndInspectionType(request.bookingId(), type).isPresent()) {
            throw new IllegalStateException("A " + type.name().toLowerCase() + " inspection already exists for this booking");
        }
        if (type == VehicleInspection.InspectionType.PICKUP
                && booking.getBookingStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Pickup inspection requires a confirmed booking");
        }
        if (type == VehicleInspection.InspectionType.RETURN) {
            VehicleInspection pickup = inspectionRepository
                    .findByBookingIdAndInspectionType(request.bookingId(), VehicleInspection.InspectionType.PICKUP)
                    .orElseThrow(() -> new IllegalStateException("A completed pickup inspection is required before return"));
            if (pickup.getStatus() != VehicleInspection.InspectionStatus.COMPLETED
                    || booking.getBookingStatus() != Booking.BookingStatus.ACTIVE) {
                throw new IllegalStateException("Return inspection requires an active booking and completed pickup inspection");
            }
            if (request.odometerReading() < pickup.getOdometerReading()) {
                throw new IllegalArgumentException("Return odometer reading cannot be lower than pickup reading");
            }
        }

        VehicleInspection inspection = VehicleInspection.builder()
                .bookingId(request.bookingId()).carId(carId).inspectionType(type)
                .status(VehicleInspection.InspectionStatus.DRAFT).inspectedBy(inspectorId)
                .odometerReading(request.odometerReading())
                .fuelLevel(request.fuelLevel()).exteriorCondition(request.exteriorCondition())
                .interiorCondition(request.interiorCondition()).notes(request.notes())
                .customerAcknowledgedAt(request.customerAcknowledgedAt()).build();
        inspection.setCreatedBy(String.valueOf(inspectorId));
        inspection.setUpdatedBy(String.valueOf(inspectorId));
        inspection = inspectionRepository.save(inspection);
        saveDamages(inspection.getId(), request.damages(), inspectorId);

        return getInspection(inspection.getId());
    }

    @Transactional
    public InspectionDTO completeDraft(Long inspectionId, Long inspectorId) {
        VehicleInspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found with id: " + inspectionId));
        if (inspection.getStatus() == VehicleInspection.InspectionStatus.COMPLETED) {
            throw new IllegalStateException("Inspection is already completed");
        }
        Booking booking = bookingRepository.findById(inspection.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + inspection.getBookingId()));
        if (inspection.getInspectionType() == VehicleInspection.InspectionType.RETURN) {
            VehicleInspection pickup = inspectionRepository.findByBookingIdAndInspectionType(
                    inspection.getBookingId(), VehicleInspection.InspectionType.PICKUP)
                    .orElseThrow(() -> new IllegalStateException("A completed pickup inspection is required before return"));
            if (pickup.getStatus() != VehicleInspection.InspectionStatus.COMPLETED
                    || inspection.getOdometerReading() < pickup.getOdometerReading()
                    || booking.getBookingStatus() != Booking.BookingStatus.ACTIVE) {
                throw new IllegalStateException("Return inspection requires an active booking and a valid odometer reading");
            }
        }
        inspection.setStatus(VehicleInspection.InspectionStatus.COMPLETED);
        inspection.setInspectedAt(LocalDateTime.now());
        inspection.setUpdatedBy(String.valueOf(inspectorId));
        inspectionRepository.save(inspection);

        if (inspection.getInspectionType() == VehicleInspection.InspectionType.PICKUP) {
            booking.setBookingStatus(Booking.BookingStatus.ACTIVE);
        } else {
            booking.setBookingStatus(Booking.BookingStatus.COMPLETED);
            Car car = carRepository.findById(inspection.getCarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + inspection.getCarId()));
            car.setMileage(inspection.getOdometerReading());
            if (car.getStatus() == Car.CarStatus.RENTED) car.setStatus(Car.CarStatus.AVAILABLE);
            carRepository.save(car);
        }
        bookingRepository.save(booking);
        return getInspection(inspection.getId());
    }

    public InspectionDTO getInspection(Long id) {
        VehicleInspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found with id: " + id));
        return toDto(inspection);
    }

    public InspectionComparisonDTO compare(Long bookingId) {
        VehicleInspection pickup = inspectionRepository.findByBookingIdAndInspectionType(
                bookingId, VehicleInspection.InspectionType.PICKUP).orElseThrow(
                () -> new ResourceNotFoundException("Pickup inspection not found for booking: " + bookingId));
        VehicleInspection returned = inspectionRepository.findByBookingIdAndInspectionType(
                bookingId, VehicleInspection.InspectionType.RETURN).orElseThrow(
                () -> new ResourceNotFoundException("Return inspection not found for booking: " + bookingId));
        List<InspectionDTO.DamageDTO> pickupDamage = damages(pickup.getId());
        List<InspectionDTO.DamageDTO> returnDamage = damages(returned.getId());
        Set<String> pickupKeys = pickupDamage.stream().map(this::damageKey).collect(Collectors.toSet());
        List<InspectionDTO.DamageDTO> newDamage = returnDamage.stream()
                .filter(damage -> !pickupKeys.contains(damageKey(damage))).toList();
        BigDecimal fuelDifference = returned.getFuelLevel().subtract(pickup.getFuelLevel());
        return new InspectionComparisonDTO(bookingId, pickup.getOdometerReading(), returned.getOdometerReading(),
                returned.getOdometerReading() - pickup.getOdometerReading(), pickup.getFuelLevel(),
                returned.getFuelLevel(), fuelDifference, pickupDamage,
                newDamage, !newDamage.isEmpty() || fuelDifference.compareTo(BigDecimal.ZERO) != 0
                        || !Objects.equals(pickup.getExteriorCondition(), returned.getExteriorCondition())
                        || !Objects.equals(pickup.getInteriorCondition(), returned.getInteriorCondition()));
    }

    @Transactional
    public InspectionDTO uploadPhoto(Long inspectionId, String category, String description,
                                     MultipartFile file, Long damageId, Long inspectorId) {
        VehicleInspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found with id: " + inspectionId));
        if (inspection.getStatus() == VehicleInspection.InspectionStatus.COMPLETED) {
            throw new IllegalStateException("Completed inspection evidence cannot be changed");
        }
        CloudinaryMediaService.UploadedMedia media = mediaService.uploadImage(file, "car-rental/inspections/" + inspectionId);
        InspectionPhoto photo = InspectionPhoto.builder().inspectionId(inspectionId).damageId(damageId)
                .photoUrl(media.url()).storageKey(media.publicId()).category(category)
                .description(description).uploadedAt(LocalDateTime.now()).build();
        photo.setCreatedBy(String.valueOf(inspectorId));
        photo.setUpdatedBy(String.valueOf(inspectorId));
        photoRepository.save(photo);
        return toDto(inspection);
    }

    private void saveDamages(Long inspectionId, List<InspectionDTO.DamageDTO> damages, Long inspectorId) {
        if (damages == null) return;
        damages.forEach(damage -> {
            InspectionDamage entity = InspectionDamage.builder().inspectionId(inspectionId)
                    .damageType(damage.damageType()).locationOnVehicle(damage.locationOnVehicle())
                    .description(damage.description()).severity(damage.severity()).build();
            entity.setCreatedBy(String.valueOf(inspectorId));
            entity.setUpdatedBy(String.valueOf(inspectorId));
            damageRepository.save(entity);
        });
    }

    private List<InspectionDTO.DamageDTO> damages(Long inspectionId) {
        return damageRepository.findByInspectionId(inspectionId).stream()
                .map(d -> new InspectionDTO.DamageDTO(d.getId(), d.getDamageType(), d.getLocationOnVehicle(),
                        d.getDescription(), d.getSeverity())).toList();
    }

    private String damageKey(InspectionDTO.DamageDTO damage) {
        return (damage.damageType() + "|" + damage.locationOnVehicle() + "|" + damage.description())
                .trim().toLowerCase(Locale.ROOT);
    }

    private InspectionDTO toDto(VehicleInspection inspection) {
        List<InspectionDTO.PhotoDTO> photos = photoRepository.findByInspectionId(inspection.getId()).stream()
                .map(p -> new InspectionDTO.PhotoDTO(p.getId(), p.getDamageId(), p.getPhotoUrl(), p.getStorageKey(),
                        p.getCategory(), p.getDescription(), p.getUploadedAt())).toList();
        return new InspectionDTO(inspection.getId(), inspection.getBookingId(), inspection.getCarId(),
                inspection.getInspectionType(), inspection.getStatus(), inspection.getInspectedBy(),
                inspection.getInspectedAt(), inspection.getOdometerReading(), inspection.getFuelLevel(),
                inspection.getExteriorCondition(), inspection.getInteriorCondition(), inspection.getNotes(),
                inspection.getCustomerAcknowledgedAt(), damages(inspection.getId()), photos);
    }
}
