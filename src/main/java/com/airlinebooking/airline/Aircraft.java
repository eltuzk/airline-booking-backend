package com.airlinebooking.airline;

import com.airlinebooking.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Aircrafts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aircraft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AircraftID")
    private Integer aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AirlineID", nullable = false)
    private Airline airline;

    @Column(name = "Model", nullable = false, length = 100)
    private String model;

    @Column(name = "RegistrationNumber", unique = true, length = 50)
    private String registrationNumber;

    @Column(name = "TotalSeats", nullable = false)
    private Integer totalSeats;
}
