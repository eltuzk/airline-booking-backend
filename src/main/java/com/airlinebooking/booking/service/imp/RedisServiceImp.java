package com.airlinebooking.booking.service.imp;

import com.airlinebooking.booking.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class RedisServiceImp implements RedisService {

    //vì setIfAbsent nhận phần thời gian dạng long
    private static final long HOLD_TIME = 15;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    // hàm này
    @Override
    public boolean lockSeat(int flightId, int userId, String seatNumber) {
        // kết thành một định dạng chuỗi ban đầu định dạng
        // đó sẽ là key và value là id người dùng
        String key = "booking:key:" + flightId + ":seat:" + seatNumber;
        String value = String.valueOf(userId);

        // sau đó sẽ check và lưu trên redis, lưu thành công trả true (và trên redis sẽ lưu key và value), và ngược lại
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(key, value, HOLD_TIME, TimeUnit.MINUTES);

        //ở đây là kiểu trả về là Boolean nên có thẻ nhận null nên pha dùng so sánh
        return Boolean.TRUE.equals(isLocked);
    }

    @Override
    public boolean isSeatHoldByCurrentUser(int flightId, int userId, String seatNumber) {

        String key = "booking:key:" + flightId + ":seat:" + seatNumber;
        String value = String.valueOf(userId);

        String userIdCurrent = redisTemplate.opsForValue().get(key);

        return userIdCurrent.equals(value) && userIdCurrent != null;
    }
}
