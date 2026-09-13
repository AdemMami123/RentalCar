package com.carrental.modules.car.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Car Entity represents a vehicle in the rental fleet.
 * 
 * Contains detailed information about each car including specifications,
 * pricing, and availability status.
 */
@Entity
@Table(name = "cars")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car extends BaseEntity {

    /**
     * Car manufacturer (e.g., "Toyota", "Honda", "Mercedes")
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Car make is required")
    private String make;

    /**
     * Car model (e.g., "Corolla", "Civic", "C-Class")
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Car model is required")
    private String model;

    /**
     * Year of manufacture
     */
    @Column(nullable = false)
    @Positive(message = "Year must be a positive number")
    private Integer year;

    /**
     * Registration number (unique identifier in some countries)
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    /**
     * License plate number
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "License plate is required")
    private String licensePlate;

    /**
     * Vehicle Identification Number (VIN) - unique identifier for the vehicle
     */
    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "VIN is required")
    private String vin;

    /**
     * Type of car (SEDAN, SUV, VAN, TRUCK, COUPE, HATCHBACK)
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CarType carType;

    /**
     * Number of seats the car has
     */
    @Column(nullable = false)
    @Positive(message = "Number of seats must be positive")
    @Builder.Default
    private Integer seats = 5;

    /**
     * Type of transmission (MANUAL, AUTOMATIC, CVT)
     */
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private TransmissionType transmission;

    /**
     * Type of fuel the car uses (PETROL, DIESEL, HYBRID, ELECTRIC)
     */
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    /**
     * Daily rental rate in currency units
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @Positive(message = "Daily rate must be greater than zero")
    private BigDecimal dailyRate;
    /**
     * Current status of the car (AVAILABLE, RENTED, MAINTENANCE, RETIRED)
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CarStatus status = CarStatus.AVAILABLE;

    /**
     * Car color
     */
    @Column(length = 50)
    private String color;

    /**
     * Current mileage of the car
     */
    @Column
    @Builder.Default
    private Long mileage = 0L;

    /**
     * Description or notes about the car
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Enumeration for Car Types
     */
    public enum CarType {
        SEDAN("Sedan"),
        SUV("SUV"),
        VAN("Van"),
        TRUCK("Truck"),
        COUPE("Coupe"),
        HATCHBACK("Hatchback");

        private final String displayName;

        CarType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeration for Transmission Types
     */
    public enum TransmissionType {
        MANUAL("Manual"),
        AUTOMATIC("Automatic"),
        CVT("CVT");

        private final String displayName;

        TransmissionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeration for Fuel Types
     */
    public enum FuelType {
        PETROL("Petrol"),
        DIESEL("Diesel"),
        HYBRID("Hybrid"),
        ELECTRIC("Electric");

        private final String displayName;

        FuelType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeration for Car Status
     */
    public enum CarStatus {
        AVAILABLE("Available"),
        RENTED("Rented"),
        MAINTENANCE("Under Maintenance"),
        RETIRED("Retired");

        private final String displayName;

        CarStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}