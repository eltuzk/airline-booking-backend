package com.airlinebooking.booking.service;

import com.airlinebooking.booking.payload.request.FlightSearchRequest;
import com.airlinebooking.booking.payload.response.FlightSearchResponse;
import com.airlinebooking.booking.payload.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    public PageResponse<FlightSearchResponse> searchFlights(FlightSearchRequest flightSearchRequest, Pageable pageable);
}