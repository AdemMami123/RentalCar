package com.carrental.modules.car.controller;

import com.carrental.modules.car.entity.Car;
import com.carrental.modules.car.service.FavoriteVehicleService;
import com.carrental.shared.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FavoriteVehicleController {
    private final FavoriteVehicleService favoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Car>>> list(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                favoriteService.list(userId(authentication)), "Favorites retrieved successfully"));
    }

    @PostMapping("/{carId}")
    public ResponseEntity<ApiResponse<Void>> add(@PathVariable Long carId, Authentication authentication) {
        favoriteService.add(userId(authentication), carId);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle saved to favorites"));
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long carId, Authentication authentication) {
        favoriteService.remove(userId(authentication), carId);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle removed from favorites"));
    }

    private Long userId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
