package com.airlinebooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.util.List;

@Entity(name = "baggage_catalog")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaggageCatalogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer baggageConfigId;

    private BigDecimal weightInKg;
    private BigDecimal price;

    @OneToMany(mappedBy = "baggageCatalog")
    private List<TicketBaggageEntity> ticketBaggageEntityList;

}
