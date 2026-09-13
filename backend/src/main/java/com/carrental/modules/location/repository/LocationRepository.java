package com.carrental.modules.location.repository;

import com.carrental.modules.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Location entity.
 * 
 * Provides database access methods for Location CRUD operations.
 * Spring Data JPA automatically generates implementation at runtime.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Find a location by its name
     * 
     * @param name the location name
     * @return Optional containing the location if found
     */
    Optional<Location> findByName(String name);

    /**
     * Find all locations in a specific city
     * 
     * @param city the city name
     * @return list of locations in that city
     */
    List<Location> findByCity(String city);

    /**
     * Find all locations in a specific country
     * 
     * @param country the country name
     * @return list of locations in that country
     */
    List<Location> findByCountry(String country);

    /**
     * Find all locations in a specific city and country
     * 
     * @param city the city name
     * @param country the country name
     * @return list of locations matching both criteria
     */
    List<Location> findByCityAndCountry(String city, String country);

    /**
     * Find locations with available cars
     * 
     * Uses custom JPQL query to find locations with more than 0 available cars
     * 
     * @return list of locations with available cars
     */
    @Query("SELECT l FROM Location l WHERE l.availableCars > 0 ORDER BY l.availableCars DESC")
    List<Location> findLocationsWithAvailableCars();

    /**
     * Search locations by name or city (case-insensitive)
     * 
     * @param searchTerm the search term
     * @return list of matching locations
     */
    @Query("SELECT l FROM Location l WHERE " +
           "LOWER(l.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Location> searchLocationsByNameOrCity(@Param("searchTerm") String searchTerm);

    /**
     * Check if a location exists by name
     * 
     * @param name the location name
     * @return true if location exists, false otherwise
     */
    boolean existsByName(String name);
}