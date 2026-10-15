package com.carrental.modules.car.repository;

import com.carrental.modules.car.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {
    List<CarImage> findByCarIdOrderByCreatedAtAsc(Long carId);
}
