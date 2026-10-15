package com.carrental.modules.car.service;

import com.carrental.modules.car.entity.Car;
import com.carrental.modules.car.entity.FavoriteVehicle;
import com.carrental.modules.car.repository.CarRepository;
import com.carrental.modules.car.repository.FavoriteVehicleRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteVehicleService {
    private final FavoriteVehicleRepository favoriteRepository;
    private final CarRepository carRepository;

    @Transactional(readOnly = true)
    public List<Car> list(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(favorite -> carRepository.findById(favorite.getCarId())
                        .orElseThrow(() -> new ResourceNotFoundException("Favorite vehicle no longer exists")))
                .toList();
    }

    @Transactional
    public void add(Long userId, Long carId) {
        if (!carRepository.existsById(carId)) throw new ResourceNotFoundException("Car not found");
        if (favoriteRepository.findByUserIdAndCarId(userId, carId).isEmpty()) {
            favoriteRepository.save(FavoriteVehicle.builder().userId(userId).carId(carId).build());
        }
    }

    @Transactional
    public void remove(Long userId, Long carId) {
        favoriteRepository.findByUserIdAndCarId(userId, carId).ifPresent(favoriteRepository::delete);
    }
}
