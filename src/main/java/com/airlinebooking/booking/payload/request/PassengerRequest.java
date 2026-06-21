package com.airlinebooking.booking.payload.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PassengerRequest {
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String passengerType;


    // Chiều đi
    private String runSeatNumber; // Null nếu là INFANT  (Trẻ sơ sinh)
    private Integer runBaggageId; // Null nếu không mua

    // Chiều về
    private String returnSeatNumber;   // Null nếu bay 1 chiều
    private Integer returnBaggageId;
}
