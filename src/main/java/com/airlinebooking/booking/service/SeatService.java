package com.airlinebooking.booking.service;

import com.airlinebooking.booking.payload.response.SeatResponse;

import java.util.List;

public interface SeatService {
    public List<SeatResponse> getSeatMap(Integer flightId);
}
