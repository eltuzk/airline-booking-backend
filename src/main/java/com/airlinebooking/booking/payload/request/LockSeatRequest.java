package com.airlinebooking.booking.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockSeatRequest {

    private Integer flightId;
    private String seatNumber;
}
