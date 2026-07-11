package com.airlinebooking.airline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Aircraft> findByAirlineAirlineId(Integer airlineId);
}
