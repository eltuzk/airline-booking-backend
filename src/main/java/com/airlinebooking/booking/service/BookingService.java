package com.airlinebooking.booking.service;

import com.airlinebooking.booking.entity.BookingEntity;
import com.airlinebooking.booking.entity.FlightEntity;
import com.airlinebooking.booking.entity.PassengerEntity;
import com.airlinebooking.booking.payload.request.BookingRequest;
import com.airlinebooking.booking.payload.response.BookingResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {


    //tạo booking
    BookingResponse createBooking(BookingRequest bookingRequest, Integer userId);


    // unlock ghế trên redis với id booking
    void unlockSeatsByBookingId(Integer bookingId);

    String getEmailByBookingId(Integer bookingId);

    List<BookingResponse> getMyBookingsByUserId(Integer userId);




}
