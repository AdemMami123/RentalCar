package com.carrental.modules.location.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Location Entity represents a car rental location/branch.
 * 
 * Stores information about rental points including address, hours of operation,
 * and contact details.
 */
@Entity
@Table(name = "locations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseEntity {

    /**
     * Location name (e.g., "Downtown Center", "Airport Terminal")
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Location name is required")
    private String name;

    /**
     * Full address of the location
     */
    @Column(nullable = false, length = 500)
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * City where location is situated
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "City is required")
    private String city;

    /**
     * Country where location is situated
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Country is required")
    private String country;

    /**
     * Latitude coordinate for mapping (using BigDecimal for precision)
     */
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    /**
     * Longitude coordinate for mapping (using BigDecimal for precision)
     */
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    /**
     * Contact phone number
     */
    @Column(length = 20)
    private String phone;

    /**
     * Contact email address
     */
    @Column(length = 100)
    private String email;

    /**
     * Opening time of the location (e.g., 08:00:00)
     */
    @Column(name = "opening_time")
    private LocalTime openingTime;

    /**
     * Closing time of the location (e.g., 20:00:00)
     */
    @Column(name = "closing_time")
    private LocalTime closingTime;

    /**
     * Number of available cars at this location
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer availableCars = 0;
}