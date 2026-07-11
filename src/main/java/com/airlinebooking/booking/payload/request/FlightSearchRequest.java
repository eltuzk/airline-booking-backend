package com.airlinebooking.booking.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class FlightSearchRequest {
    @NotBlank(message = "Mã chuyến bay đi không được trống")
    private String departureCode;
    @NotBlank(message = "Mã chuyến bay đến không được trống")
    private String arrivalCode;

    // format dạng chuẩn khi truyền yyyy-MM-dd leen
    @NotNull(message = "Ngày tháng đi không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    // Gán giá trị mặc định luôn ở đây cho gọn
    @NotNull(message = "Số lượng người lớn không được để trống")
    @Min(value = 1,
            message = "Phải có ít nhất 1 người lớn")
    @Max(value = 99,
            message = "Số lượng người lớn không hợp lệ")
    private Integer adults = 1;



    @Min(value = 0,
            message = "Số lượng trẻ em không được là số âm")
    @Max(value = 99,
            message = "Số lượng trẻ em không hợp lệ")
    private Integer children = 0;



    @Min(value = 0,
            message = "Số lượng trẻ sơ sinh không được là số âm")
    @Max(value = 99,
            message = "Số lượng trẻ sơ sinh không hợp lệ")
    private Integer infants = 0;

}
