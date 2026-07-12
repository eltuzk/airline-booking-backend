package com.airlinebooking.airport;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping
    public ResponseEntity<List<AirportDto>> getAllAirports() {
        return ResponseEntity.ok(airportService.getAllAirports());
    }

    @GetMapping("/{code}")
    public ResponseEntity<AirportDto> getAirportByCode(@PathVariable String code) {
        return ResponseEntity.ok(airportService.getAirportByCode(code));
    }

    @PostMapping
    public ResponseEntity<AirportDto> createAirport(@Valid @RequestBody CreateAirportRequest request) {
        return new ResponseEntity<>(airportService.createAirport(request), HttpStatus.CREATED);
    }

    @PutMapping("/{code}")
    public ResponseEntity<AirportDto> updateAirport(@PathVariable String code, @Valid @RequestBody CreateAirportRequest request) {
        return ResponseEntity.ok(airportService.updateAirport(code, request));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteAirport(@PathVariable String code) {
        airportService.deleteAirport(code);
        return ResponseEntity.noContent().build();
    }
}
