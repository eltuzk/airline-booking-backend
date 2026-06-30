package com.airlinebooking.payment.service;

import com.airlinebooking.booking.service.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingKafkaConsumerService {
    private final RedisService redisService;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "booking-payment-completed-topic", groupId = "airline-booking-group")
    public void listen(String message){
        log.info("KAFKA CONSUMER: Nhận được tin nhắn: {}", message);



        //1. phân tích tin nắn để lấy bookignId
        // Ở bước trước ta gửi {"bookingId":1,"status":"SUCCESS"}
        Integer bookingId = extractBookingId(message);


        // tại đây xóa ghế trên redis
        log.info("KAFKA CONSUMER: đang tiến hành xóa lock ghế cho bookingId: {}", bookingId);
        redisService.unlockSeat()

    }


    private Integer extractBookingId(String message){
        try{
            // đọc chuỗi message thành 1 câu Json
            JsonNode jsonNode = objectMapper.readTree(message);


            //Laays ra gias trij coojt boooking id
            Integer value = jsonNode.get("bookingId").asInt();

            return value;


        } catch (Exception e) {
            log.error("Lỗi khi parse tin nhắn Kafka: {}", message, e);
            return null;
        }
    }

}
