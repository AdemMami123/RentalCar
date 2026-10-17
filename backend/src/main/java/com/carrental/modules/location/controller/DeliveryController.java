package com.carrental.modules.location.controller;

import com.carrental.modules.location.dto.DeliveryQuoteDTO;
import com.carrental.modules.location.dto.DeliveryQuoteRequest;
import com.carrental.modules.location.service.DeliveryService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<DeliveryQuoteDTO>> quote(@Valid @RequestBody DeliveryQuoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.quote(request),
                "Delivery eligibility calculated successfully"));
    }
}
