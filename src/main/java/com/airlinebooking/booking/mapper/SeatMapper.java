package com.airlinebooking.booking.mapper;


import com.airlinebooking.booking.entity.SeatEntity;
import com.airlinebooking.booking.payload.response.SeatResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SeatMapper {

    public SeatResponse toResponse(SeatEntity seatEntity){
        if(seatEntity == null){
            return null;
        }

        SeatResponse seatResponse = new SeatResponse();

        seatResponse.setSeatId(seatEntity.getSeatId());
        seatResponse.setSeatNumber(seatEntity.getSeatNumber());
        seatResponse.setSeatClass(seatEntity.getSeatClass());
        seatResponse.setStatus(seatEntity.getSeatStatus());


        // tính tiền đ lưu phần giá cuối cùng
        if(seatEntity.getFlight() != null &&
                seatEntity.getFlight().getBasePrice() != null &&
                seatEntity.getPriceMultiplier() != null){

            BigDecimal basePrice = seatEntity.getFlight().getBasePrice();
            BigDecimal priceMultiplier = seatEntity.getPriceMultiplier();


            seatResponse.setFinalPrice(basePrice.multiply(priceMultiplier));

        }

        return seatResponse;



    }
}
