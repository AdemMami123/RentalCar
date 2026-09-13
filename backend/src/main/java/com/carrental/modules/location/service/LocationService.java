package com.carrental.modules.location.service;

import com.carrental.modules.location.dto.LocationDTO;
import com.carrental.modules.location.entity.Location;
import com.carrental.modules.location.mapper.LocationMapper;
import com.carrental.modules.location.repository.LocationRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Location business logic.
 * 
 * Handles all location-related operations including CRUD, search, and filtering.
 * Uses @Transactional to manage database transactions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    /**
     * Get all locations
     * 
     * @return list of all location DTOs
     */
    public List<LocationDTO> getAllLocations() {
        log.debug("Fetching all locations");
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a location by ID
     * 
     * @param id the location ID
     * @return the location DTO
     * @throws ResourceNotFoundException if location not found
     */
    public LocationDTO getLocationById(Long id) {
        log.debug("Fetching location with id: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));
        return locationMapper.toDTO(location);
    }

    /**
     * Get all locations in a specific city
     * 
     * @param city the city name
     * @return list of location DTOs in that city
     */
    public List<LocationDTO> getLocationsByCity(String city) {
        log.debug("Fetching locations in city: {}", city);
        return locationRepository.findByCity(city)
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all locations in a specific country
     * 
     * @param country the country name
     * @return list of location DTOs in that country
     */
    public List<LocationDTO> getLocationsByCountry(String country) {
        log.debug("Fetching locations in country: {}", country);
        return locationRepository.findByCountry(country)
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get locations with available cars
     * 
     * @return list of location DTOs with available cars
     */
    public List<LocationDTO> getLocationsWithAvailableCars() {
        log.debug("Fetching locations with available cars");
        return locationRepository.findLocationsWithAvailableCars()
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search locations by name or city
     * 
     * @param searchTerm the search term
     * @return list of matching location DTOs
     */
    public List<LocationDTO> searchLocations(String searchTerm) {
        log.debug("Searching locations with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.isBlank()) {
            return getAllLocations();
        }
        return locationRepository.searchLocationsByNameOrCity(searchTerm)
                .stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new location
     * 
     * @param locationDTO the location data
     * @return the created location DTO
     */
    @Transactional
    public LocationDTO createLocation(LocationDTO locationDTO) {
        log.info("Creating new location with name: {}", locationDTO.getName());
        
        // Check if location already exists
        if (locationRepository.existsByName(locationDTO.getName())) {
            log.warn("Location with name {} already exists", locationDTO.getName());
            throw new IllegalArgumentException("Location with name '" + locationDTO.getName() + "' already exists");
        }

        Location location = locationMapper.toEntity(locationDTO);
        Location savedLocation = locationRepository.save(location);
        
        log.info("Location created successfully with id: {}", savedLocation.getId());
        return locationMapper.toDTO(savedLocation);
    }

    /**
     * Update an existing location
     * 
     * @param id the location ID
     * @param locationDTO the updated location data
     * @return the updated location DTO
     * @throws ResourceNotFoundException if location not found
     */
    @Transactional
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        log.info("Updating location with id: {}", id);
        
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));

        // Check if new name is already taken by another location
        if (!location.getName().equals(locationDTO.getName()) && 
            locationRepository.existsByName(locationDTO.getName())) {
            log.warn("Location with name {} already exists", locationDTO.getName());
            throw new IllegalArgumentException("Location with name '" + locationDTO.getName() + "' already exists");
        }

        locationMapper.updateEntityFromDTO(locationDTO, location);
        Location updatedLocation = locationRepository.save(location);
        
        log.info("Location updated successfully");
        return locationMapper.toDTO(updatedLocation);
    }

    /**
     * Delete a location by ID
     * 
     * @param id the location ID
     * @throws ResourceNotFoundException if location not found
     */
    @Transactional
    public void deleteLocation(Long id) {
        log.info("Deleting location with id: {}", id);
        
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));

        locationRepository.delete(location);
        log.info("Location deleted successfully");
    }

    /**
     * Update available cars count for a location
     * 
     * @param id the location ID
     * @param availableCars the new count of available cars
     * @return the updated location DTO
     * @throws ResourceNotFoundException if location not found
     */
    @Transactional
    public LocationDTO updateAvailableCars(Long id, Integer availableCars) {
        log.info("Updating available cars for location {}: {}", id, availableCars);
        
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id
                ));

        location.setAvailableCars(availableCars);
        Location updatedLocation = locationRepository.save(location);
        
        log.info("Available cars updated successfully");
        return locationMapper.toDTO(updatedLocation);
    }

    /**
     * Get count of all locations
     * 
     * @return total number of locations
     */
    public Long getTotalLocationsCount() {
        return locationRepository.count();
    }
}