package com.airlinebooking.airline;

import com.airlinebooking.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Airlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AirlineID")
    private Integer airlineId;

    @Column(name = "AirlineCode", nullable = false, unique = true, length = 10)
    private String airlineCode;

    @Column(name = "AirlineName", nullable = false, length = 100)
    private String airlineName;

    @Column(name = "LogoUrl", length = 255)
    private String logoUrl;
}
