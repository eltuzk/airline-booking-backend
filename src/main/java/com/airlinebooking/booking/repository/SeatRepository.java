package com.airlinebooking.booking.repository;

import com.airlinebooking.booking.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatEntity, Integer> {

    // tìm tất cả ghế thuộc về một chuyến bay cụ thể
    List<SeatEntity> findByFlight_FlightId(Integer flightId);
}
