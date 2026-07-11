package com.airlinebooking.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Integer seatId;
    private Integer flightId;
    private String seatNumber;
    private SeatClass seatClass;
    private SeatType seatType;
    private BigDecimal priceMultiplier;
    private Boolean isEmpty;
}
