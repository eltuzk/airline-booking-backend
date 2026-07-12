package com.airlinebooking.seat;

import com.airlinebooking.common.BaseEntity;
import com.airlinebooking.flight.Flight;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"FlightID", "SeatNumber"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SeatID")
    private Integer seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FlightID", nullable = false)
    private Flight flight;

    @Column(name = "SeatNumber", nullable = false, length = 10)
    private String seatNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "SeatClass")
    private SeatClass seatClass = SeatClass.Economy;

    @Enumerated(EnumType.STRING)
    @Column(name = "SeatType")
    private SeatType seatType;

    @Builder.Default
    @Column(name = "PriceMultiplier", precision = 4, scale = 2)
    private BigDecimal priceMultiplier = BigDecimal.ONE;

    @Builder.Default
    @Column(name = "IsEmpty")
    private Boolean isEmpty = true;
}
