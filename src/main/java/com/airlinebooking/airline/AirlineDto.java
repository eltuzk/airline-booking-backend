package com.airlinebooking.airline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirlineDto {
    private Integer airlineId;
    private String airlineCode;
    private String airlineName;
    private String logoUrl;
}
