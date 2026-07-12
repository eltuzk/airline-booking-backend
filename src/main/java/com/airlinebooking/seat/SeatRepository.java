package com.airlinebooking.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByFlightFlightId(Integer flightId);
    boolean existsByFlightFlightId(Integer flightId);
}
