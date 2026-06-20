package com.airlinebooking.booking.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flights")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flightId;

    private String flightNumber;

    // Giữ nguyên là Integer để không cần phải tạo thêm thực thể Airline
    private Integer airlineId;

    // Giữ nguyên là Integer để không cần phải tạo thêm thực thể Aircraft
    private Integer aircraftId;

    private String departureAirportCode;

    private String arrivalAirportCode;

    private LocalDateTime departureTime; // DATETIME trong MySQL tương ứng với LocalDateTime

    private LocalDateTime arrivalTime;

    private BigDecimal basePrice; // DECIMAL trong MySQL tương ứng với BigDecimal

    private Integer availableSeats;

    private String status; // SCHEDULED, DELAYED, CANCELLED, COMPLETED

    @Version // Hỗ trợ Optimistic Locking như thiết kế DB của nhóm bạn
    private Long version;

    @OneToMany(mappedBy = "flight")
    private List<SeatEntity> seatEntityList;
}
