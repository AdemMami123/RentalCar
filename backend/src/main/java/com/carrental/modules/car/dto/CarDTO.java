package com.carrental.modules.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Car.
 * 
 * Used for API requests and responses to encapsulate car data
 * without exposing JPA entity details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {

    /**
     * Unique identifier
     */
    private Long id;

    /**
     * Car manufacturer
     */
    @NotBlank(message = "Car make cannot be blank")
    @Size(min = 2, max = 100, message = "Make must be between 2 and 100 characters")
    private String make;

    /**
     * Car model
     */
    @NotBlank(message = "Car model cannot be blank")
    @Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
    private String model;

    /**
     * Year of manufacture
     */
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year cannot be in the future")
    private Integer year;

    /**
     * Registration number
     */
    @NotBlank(message = "Registration number cannot be blank")
    @Size(min = 1, max = 50, message = "Registration number must be between 1 and 50 characters")
    private String registrationNumber;

    /**
     * License plate number
     */
    @NotBlank(message = "License plate cannot be blank")
    @Size(min = 1, max = 50, message = "License plate must be between 1 and 50 characters")
    private String licensePlate;

    /**
     * Vehicle Identification Number
     */
    @NotBlank(message = "VIN cannot be blank")
    @Size(min = 10, max = 100, message = "VIN must be between 10 and 100 characters")
    private String vin;

    /**
     * Type of car
     */
    @NotNull(message = "Car type is required")
    private String carType;

    /**
     * Number of seats
     */
    @NotNull(message = "Number of seats is required")
    @Positive(message = "Seats must be greater than 0")
    @Max(value = 15, message = "Seats cannot exceed 15")
    private Integer seats;

    /**
     * Transmission type
     */
    private String transmission;

    /**
     * Fuel type
     */
    private String fuelType;

    /**
     * Daily rental rate
     */
    @NotNull(message = "Daily rate is required")
    @Positive(message = "Daily rate must be greater than 0")
    private Double dailyRate;

    /**
     * Current status
     */
    private String status;

    /**
     * Car color
     */
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    /**
     * Current mileage
     */
    @Min(value = 0, message = "Mileage cannot be negative")
    private Long mileage;

    /**
     * Description
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Timestamp when created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when last updated
     */
    private LocalDateTime updatedAt;

    /**
     * User who created
     */
    private String createdBy;

    /**
     * User who last updated
     */
    private String updatedBy;
}