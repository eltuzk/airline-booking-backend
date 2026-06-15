package com.airlinebooking.booking.service;

public interface RedisService {
    //Hàm lock ghế khi mà được chọn
    public boolean lockSeat(int flightId, int userId, String seatNumber);

    //Hàm check xem ghế hiện tại có phải là chur nhân đó không (TH người đó refesh lại trang)
    public boolean isSeatHoldByCurrentUser(int flightID, int userId, String seatNumber);
}
