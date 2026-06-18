package com.airlinebooking.booking.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SeatResponse {
    private Integer seatId;
    private String seatNumber;
    private String seatClass;
    private BigDecimal finalPrice;
    private String status; // Trạng thái sẽ được quyết định sau khi trộn MySQL và Redis
}
