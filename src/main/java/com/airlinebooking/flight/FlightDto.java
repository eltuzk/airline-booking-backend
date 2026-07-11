package com.airlinebooking.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {
    private Integer flightId;
    private String flightNumber;
    private Integer airlineId;
    private String airlineName;
    private Integer aircraftId;
    private String aircraftModel;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;
    private FlightStatus status;
}
