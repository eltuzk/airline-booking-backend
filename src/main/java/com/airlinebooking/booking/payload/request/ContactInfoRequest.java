package com.airlinebooking.booking.payload.request;

import lombok.Data;

@Data
public class ContactInfoRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
}
