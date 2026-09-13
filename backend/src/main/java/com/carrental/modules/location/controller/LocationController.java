package com.carrental.modules.location.controller;

import com.carrental.modules.location.dto.LocationDTO;
import com.carrental.modules.location.service.LocationService;
import com.carrental.shared.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

/**
 * REST Controller for Location endpoints.
 * 
 * Handles all HTTP requests related to locations.
 * Base path: /api/locations
 */
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class LocationController {

    private final LocationService locationService;

    /**
     * Get all locations
     * 
     * GET /api/locations
     * 
     * @return ResponseEntity with list of all locations
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAllLocations() {
        log.info("GET request to retrieve all locations");
        List<LocationDTO> locations = locationService.getAllLocations();
        return ResponseEntity.ok(
                ApiResponse.success(locations, "Locations retrieved successfully")
        );
    }

    /**
     * Get a location by ID
     * 
     * GET /api/locations/{id}
     * 
     * @param id the location ID
     * @return ResponseEntity with the location data
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> getLocationById(@PathVariable Long id) {
        log.info("GET request to retrieve location with id: {}", id);
        LocationDTO location = locationService.getLocationById(id);
        return ResponseEntity.ok(
                ApiResponse.success(location, "Location retrieved successfully")
        );
    }

    /**
     * Search locations by city
     * 
     * GET /api/locations/search/city?city=Tunis
     * 
     * @param city the city name
     * @return ResponseEntity with matching locations
     */
    @GetMapping("/search/city")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getLocationsByCity(
            @RequestParam String city) {
        log.info("GET request to search locations by city: {}", city);
        List<LocationDTO> locations = locationService.getLocationsByCity(city);
        return ResponseEntity.ok(
                ApiResponse.success(locations, "Locations found in city: " + city)
        );
    }

    /**
     * Search locations by country
     * 
     * GET /api/locations/search/country?country=Tunisia
     * 
     * @param country the country name
     * @return ResponseEntity with matching locations
     */
    @GetMapping("/search/country")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getLocationsByCountry(
            @RequestParam String country) {
        log.info("GET request to search locations by country: {}", country);
        List<LocationDTO> locations = locationService.getLocationsByCountry(country);
        return ResponseEntity.ok(
                ApiResponse.success(locations, "Locations found in country: " + country)
        );
    }

    /**
     * Get locations with available cars
     * 
     * GET /api/locations/available
     * 
     * @return ResponseEntity with locations that have available cars
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAvailableLocations() {
        log.info("GET request to retrieve locations with available cars");
        List<LocationDTO> locations = locationService.getLocationsWithAvailableCars();
        return ResponseEntity.ok(
                ApiResponse.success(locations, "Available locations retrieved")
        );
    }

    /**
     * Search locations by name or city
     * 
     * GET /api/locations/search?term=Downtown
     * 
     * @param searchTerm the search term
     * @return ResponseEntity with matching locations
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> searchLocations(
            @RequestParam(required = false) String searchTerm) {
        log.info("GET request to search locations with term: {}", searchTerm);
        List<LocationDTO> locations = locationService.searchLocations(searchTerm);
        return ResponseEntity.ok(
                ApiResponse.success(locations, "Search completed successfully")
        );
    }

    /**
     * Create a new location
     * 
     * POST /api/locations
     * Request body: LocationDTO
     * 
     * @param locationDTO the location data to create
     * @return ResponseEntity with created location
     */
    @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationDTO>> createLocation(
            @Valid @RequestBody LocationDTO locationDTO) {
        log.info("POST request to create new location: {}", locationDTO.getName());
        LocationDTO createdLocation = locationService.createLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(createdLocation, "Location created successfully")
        );
    }

    /**
     * Update an existing location
     * 
     * PUT /api/locations/{id}
     * Request body: LocationDTO
     * 
     * @param id the location ID
     * @param locationDTO the updated location data
     * @return ResponseEntity with updated location
     */
    @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationDTO>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationDTO locationDTO) {
        log.info("PUT request to update location with id: {}", id);
        LocationDTO updatedLocation = locationService.updateLocation(id, locationDTO);
        return ResponseEntity.ok(
                ApiResponse.success(updatedLocation, "Location updated successfully")
        );
    }

    /**
     * Delete a location
     * 
     * DELETE /api/locations/{id}
     * 
     * @param id the location ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        log.info("DELETE request to delete location with id: {}", id);
        locationService.deleteLocation(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Location deleted successfully")
        );
    }

    /**
     * Update available cars count for a location
     * 
     * PATCH /api/locations/{id}/available-cars?count=5
     * 
     * @param id the location ID
     * @param count the new available cars count
     * @return ResponseEntity with updated location
     */
    @PatchMapping("/{id}/available-cars")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationDTO>> updateAvailableCars(
            @PathVariable Long id,
            @RequestParam Integer count) {
        log.info("PATCH request to update available cars for location {}: {}", id, count);
        LocationDTO updatedLocation = locationService.updateAvailableCars(id, count);
        return ResponseEntity.ok(
                ApiResponse.success(updatedLocation, "Available cars updated successfully")
        );
    }

    /**
     * Get total count of locations
     * 
     * GET /api/locations/stats/total
     * 
     * @return ResponseEntity with total count
     */
    @GetMapping("/stats/total")
    public ResponseEntity<ApiResponse<Long>> getTotalLocationsCount() {
        log.info("GET request to retrieve total locations count");
        Long totalCount = locationService.getTotalLocationsCount();
        return ResponseEntity.ok(
                ApiResponse.success(totalCount, "Total locations count retrieved")
        );
    }
}