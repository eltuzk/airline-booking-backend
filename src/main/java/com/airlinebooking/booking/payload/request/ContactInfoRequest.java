package com.airlinebooking.booking.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContactInfoRequest {
    @NotBlank(message = "Tên của người booking không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(regexp = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$",
            message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại người booking không được để trống")
    @Pattern(regexp = "^0[0-9]{9,10}$",
                message = "Số điện thoại không hợp lệ (phải có 10 chữ số và bắt đầu bằng số 0)")
    private String phoneNumber;
}
