package com.airlinebooking.airline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftDto {
    private Integer aircraftId;
    private Integer airlineId;
    private String airlineName;
    private String model;
    private String registrationNumber;
    private Integer totalSeats;
}
