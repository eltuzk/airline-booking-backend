package com.airlinebooking.flight;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDto> getFlightById(@PathVariable Integer id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(@RequestBody FlightSearchRequest request) {
        return ResponseEntity.ok(flightService.searchFlights(request));
    }

    @PostMapping
    public ResponseEntity<FlightDto> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        return new ResponseEntity<>(flightService.createFlight(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDto> updateFlight(@PathVariable Integer id, @Valid @RequestBody CreateFlightRequest request) {
        return ResponseEntity.ok(flightService.updateFlight(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
