package com.airlinebooking.booking.controller;

import com.airlinebooking.booking.payload.request.LockSeatRequest;
import com.airlinebooking.booking.payload.response.BaseResponse;
import com.airlinebooking.booking.service.RedisService;
import com.airlinebooking.booking.service.imp.RedisServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private RedisService redisService;

    @PostMapping("/lock")
    public ResponseEntity<?> lockSeat(@RequestBody LockSeatRequest lockSeatRequest){
        Integer currentUserId = 1;      // tạm thời set cứng id của khách hàng vì chưa làm xong
        boolean isLocked = redisService.lockSeat(lockSeatRequest.getFlightId(), currentUserId, lockSeatRequest.getSeatNumber());




        BaseResponse baseResponse = new BaseResponse();

        if(isLocked){
            baseResponse.setCode(200);
            baseResponse.setMessage("Bạn đã chọn ghế, có 15 phút để thao tác");
            return ResponseEntity.ok(baseResponse);
        } else {
            baseResponse.setCode(400);
            baseResponse.setMessage("Xin lỗi, ghế này đã có người chọn");
            return ResponseEntity.badRequest().body(baseResponse);
        }



    }


    @DeleteMapping("/unlock")
    public ResponseEntity<?> unlockSeat(@RequestBody LockSeatRequest lockSeatRequest){
        Integer currentUserId = 1;      // tạm thời set cứng id của khách hàng vì chưa làm xong
        boolean isUnlocked = redisService.unlockSeat(lockSeatRequest.getFlightId(), currentUserId, lockSeatRequest.getSeatNumber());




        BaseResponse baseResponse = new BaseResponse();

        if(isUnlocked){
            baseResponse.setCode(200);
            baseResponse.setMessage("Đã nhả ghế");
            return ResponseEntity.ok(baseResponse);
        } else {
            baseResponse.setCode(400);
            baseResponse.setMessage("Không thể nhả ghế hoặc ghế đã hết hạn");
            return ResponseEntity.badRequest().body(baseResponse);
        }



    }



}
