package com.airlinebooking.booking.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "passenger_tickets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerTicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketId;

    private BigDecimal ticketPrice;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private PassengerEntity passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", unique = true)
    private SeatEntity seat;

}
