package com.carrental.modules.car.service;

import com.carrental.modules.car.dto.CarDTO;
import com.carrental.modules.car.entity.Car;
import com.carrental.modules.car.entity.CarImage;
import com.carrental.modules.car.mapper.CarMapper;
import com.carrental.modules.car.repository.CarImageRepository;
import com.carrental.modules.car.repository.CarRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import com.carrental.shared.services.CloudinaryMediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class for Car business logic.
 * 
 * Handles all car-related operations including CRUD, search, and filtering.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarImageRepository carImageRepository;
    private final CloudinaryMediaService mediaService;

    /**
     * Get all cars
     */
    public List<CarDTO> getAllCars() {
        log.debug("Fetching all cars");
        return carRepository.findAll()
                .stream()
                .map(this::toDTOWithImages)
                .collect(Collectors.toList());
    }

    /**
     * Get car by ID
     */
    public CarDTO getCarById(Long id) {
        log.debug("Fetching car with id: {}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with id: " + id
                ));
        return toDTOWithImages(car);
    }

    /**
     * Get car by registration number
     */
    public CarDTO getCarByRegistrationNumber(String registrationNumber) {
        log.debug("Fetching car with registration number: {}", registrationNumber);
        Car car = carRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with registration number: " + registrationNumber
                ));
        return carMapper.toDTO(car);
    }

    /**
     * Get car by VIN
     */
    public CarDTO getCarByVin(String vin) {
        log.debug("Fetching car with VIN: {}", vin);
        Car car = carRepository.findByVin(vin)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with VIN: " + vin
                ));
        return carMapper.toDTO(car);
    }

    /**
     * Get all cars of a specific type
     */
    public List<CarDTO> getCarsByType(String carType) {
        log.debug("Fetching cars by type: {}", carType);
        Car.CarType type = Car.CarType.valueOf(carType.toUpperCase());
        return carRepository.findByCarType(type)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all available cars
     */
    public List<CarDTO> getAvailableCars() {
        log.debug("Fetching available cars");
        return carRepository.findAvailableCars()
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available cars by type
     */
    public List<CarDTO> getAvailableCarsByType(String carType) {
        log.debug("Fetching available cars by type: {}", carType);
        Car.CarType type = Car.CarType.valueOf(carType.toUpperCase());
        return carRepository.findAvailableCarsByType(type)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get cars within price range
     */
    public List<CarDTO> getCarsByPriceRange(Double minPrice, Double maxPrice) {
        log.debug("Fetching cars by price range: {} - {}", minPrice, maxPrice);
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new IllegalArgumentException("Invalid price range");
        }
        return carRepository.findCarsByPriceRange(minPrice, maxPrice)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search cars by make or model
     */
    public List<CarDTO> searchCars(String searchTerm) {
        log.debug("Searching cars with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.isBlank()) {
            return getAllCars();
        }
        return carRepository.searchByMakeOrModel(searchTerm)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get cars with low mileage
     */
    public List<CarDTO> getCarsWithLowMileage(Long mileage) {
        log.debug("Fetching cars with mileage below: {}", mileage);
        return carRepository.findCarsWithLowMileage(mileage)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new car
     */
    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        log.info("Creating new car: {} {}", carDTO.getMake(), carDTO.getModel());
        if (carDTO.getFleetQuantity() == null) {
            carDTO.setFleetQuantity(1);
        }

        // Check if registration number already exists
        if (carRepository.existsByRegistrationNumber(carDTO.getRegistrationNumber())) {
            log.warn("Car with registration number {} already exists", carDTO.getRegistrationNumber());
            throw new IllegalArgumentException(
                    "Car with registration number '" + carDTO.getRegistrationNumber() + "' already exists"
            );
        }

        if (carRepository.existsByLicensePlate(carDTO.getLicensePlate())) {
            throw new IllegalArgumentException(
                "Car with license plate '" + carDTO.getLicensePlate() + "' already exists"
            );
        }

        // Check if VIN already exists
        if (carRepository.existsByVin(carDTO.getVin())) {
            log.warn("Car with VIN {} already exists", carDTO.getVin());
            throw new IllegalArgumentException(
                    "Car with VIN '" + carDTO.getVin() + "' already exists"
            );
        }

        Car car = carMapper.toEntity(carDTO);
        Car savedCar = carRepository.save(car);
        log.info("Car created successfully with id: {}", savedCar.getId());

        return carMapper.toDTO(savedCar);
    }

    @Transactional(rollbackFor = Exception.class)
    public CarDTO createCarWithImages(CarDTO carDTO, List<MultipartFile> images) {
        if (images == null || images.stream().noneMatch(file -> file != null && !file.isEmpty())) {
            throw new IllegalArgumentException("At least one car image is required");
        }

        CarDTO created = createCar(carDTO);
        Car car = carRepository.findById(created.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Created car could not be loaded"));
        carRepository.flush();
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) continue;
            CloudinaryMediaService.UploadedMedia uploaded = mediaService.uploadImage(image, "rentalcar/cars/" + car.getId());
            carImageRepository.save(CarImage.builder().car(car).url(uploaded.url()).publicId(uploaded.publicId()).build());
        }
        return toDTOWithImages(car);
    }

    /**
     * Update existing car
     */
    @Transactional
    public CarDTO updateCar(Long id, CarDTO carDTO) {
        log.info("Updating car with id: {}", id);

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with id: " + id
                ));

        // Check if new registration number is already taken
        if (!car.getRegistrationNumber().equals(carDTO.getRegistrationNumber()) &&
            carRepository.existsByRegistrationNumber(carDTO.getRegistrationNumber())) {
            log.warn("Car with registration number {} already exists", carDTO.getRegistrationNumber());
            throw new IllegalArgumentException(
                    "Car with registration number '" + carDTO.getRegistrationNumber() + "' already exists"
            );
        }

        if (!car.getLicensePlate().equals(carDTO.getLicensePlate()) &&
            carRepository.existsByLicensePlate(carDTO.getLicensePlate())) {
            throw new IllegalArgumentException(
                "Car with license plate '" + carDTO.getLicensePlate() + "' already exists"
            );
        }

        // Check if new VIN is already taken
        if (!car.getVin().equals(carDTO.getVin()) &&
            carRepository.existsByVin(carDTO.getVin())) {
            log.warn("Car with VIN {} already exists", carDTO.getVin());
            throw new IllegalArgumentException(
                    "Car with VIN '" + carDTO.getVin() + "' already exists"
            );
        }

        carMapper.updateEntityFromDTO(carDTO, car);
        Car updatedCar = carRepository.save(car);
        log.info("Car updated successfully");

        return carMapper.toDTO(updatedCar);
    }

    /**
     * Delete car
     */
    @Transactional
    public void deleteCar(Long id) {
        log.info("Deleting car with id: {}", id);

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with id: " + id
                ));

        carRepository.delete(car);
        log.info("Car deleted successfully");
    }

    private CarDTO toDTOWithImages(Car car) {
        CarDTO dto = carMapper.toDTO(car);
        dto.setImageUrls(carImageRepository.findByCarIdOrderByCreatedAtAsc(car.getId()).stream()
                .map(CarImage::getUrl)
                .toList());
        return dto;
    }

    /**
     * Update car status
     */
    @Transactional
    public CarDTO updateCarStatus(Long id, String status) {
        log.info("Updating car status - id: {}, status: {}", id, status);

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with id: " + id
                ));

        try {
            Car.CarStatus carStatus = Car.CarStatus.valueOf(status.toUpperCase());
            car.setStatus(carStatus);
            Car updatedCar = carRepository.save(car);
            log.info("Car status updated successfully");
            return carMapper.toDTO(updatedCar);
        } catch (IllegalArgumentException e) {
            log.error("Invalid car status: {}", status);
            throw new IllegalArgumentException("Invalid car status: " + status);
        }
    }

    /**
     * Update car mileage
     */
    @Transactional
    public CarDTO updateCarMileage(Long id, Long mileage) {
        log.info("Updating car mileage - id: {}, mileage: {}", id, mileage);

        if (mileage < 0) {
            throw new IllegalArgumentException("Mileage cannot be negative");
        }

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found with id: " + id
                ));

        car.setMileage(mileage);
        Car updatedCar = carRepository.save(car);
        log.info("Car mileage updated successfully");

        return carMapper.toDTO(updatedCar);
    }

    /**
     * Get car count by status
     */
    public Long getCarCountByStatus(String status) {
        log.debug("Getting car count for status: {}", status);
        Car.CarStatus carStatus = Car.CarStatus.valueOf(status.toUpperCase());
        return carRepository.countByStatus(carStatus);
    }

    /**
     * Get total cars count
     */
    public Long getTotalCarsCount() {
        log.debug("Getting total cars count");
        return carRepository.count();
    }

    /**
     * Get cars by transmission type
     */
    public List<CarDTO> getCarsByTransmission(String transmission) {
        log.debug("Fetching cars by transmission: {}", transmission);
        Car.TransmissionType transmissionType = Car.TransmissionType.valueOf(transmission.toUpperCase());
        return carRepository.findByTransmission(transmissionType)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get cars by fuel type
     */
    public List<CarDTO> getCarsByFuelType(String fuelType) {
        log.debug("Fetching cars by fuel type: {}", fuelType);
        Car.FuelType type = Car.FuelType.valueOf(fuelType.toUpperCase());
        return carRepository.findByFuelType(type)
                .stream()
                .map(carMapper::toDTO)
                .collect(Collectors.toList());
    }
}