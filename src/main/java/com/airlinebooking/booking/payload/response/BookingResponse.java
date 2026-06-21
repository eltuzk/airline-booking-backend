package com.airlinebooking.booking.payload.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingResponse {
    private Integer bookingId;
    private String bookingCode; // Mã PNR (VD: A8F9X1)
    private String contactName;
    private String contactEmail;
    private BigDecimal totalAmount;
    private String status;
}
