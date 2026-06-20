package com.airlinebooking.booking.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "seats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seatId;

    private String seatNumber;

    private String seatClass;

    private String seatType;

    private String seatStatus;

    private BigDecimal priceMultiplier;

    @Version
    private Long version;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;


    @OneToMany(mappedBy = "seat")
    private List<PassengerTicketEntity> passengerTicketEntityList;
}
