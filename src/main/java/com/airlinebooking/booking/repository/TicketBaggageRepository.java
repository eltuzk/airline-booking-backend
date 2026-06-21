package com.airlinebooking.booking.repository;

import com.airlinebooking.booking.entity.TicketBaggageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketBaggageRepository extends JpaRepository<TicketBaggageEntity, Integer> {
}
