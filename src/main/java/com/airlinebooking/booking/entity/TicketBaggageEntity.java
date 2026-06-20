package com.airlinebooking.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_baggages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketBaggageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketBaggageId;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private PassengerTicketEntity ticket;

    @ManyToOne
    @JoinColumn(name = "baggage_config_id")
    private BaggageCatalogEntity baggageCatalog;
}
