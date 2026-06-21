package com.airlinebooking.booking.mapper;

import com.airlinebooking.booking.entity.BookingEntity;
import com.airlinebooking.booking.payload.response.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(BookingEntity bookingEntity){
        if(bookingEntity == null){
            return null;
        }

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingId(bookingEntity.getBookingId());
        bookingResponse.setBookingCode(bookingEntity.getBookingCode());
        bookingResponse.setContactName(bookingEntity.getContactName());
        bookingResponse.setContactEmail(bookingEntity.getContactEmail());
        bookingResponse.setTotalAmount(bookingEntity.getTotalAmount());
        bookingResponse.setStatus(bookingEntity.getStatus());

        return bookingResponse;
    }
}
