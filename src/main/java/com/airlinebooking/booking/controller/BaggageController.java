package com.airlinebooking.booking.controller;

import com.airlinebooking.booking.payload.response.BaggageResponse;
import com.airlinebooking.booking.payload.response.BaseResponse;
import com.airlinebooking.booking.service.BaggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/baggages")
public class BaggageController {

    @Autowired
    private BaggageService baggageService;

    @GetMapping
    public ResponseEntity<?> getAllBaggages(){
        List<BaggageResponse> baggageResponseList = new ArrayList<>();
        baggageResponseList = baggageService.getAllBaggages();

        BaseResponse baseResponse = new BaseResponse();


        baseResponse.setCode(200);
        baseResponse.setMessage("Đây là danh sách các hạng vé hành lý");
        baseResponse.setData(baggageResponseList);
        return ResponseEntity.ok(baseResponse);



    }
}
