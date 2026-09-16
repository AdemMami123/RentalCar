package com.carrental.modules.car.repository;

import com.carrental.modules.car.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Car entity.
 * 
 * Provides database access methods for Car CRUD operations.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Find a car by registration number
     */
    Optional<Car> findByRegistrationNumber(String registrationNumber);

    /**
     * Find a car by license plate
     */
    Optional<Car> findByLicensePlate(String licensePlate);

    /**
     * Find a car by VIN
     */
    Optional<Car> findByVin(String vin);

    /**
     * Find all cars of a specific type
     */
    List<Car> findByCarType(Car.CarType carType);

    /**
     * Find all cars with a specific status
     */
    List<Car> findByStatus(Car.CarStatus status);

    /**
     * Find all available cars
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' ORDER BY c.dailyRate ASC")
    List<Car> findAvailableCars();

    /**
     * Find all available cars of a specific type
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' AND c.carType = :carType ORDER BY c.dailyRate ASC")
    List<Car> findAvailableCarsByType(@Param("carType") Car.CarType carType);

    /**
     * Find cars within a price range
     */
    @Query("SELECT c FROM Car c WHERE c.dailyRate >= :minPrice AND c.dailyRate <= :maxPrice ORDER BY c.dailyRate ASC")
    List<Car> findCarsByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Search cars by make or model (case-insensitive)
     */
    @Query("SELECT c FROM Car c WHERE " +
           "LOWER(c.make) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.model) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Car> searchByMakeOrModel(@Param("searchTerm") String searchTerm);

    /**
     * Find cars with low mileage (good condition)
     */
    @Query("SELECT c FROM Car c WHERE c.mileage < :mileage ORDER BY c.mileage ASC")
    List<Car> findCarsWithLowMileage(@Param("mileage") Long mileage);

    /**
     * Count cars by status
     */
    Long countByStatus(Car.CarStatus status);

    /**
     * Check if car exists by registration number
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByLicensePlate(String licensePlate);

    /**
     * Check if car exists by VIN
     */
    boolean existsByVin(String vin);

    /**
     * Get cars of a specific year
     */
    List<Car> findByYearOrderByMakeAsc(Integer year);

    /**
     * Get cars by transmission type
     */
    List<Car> findByTransmission(Car.TransmissionType transmission);

    /**
     * Get cars by fuel type
     */
    List<Car> findByFuelType(Car.FuelType fuelType);
}