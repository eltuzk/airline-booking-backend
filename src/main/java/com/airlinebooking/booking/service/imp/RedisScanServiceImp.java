package com.airlinebooking.booking.service.imp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisScanServiceImp implements RedisCallback<Set<String>> {

    private String patternToMatch;

    private int countScan;

    @Override
    public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {

        // tạo một list đểdđưunjg các key tìm thấy không trùng lặp
        Set<String> keysFoundList = new HashSet<>();

        // đưa một cái tờ mẫu buộc với các điều kiên trên để quét với điều kiện bao nhiêu số lượng mỗi lần và mẫu quét là gì
        ScanOptions options = ScanOptions.scanOptions()
                                            .match(this.patternToMatch)
                                            .count(this.countScan)
                                            .build();

        // đưa tờ mẫu đó xuống Redis và nhận lại con trỏ để qquyet
        Cursor<byte[]> redisCursor = connection.scan(options);


        try{
            while(redisCursor.hasNext()){

                // quét cục nào match với  options
                byte[] rawKey = redisCursor.next();


                // chuyển cục trên thành String
                String readableKey = new String(rawKey);

                // luưu vào giỏ
                keysFoundList.add(readableKey);


            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // dù lỗi hay không cũng phải đóng cursor nêu không tràn RAM
            redisCursor.close();

        }




        return keysFoundList;
    }
}
