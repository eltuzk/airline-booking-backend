package com.airlinebooking.booking.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    private String bookingCode; // Mã PNR (VD: VJ123456)

    private Integer userId;     // dữ Integer để add vào không vị lỗi

    private String contactName;

    private String contactEmail;

    private String contactPhone;

    private BigDecimal totalAmount;

    private String status; // PENDING, CONFIRMED, CANCELLED

    private Boolean isDeleted = false;

    @Version // Phép thuật của JPA để chống Race Condition khi update DB
    private Long version;


    @OneToMany(mappedBy = "booking")
    private List<PassengerTicketEntity> passengerTicketEntityList;



}
