package com.carrental.modules.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data Transfer Object for Location.
 * 
 * Used for API requests and responses to encapsulate location data
 * without exposing JPA entity details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {

    /**
     * Unique identifier
     */
    private Long id;

    /**
     * Location name - required field
     */
    @NotBlank(message = "Location name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * Full street address - required field
     */
    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 500, message = "Address must be between 5 and 500 characters")
    private String address;

    /**
     * City name - required field
     */
    @NotBlank(message = "City cannot be blank")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    /**
     * Country name - required field
     */
    @NotBlank(message = "Country cannot be blank")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    /**
     * Latitude coordinate (optional)
     */
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    /**
     * Longitude coordinate (optional)
     */
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    /**
     * Phone number (optional) - pattern allows empty string or valid phone format
     */
    @Pattern(regexp = "^[+]?[0-9]{1,3}[-.]?[0-9]{1,14}$|^$", 
             message = "Phone number format is invalid")
    private String phone;

    /**
     * Email address (optional)
     */
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Opening time (optional)
     */
    private LocalTime openingTime;

    /**
     * Closing time (optional)
     */
    private LocalTime closingTime;

    /**
     * Number of available cars at this location
     */
    private Integer availableCars;

    /**
     * Timestamp when location was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when location was last updated
     */
    private LocalDateTime updatedAt;

    /**
     * User who created the location
     */
    private String createdBy;

    /**
     * User who last updated the location
     */
    private String updatedBy;
}