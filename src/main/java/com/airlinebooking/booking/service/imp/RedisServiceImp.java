package com.airlinebooking.booking.service.imp;

import com.airlinebooking.booking.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
public class RedisServiceImp implements RedisService {

    //vì setIfAbsent nhận phần thời gian dạng long
    private static final long HOLD_TIME = 15;
    // mặc định mỗi lần quét 100 keys
    private static final int QUANTITY_KEYS = 100;

    @Autowired
    private StringRedisTemplate redisTemplate;




    // hàm này
    @Override
    public boolean lockSeat(Integer flightId, Integer userId, String seatNumber) {
        // kết thành một định dạng chuỗi ban đầu định dạng
        // đó sẽ là key và value là id người dùng
        String key = "booking:flight:" + flightId + ":seat:" + seatNumber;
        String value = String.valueOf(userId);

        // sau đó sẽ check và lưu trên redis, lưu thành công trả true (và trên redis sẽ lưu key và value), và ngược lại
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(key, value, HOLD_TIME, TimeUnit.MINUTES);

        //ở đây là kiểu trả về là Boolean nên có thẻ nhận null nên pha dùng so sánh
        return Boolean.TRUE.equals(isLocked);
    }

    @Override
    public boolean isSeatHoldByCurrentUser(Integer flightId, Integer userId, String seatNumber) {

        String key = "booking:flight:" + flightId + ":seat:" + seatNumber;
        String value = String.valueOf(userId);

        String userIdCurrent = redisTemplate.opsForValue().get(key);

        return  userIdCurrent != null && userIdCurrent.equals(value);
    }

    @Override
    public boolean unlockSeat(Integer flightId, Integer userId, String seatNumber) {

        String key = "booking:flight:" + flightId + ":seat:" + seatNumber;
        String value = String.valueOf(userId);


        // tạo một đoạn script đẻ redis khi thực hiện đoạn này sẽ khóa tất cả cuwar khác lại
        String lauScript =  """
                        if redis.call('get', KEYS[1]) == ARGV[1] then
                            return redis.call('del', KEYS[1])
                        else
                            return 0
                        end
                        """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(lauScript);
        redisScript.setResultType(Long.class);

        Long result =  redisTemplate.execute(redisScript, List.of(key), value);

        return result != null && result == 1L;

    }

    @Override
    public Set<String> scanKeys(String pattern) {
        RedisScanServiceImp redisScanServiceImp = new RedisScanServiceImp();
        redisScanServiceImp.setPatternToMatch(pattern);
        redisScanServiceImp.setCountScan(QUANTITY_KEYS);

        Set<String> result = redisTemplate.execute(redisScanServiceImp);

        return result;
    }
}
