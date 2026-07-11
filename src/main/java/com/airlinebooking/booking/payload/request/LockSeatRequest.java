package com.airlinebooking.booking.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LockSeatRequest {

    @NotNull(message = "Không được để trống id chuyến bay")
    @Min(value = 1, message = "Id chuyến bay không thể nhỏ hơn 1")
    private Integer flightId;

    @NotBlank(message = "Không đưuọc để trống số ghế")
    @Size(min = 2, max = 4, message = "Số ghế phải từ 2-4 kí tự")
    private String seatNumber;
}
