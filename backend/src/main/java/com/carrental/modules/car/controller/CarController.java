package com.carrental.modules.car.controller;

import com.carrental.modules.car.dto.CarDTO;
import com.carrental.modules.car.service.CarService;
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
 * REST Controller for Car endpoints.
 * 
 * Handles all HTTP requests related to cars.
 * Base path: /api/cars
 */
@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class CarController {

    private final CarService carService;

    /**
     * Get all cars
     * 
     * GET /api/cars
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CarDTO>>> getAllCars() {
        log.info("GET request to retrieve all cars");
        List<CarDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Cars retrieved successfully")
        );
    }

    /**
     * Get car by ID
     * 
     * GET /api/cars/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarDTO>> getCarById(@PathVariable Long id) {
        log.info("GET request to retrieve car with id: {}", id);
        CarDTO car = carService.getCarById(id);
        return ResponseEntity.ok(
                ApiResponse.success(car, "Car retrieved successfully")
        );
    }

    /**
     * Get car by registration number
     * 
     * GET /api/cars/search/registration?number=REG001
     */
    @GetMapping("/search/registration")
    public ResponseEntity<ApiResponse<CarDTO>> getCarByRegistration(
            @RequestParam String number) {
        log.info("GET request to retrieve car by registration: {}", number);
        CarDTO car = carService.getCarByRegistrationNumber(number);
        return ResponseEntity.ok(
                ApiResponse.success(car, "Car retrieved successfully")
        );
    }

    /**
     * Get car by VIN
     * 
     * GET /api/cars/search/vin?vin=VIN001
     */
    @GetMapping("/search/vin")
    public ResponseEntity<ApiResponse<CarDTO>> getCarByVin(@RequestParam String vin) {
        log.info("GET request to retrieve car by VIN: {}", vin);
        CarDTO car = carService.getCarByVin(vin);
        return ResponseEntity.ok(
                ApiResponse.success(car, "Car retrieved successfully")
        );
    }

    /**
     * Get cars by type
     * 
     * GET /api/cars/type?type=SEDAN
     */
    @GetMapping("/type")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByType(
            @RequestParam String type) {
        log.info("GET request to retrieve cars by type: {}", type);
        List<CarDTO> cars = carService.getCarsByType(type);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Cars retrieved successfully")
        );
    }

    /**
     * Get available cars
     * 
     * GET /api/cars/available
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getAvailableCars() {
        log.info("GET request to retrieve available cars");
        List<CarDTO> cars = carService.getAvailableCars();
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Available cars retrieved successfully")
        );
    }

    /**
     * Get available cars by type
     * 
     * GET /api/cars/available/type?type=SUV
     */
    @GetMapping("/available/type")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getAvailableCarsByType(
            @RequestParam String type) {
        log.info("GET request to retrieve available cars by type: {}", type);
        List<CarDTO> cars = carService.getAvailableCarsByType(type);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Available cars retrieved successfully")
        );
    }

    /**
     * Get cars by price range
     * 
     * GET /api/cars/price-range?minPrice=50&maxPrice=150
     */
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        log.info("GET request to retrieve cars by price range: {} - {}", minPrice, maxPrice);
        List<CarDTO> cars = carService.getCarsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Cars retrieved successfully")
        );
    }

    /**
     * Search cars
     * 
     * GET /api/cars/search?term=Toyota
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CarDTO>>> searchCars(
            @RequestParam(required = false) String term) {
        log.info("GET request to search cars with term: {}", term);
        List<CarDTO> cars = carService.searchCars(term);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Search completed successfully")
        );
    }

    /**
     * Get cars with low mileage
     * 
     * GET /api/cars/low-mileage?mileage=50000
     */
    @GetMapping("/low-mileage")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsWithLowMileage(
            @RequestParam Long mileage) {
        log.info("GET request to retrieve cars with mileage below: {}", mileage);
        List<CarDTO> cars = carService.getCarsWithLowMileage(mileage);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Cars retrieved successfully")
        );
    }

    /**
     * Get cars by transmission type
     * 
     * GET /api/cars/transmission?type=AUTOMATIC
     */
    @GetMapping("/transmission")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByTransmission(
            @RequestParam String type) {
        log.info("GET request to retrieve cars by transmission: {}", type);
        List<CarDTO> cars = carService.getCarsByTransmission(type);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Cars retrieved successfully")
        );
    }

    /**
     * Get cars by fuel type
     * 
     * GET /api/cars/fuel-type?type=PETROL
     */
    @GetMapping("/fuel-type")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByFuelType(
            @RequestParam String type) {
        log.info("GET request to retrieve cars by fuel type: {}", type);
        List<CarDTO> cars = carService.getCarsByFuelType(type);
        return ResponseEntity.ok(
                ApiResponse.success(cars, "Cars retrieved successfully")
        );
    }

    /**
     * Create new car
     * 
     * POST /api/cars
     */
    @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> createCar(
            @Valid @RequestBody CarDTO carDTO) {
        log.info("POST request to create new car: {} {}", carDTO.getMake(), carDTO.getModel());
        CarDTO createdCar = carService.createCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(createdCar, "Car created successfully")
        );
    }

    /**
     * Update car
     * 
     * PUT /api/cars/{id}
     */
    @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarDTO carDTO) {
        log.info("PUT request to update car with id: {}", id);
        CarDTO updatedCar = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(
                ApiResponse.success(updatedCar, "Car updated successfully")
        );
    }

    /**
     * Delete car
     * 
     * DELETE /api/cars/{id}
     */
    @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long id) {
        log.info("DELETE request to delete car with id: {}", id);
        carService.deleteCar(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Car deleted successfully")
        );
    }

    /**
     * Update car status
     * 
     * PATCH /api/cars/{id}/status?status=RENTED
     */
    @PatchMapping("/{id}/status")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> updateCarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("PATCH request to update car status - id: {}, status: {}", id, status);
        CarDTO updatedCar = carService.updateCarStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success(updatedCar, "Car status updated successfully")
        );
    }

    /**
     * Update car mileage
     * 
     * PATCH /api/cars/{id}/mileage?mileage=50000
     */
    @PatchMapping("/{id}/mileage")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> updateCarMileage(
            @PathVariable Long id,
            @RequestParam Long mileage) {
        log.info("PATCH request to update car mileage - id: {}, mileage: {}", id, mileage);
        CarDTO updatedCar = carService.updateCarMileage(id, mileage);
        return ResponseEntity.ok(
                ApiResponse.success(updatedCar, "Car mileage updated successfully")
        );
    }

    /**
     * Get car count by status
     * 
     * GET /api/cars/stats/count-by-status?status=AVAILABLE
     */
    @GetMapping("/stats/count-by-status")
    public ResponseEntity<ApiResponse<Long>> getCarCountByStatus(
            @RequestParam String status) {
        log.info("GET request to get car count by status: {}", status);
        Long count = carService.getCarCountByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success(count, "Car count retrieved successfully")
        );
    }

    /**
     * Get total cars count
     * 
     * GET /api/cars/stats/total
     */
    @GetMapping("/stats/total")
    public ResponseEntity<ApiResponse<Long>> getTotalCarsCount() {
        log.info("GET request to get total cars count");
        Long count = carService.getTotalCarsCount();
        return ResponseEntity.ok(
                ApiResponse.success(count, "Total cars count retrieved successfully")
        );
    }
}