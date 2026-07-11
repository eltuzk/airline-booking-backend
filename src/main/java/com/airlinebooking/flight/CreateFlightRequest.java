package com.airlinebooking.flight;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateFlightRequest {

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotNull(message = "Airline ID is required")
    private Integer airlineId;

    @NotNull(message = "Aircraft ID is required")
    private Integer aircraftId;

    @NotBlank(message = "Departure airport code is required")
    private String departureAirportCode;

    @NotBlank(message = "Arrival airport code is required")
    private String arrivalAirportCode;

    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;
    
    private FlightStatus status = FlightStatus.Scheduled;
}
