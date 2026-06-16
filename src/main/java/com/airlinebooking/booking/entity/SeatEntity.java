package com.airlinebooking.booking.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity(name = "seats")
@Getter
@Setter
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seatId;

    private String seatNumber;

    private String seatClass;

    private String seatType;

    private String status;

    private Double priceMultiplier;

    @Version
    private BigDecimal version;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;


    @OneToMany(mappedBy = "seat")
    private List<PassengerTicketEntity> passengerTicketEntityList;
}
