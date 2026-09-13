package com.carrental.modules.car.mapper;

import com.carrental.modules.car.dto.CarDTO;
import com.carrental.modules.car.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Car entity and CarDTO.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CarMapper {

    /**
     * Convert Car entity to CarDTO
     * Enums are automatically converted to their string values
     */
    @Mapping(target = "carType", expression = "java(car.getCarType() != null ? car.getCarType().toString() : null)")
    @Mapping(target = "transmission", expression = "java(car.getTransmission() != null ? car.getTransmission().toString() : null)")
    @Mapping(target = "fuelType", expression = "java(car.getFuelType() != null ? car.getFuelType().toString() : null)")
    @Mapping(target = "status", expression = "java(car.getStatus() != null ? car.getStatus().toString() : null)")
    CarDTO toDTO(Car car);

    /**
     * Convert CarDTO to Car entity
     * Enums are automatically converted from their string values
     */
    @Mapping(target = "carType", expression = "java(carDTO.getCarType() != null ? Car.CarType.valueOf(carDTO.getCarType()) : null)")
    @Mapping(target = "transmission", expression = "java(carDTO.getTransmission() != null ? Car.TransmissionType.valueOf(carDTO.getTransmission()) : null)")
    @Mapping(target = "fuelType", expression = "java(carDTO.getFuelType() != null ? Car.FuelType.valueOf(carDTO.getFuelType()) : null)")
    @Mapping(target = "status", expression = "java(carDTO.getStatus() != null ? Car.CarStatus.valueOf(carDTO.getStatus()) : null)")
    Car toEntity(CarDTO carDTO);

    /**
     * Update an existing Car entity with data from CarDTO
     */
    @Mapping(target = "carType", expression = "java(carDTO.getCarType() != null ? Car.CarType.valueOf(carDTO.getCarType()) : car.getCarType())")
    @Mapping(target = "transmission", expression = "java(carDTO.getTransmission() != null ? Car.TransmissionType.valueOf(carDTO.getTransmission()) : car.getTransmission())")
    @Mapping(target = "fuelType", expression = "java(carDTO.getFuelType() != null ? Car.FuelType.valueOf(carDTO.getFuelType()) : car.getFuelType())")
    @Mapping(target = "status", expression = "java(carDTO.getStatus() != null ? Car.CarStatus.valueOf(carDTO.getStatus()) : car.getStatus())")
    void updateEntityFromDTO(CarDTO carDTO, @MappingTarget Car car);
}