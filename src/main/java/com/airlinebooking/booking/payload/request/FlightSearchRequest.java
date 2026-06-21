package com.airlinebooking.booking.payload.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class FlightSearchRequest {
    private String departureCode;
    private String arrivalCode;

    // format dạng chuẩn khi truyền yyyy-MM-dd leen
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    // Gán giá trị mặc định luôn ở đây cho gọn
    private Integer adults = 1;
    private Integer children = 0;
    private Integer infants = 0;

}
