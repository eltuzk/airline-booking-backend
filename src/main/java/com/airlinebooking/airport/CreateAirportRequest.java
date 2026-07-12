package com.airlinebooking.airport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAirportRequest {

    @NotBlank(message = "Airport code is required")
    @Size(min = 3, max = 3, message = "Airport code must be exactly 3 characters")
    private String airportCode;

    @NotBlank(message = "Airport name is required")
    private String airportName;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Country ID is required")
    private Integer countryId;

    private String timezone;
}
