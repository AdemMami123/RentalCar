package com.carrental.modules.location.dto;

import java.math.BigDecimal;

public record DeliveryQuoteDTO(boolean eligible, String message, BigDecimal distanceKm,
                               BigDecimal deliveryFee, Integer minimumNoticeHours,
                               Long sourceLocationId) {}
