package com.airlinebooking.booking.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PassengerRequest {
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh nhật phải ở trong quá khứ")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$",
                message = "Giới tính chỉ chấp nhận MALE, FEMALE hoặc OTHER")
    private String gender;

    @NotBlank(message = "Loại hành khách không được để trống")
    @Pattern(regexp = "^(ADULT|CHILD|INFANT)$",
            message = "Loại hành khách chỉ chấp nhận ADULT, CHILD hoặc INFANT")
    private String passengerType;


    // Chiều đi
    @Pattern(regexp = "^[0-9]{1,2}[A-Z]$",
                message = "Mã ghế chiều đi không đúng định dạng")
    private String runSeatNumber; // Null nếu là INFANT  (Trẻ sơ sinh)

    @Min(value = 1, message = "ID hành lý chiều đi không hợp lệ")
    private Integer runBaggageId; // Null nếu không mua

    // Chiều về
    @Pattern(regexp = "^[0-9]{1,2}[A-Z]$",
            message = "Mã ghế chiều về không đúng định dạng")
    private String returnSeatNumber;   // Null nếu bay 1 chiều
    @Min(value = 1, message = "ID hành lý chiều về không hợp lệ")
    private Integer returnBaggageId;
}
