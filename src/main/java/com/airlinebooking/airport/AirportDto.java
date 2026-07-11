package com.airlinebooking.airport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportDto {
    private String airportCode;
    private String airportName;
    private String city;
    private Integer countryId;
    private String timezone;
}
