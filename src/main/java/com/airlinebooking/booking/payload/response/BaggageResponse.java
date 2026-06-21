package com.airlinebooking.booking.payload.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BaggageResponse {
    private Integer baggageConfigId;
    private BigDecimal weightInKg;
    private BigDecimal price;

}
