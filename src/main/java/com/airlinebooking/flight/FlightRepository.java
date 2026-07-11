package com.airlinebooking.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    boolean existsByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE " +
           "(:departureAirportCode IS NULL OR f.departureAirport.airportCode = :departureAirportCode) AND " +
           "(:arrivalAirportCode IS NULL OR f.arrivalAirport.airportCode = :arrivalAirportCode) AND " +
           "(:startOfDay IS NULL OR f.departureTime >= :startOfDay) AND " +
           "(:endOfDay IS NULL OR f.departureTime < :endOfDay)")
    List<Flight> searchFlights(@Param("departureAirportCode") String departureAirportCode,
                               @Param("arrivalAirportCode") String arrivalAirportCode,
                               @Param("startOfDay") LocalDateTime startOfDay,
                               @Param("endOfDay") LocalDateTime endOfDay);
}
