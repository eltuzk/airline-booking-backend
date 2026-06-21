package com.airlinebooking.booking.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    private Integer runFlightId;
    private Integer returnFlightId;     // nếu không bay về thì 1 chiều

    private ContactInfoRequest contactInfoRequest;
    private List<PassengerRequest> passengerRequestList;
}
