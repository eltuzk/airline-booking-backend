package com.airlinebooking.booking.controller;

import com.airlinebooking.booking.payload.request.BookingRequest;
import com.airlinebooking.booking.payload.response.BaseResponse;
import com.airlinebooking.booking.payload.response.BookingResponse;
import com.airlinebooking.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request){
        //gán cứng userId = 1 trước đẻ test
        Integer curUserId = 1;


        BaseResponse baseResponse = new BaseResponse();

        BookingResponse bookingResponse = bookingService.createBooking(request, curUserId);

        baseResponse.setCode(200);
        baseResponse.setMessage("Tạo booking thành công, mời thanh toán trong vòng 15p!!!");
        baseResponse.setData(bookingResponse);

        return ResponseEntity.ok(baseResponse);
    }


}
