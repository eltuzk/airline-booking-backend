package com.airlinebooking.booking.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    @NotNull(message = "Không được để trống ID chiều đi")
    private Integer runFlightId;


    private Integer returnFlightId;     // nếu không bay về thì 1 chiều

    @Valid
    @NotNull(message = "Thông tin liên hệ không đưuọc bỏ trống")
    private ContactInfoRequest contactInfoRequest;

    @Valid
    @NotNull(message = "Dánh sách không đưuọc để trống")
    @Size(min = 1, message = "Phải có ít nhất 1 hành khách")
    private List<PassengerRequest> passengerRequestList;
}
