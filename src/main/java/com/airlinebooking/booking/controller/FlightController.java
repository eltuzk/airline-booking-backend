package com.airlinebooking.booking.controller;

import com.airlinebooking.booking.payload.request.FlightSearchRequest;
import com.airlinebooking.booking.payload.response.BaseResponse;
import com.airlinebooking.booking.payload.response.FlightSearchResponse;
import com.airlinebooking.booking.payload.response.PageResponse;
import com.airlinebooking.booking.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<?> searchFlights(@Valid @ModelAttribute FlightSearchRequest flightSearchRequest,
                                           @PageableDefault(
                                                   page = 1,
                                                   size = 10,
                                                   sort = "basePrice",
                                                   direction = Sort.Direction.ASC //Giá tăng dần
                                           ) Pageable pageable){
        BaseResponse baseResponse = new BaseResponse();

        PageResponse<FlightSearchResponse> pageResponse = flightService.searchFlights(flightSearchRequest, pageable);






        baseResponse.setCode(200);
        baseResponse.setMessage("Đây là danh sách các chuyến bay thỏa mãn điều kiện");
        baseResponse.setData(pageResponse);
        return ResponseEntity.ok(baseResponse);



    }
}
