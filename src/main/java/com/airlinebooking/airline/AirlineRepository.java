package com.airlinebooking.airline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {
    boolean existsByAirlineCode(String airlineCode);
}
