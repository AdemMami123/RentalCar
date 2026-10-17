package com.carrental.modules.inspection;

import com.carrental.modules.booking.repository.BookingRepository;
import com.carrental.modules.car.repository.CarRepository;
import com.carrental.modules.inspection.dto.InspectionComparisonDTO;
import com.carrental.modules.inspection.entity.*;
import com.carrental.modules.inspection.repository.*;
import com.carrental.modules.inspection.service.InspectionService;
import com.carrental.shared.services.CloudinaryMediaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InspectionServiceTest {
    @Mock private VehicleInspectionRepository inspectionRepository;
    @Mock private InspectionDamageRepository damageRepository;
    @Mock private InspectionPhotoRepository photoRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private CarRepository carRepository;
    @Mock private CloudinaryMediaService mediaService;
    @InjectMocks private InspectionService service;

    @Test
    void compareFlagsOnlyDamageNotDocumentedAtPickup() {
        VehicleInspection pickup = VehicleInspection.builder().bookingId(10L)
                .inspectionType(VehicleInspection.InspectionType.PICKUP).odometerReading(1000L)
                .fuelLevel(new BigDecimal("80")).exteriorCondition("GOOD").interiorCondition("GOOD").build();
        pickup.setId(1L);
        VehicleInspection returned = VehicleInspection.builder().bookingId(10L)
                .inspectionType(VehicleInspection.InspectionType.RETURN).odometerReading(1250L)
                .fuelLevel(new BigDecimal("60")).exteriorCondition("FAIR").interiorCondition("GOOD").build();
        returned.setId(2L);
        InspectionDamage existing = InspectionDamage.builder().inspectionId(1L)
                .damageType("SCRATCH").locationOnVehicle("FRONT").description("small scratch").severity("MINOR").build();
        existing.setId(3L);
        InspectionDamage repeated = InspectionDamage.builder().inspectionId(2L)
                .damageType("SCRATCH").locationOnVehicle("FRONT").description("small scratch").severity("MINOR").build();
        repeated.setId(4L);
        InspectionDamage newDamage = InspectionDamage.builder().inspectionId(2L)
                .damageType("DENT").locationOnVehicle("REAR").description("new dent").severity("MODERATE").build();
        newDamage.setId(5L);

        when(inspectionRepository.findByBookingIdAndInspectionType(10L, VehicleInspection.InspectionType.PICKUP))
                .thenReturn(Optional.of(pickup));
        when(inspectionRepository.findByBookingIdAndInspectionType(10L, VehicleInspection.InspectionType.RETURN))
                .thenReturn(Optional.of(returned));
        when(damageRepository.findByInspectionId(1L)).thenReturn(List.of(existing));
        when(damageRepository.findByInspectionId(2L)).thenReturn(List.of(repeated, newDamage));

        InspectionComparisonDTO result = service.compare(10L);

        assertThat(result.distanceDriven()).isEqualTo(250L);
        assertThat(result.fuelDifference()).isEqualByComparingTo("-20");
        assertThat(result.existingDamage()).hasSize(1);
        assertThat(result.potentialNewDamage()).extracting("description").containsExactly("new dent");
        assertThat(result.hasDiscrepancies()).isTrue();
    }
}
