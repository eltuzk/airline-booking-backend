package com.airlinebooking.booking.service;

import com.airlinebooking.booking.payload.request.FlightSearchRequest;
import com.airlinebooking.booking.payload.response.FlightSearchResponse;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest flightSearchRequest);
}