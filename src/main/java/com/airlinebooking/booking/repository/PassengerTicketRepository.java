package com.airlinebooking.booking.repository;

import com.airlinebooking.booking.entity.PassengerTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerTicket extends JpaRepository<PassengerTicketEntity, Integer> {
}
