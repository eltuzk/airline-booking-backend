package com.airlinebooking.airport;

import com.airlinebooking.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Airports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport extends BaseEntity {

    @Id
    @Column(name = "AirportCode", length = 3)
    private String airportCode;

    @Column(name = "AirportName", nullable = false, length = 100)
    private String airportName;

    @Column(name = "City", nullable = false, length = 100)
    private String city;

    @Column(name = "CountryID", nullable = false)
    private Integer countryId;

    @Column(name = "Timezone", length = 50)
    private String timezone;
}
