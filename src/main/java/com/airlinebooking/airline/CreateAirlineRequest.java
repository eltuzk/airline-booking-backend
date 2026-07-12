package com.airlinebooking.airline;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAirlineRequest {
    @NotBlank(message = "Airline code is required")
    private String airlineCode;

    @NotBlank(message = "Airline name is required")
    private String airlineName;

    private String logoUrl;
}
