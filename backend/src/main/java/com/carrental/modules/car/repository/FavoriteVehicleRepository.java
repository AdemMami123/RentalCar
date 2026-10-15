package com.carrental.modules.car.repository;

import com.carrental.modules.car.entity.FavoriteVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteVehicleRepository extends JpaRepository<FavoriteVehicle, Long> {
    List<FavoriteVehicle> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteVehicle> findByUserIdAndCarId(Long userId, Long carId);
}
