package com.airlinebooking.booking.controller;

import com.airlinebooking.booking.exceptions.AppException;
import com.airlinebooking.booking.exceptions.ErrorCode;
import com.airlinebooking.booking.payload.request.LockSeatRequest;
import com.airlinebooking.booking.payload.response.BaseResponse;
import com.airlinebooking.booking.payload.response.SeatResponse;
import com.airlinebooking.booking.service.RedisService;
import com.airlinebooking.booking.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private SeatService seatService;

    @PostMapping("/lock")
    public ResponseEntity<?> lockSeat(@Valid @RequestBody LockSeatRequest lockSeatRequest){
        Integer currentUserId = 1;      // tạm thời set cứng id của khách hàng vì chưa làm xong
        boolean isLocked = redisService.lockSeat(lockSeatRequest.getFlightId(), currentUserId, lockSeatRequest.getSeatNumber());

        if (!isLocked) {
            throw new AppException(ErrorCode.SEAT_ALREADY_LOCKED);
        }

        // Nếu qua được đoạn trên -> Chắc chắn thành công
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setMessage("Bạn đã chọn ghế, có 15 phút để thao tác");
        return ResponseEntity.ok(baseResponse);



    }


    @DeleteMapping("/unlock")
    public ResponseEntity<?> unlockSeat(@Valid @RequestBody LockSeatRequest lockSeatRequest){

        Integer currentUserId = 1;      // tạm thời set cứng id của khách hàng vì chưa làm xong

        boolean isUnlocked = redisService.unlockSeat(lockSeatRequest.getFlightId(), currentUserId, lockSeatRequest.getSeatNumber());



        if (!isUnlocked) {
            throw new AppException(ErrorCode.SEAT_UNLOCK_FAILED);
        }

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setMessage("Đã nhả ghế thành công");
        return ResponseEntity.ok(baseResponse);



    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<?> getSeatMap(@Valid @PathVariable Integer flightId){
        BaseResponse baseResponse = new BaseResponse();

        List<SeatResponse> seatResponseList = new ArrayList<>();

        seatResponseList = seatService.getSeatMap(flightId);


        baseResponse.setCode(200);
        baseResponse.setMessage("Danh sách ghế");
        baseResponse.setData(seatResponseList);
        return ResponseEntity.ok(baseResponse);


    }



}
