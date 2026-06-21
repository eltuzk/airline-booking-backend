package com.airlinebooking.booking.repository;

import com.airlinebooking.booking.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {
    @Query(
            """
            SELECT f
            FROM FlightEntity f WHERE
            f.departureAirportCode = :departrueCode AND
            f.arrivalAirportCode = :arrivalCode AND
            f.departureTime BETWEEN :start AND :end AND 
            f.availableSeats >= :seats AND 
            f.status = :status            
            """)
    public List<FlightEntity> searchAvailableFlights(
            @Param("departrueCode") String departrueCode,
            @Param("arrivalCode") String arrivalCode,
            @Param("start")LocalDateTime startOfDay,
            @Param("end") LocalDateTime endOfDay,
            @Param("seats") Integer requiredSeats,
            @Param("status") String status

            );
}
