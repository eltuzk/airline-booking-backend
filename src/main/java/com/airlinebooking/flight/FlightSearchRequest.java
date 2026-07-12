package com.airlinebooking.flight;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FlightSearchRequest {
    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDate departureDate;
}
