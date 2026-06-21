package com.airlinebooking.booking.service;

import com.airlinebooking.booking.entity.BookingEntity;
import com.airlinebooking.booking.entity.FlightEntity;
import com.airlinebooking.booking.entity.PassengerEntity;
import com.airlinebooking.booking.payload.request.BookingRequest;
import com.airlinebooking.booking.payload.response.BookingResponse;

import java.math.BigDecimal;

public interface BookingService {


    //tạo booking
    BookingResponse createBooking(BookingRequest bookingRequest, Integer userId);

}
