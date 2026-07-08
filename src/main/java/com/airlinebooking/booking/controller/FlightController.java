package com.airlinebooking.booking.controller;

import com.airlinebooking.booking.payload.request.FlightSearchRequest;
import com.airlinebooking.booking.payload.response.BaseResponse;
import com.airlinebooking.booking.payload.response.FlightSearchResponse;
import com.airlinebooking.booking.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(@ModelAttribute FlightSearchRequest flightSearchRequest){
        BaseResponse baseResponse = new BaseResponse();


        List<FlightSearchResponse> flightSearchResponseList = flightService.searchFlights(flightSearchRequest);



        baseResponse.setCode(200);
        baseResponse.setMessage("Đây là danh sách các chuyến bay thỏa mãn điều kiện");
        baseResponse.setData(flightSearchResponseList);
        return ResponseEntity.ok(baseResponse);



    }
}
