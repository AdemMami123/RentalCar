package com.carrental.modules.location.service;

import com.carrental.modules.location.dto.DeliveryQuoteDTO;
import com.carrental.modules.location.dto.DeliveryQuoteRequest;
import com.carrental.modules.location.entity.Location;
import com.carrental.modules.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final LocationRepository locationRepository;

    public DeliveryQuoteDTO quote(DeliveryQuoteRequest request) {
        if (request.scheduledAt().isBefore(LocalDateTime.now().plusHours(24))) {
            return new DeliveryQuoteDTO(false, "Delivery requires at least 24 hours notice.",
                    null, null, 24, null);
        }
        Location source = locationRepository.findByActiveTrueOrderByNameAsc().stream()
                .filter(Location::isDeliveryEnabled)
                .filter(location -> location.getLatitude() != null && location.getLongitude() != null)
                .min((left, right) -> Double.compare(distanceKm(left, request), distanceKm(right, request)))
                .orElse(null);
        if (source == null) {
            return new DeliveryQuoteDTO(false, "No delivery office is currently available.", null, null, 24, null);
        }
        BigDecimal distance = BigDecimal.valueOf(distanceKm(source, request)).setScale(2, RoundingMode.HALF_UP);
        if (distance.compareTo(BigDecimal.valueOf(25)) > 0) {
            return new DeliveryQuoteDTO(false, "This address is outside the supported delivery area.",
                    distance, null, 24, source.getId());
        }
        BigDecimal fee = BigDecimal.valueOf(15).add(distance.multiply(BigDecimal.valueOf(1.5)))
                .setScale(2, RoundingMode.HALF_UP);
        return new DeliveryQuoteDTO(true, "Delivery is available.", distance, fee, 24, source.getId());
    }

    private double distanceKm(Location source, DeliveryQuoteRequest target) {
        return haversine(source.getLatitude().doubleValue(), source.getLongitude().doubleValue(),
                target.latitude().doubleValue(), target.longitude().doubleValue());
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
