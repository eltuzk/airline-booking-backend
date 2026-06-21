package com.airlinebooking.booking.service;

import com.airlinebooking.booking.payload.response.BaggageResponse;

import java.util.List;

public interface BaggageService {

    public List<BaggageResponse> getAllBaggages();
}
