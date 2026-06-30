package com.airlinebooking.booking.mapper;

import com.airlinebooking.booking.entity.BookingEntity;
import com.airlinebooking.booking.payload.response.BookingResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    public List<BookingResponse> toResponseList(List<BookingEntity> bookingEntityList){
        List<BookingResponse> bookingResponseList = new ArrayList<>();

        for(BookingEntity b : bookingEntityList){
            bookingResponseList.add(toResponse(b));
        }

        return bookingResponseList;
    }
}
