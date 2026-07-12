package com.airlinebooking.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights/{flightId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<List<SeatDto>> getSeatsForFlight(@PathVariable Integer flightId) {
        return ResponseEntity.ok(seatService.getSeatsByFlightId(flightId));
    }

    @PostMapping("/generate")
    public ResponseEntity<List<SeatDto>> generateSeatsForFlight(@PathVariable Integer flightId) {
        return new ResponseEntity<>(seatService.generateSeatsForFlight(flightId), HttpStatus.CREATED);
    }
}
