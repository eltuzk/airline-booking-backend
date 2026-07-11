package com.airlinebooking.airline;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAircraftRequest {

    @NotNull(message = "Airline ID is required")
    private Integer airlineId;

    @NotBlank(message = "Model is required")
    private String model;

    private String registrationNumber;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;
}
