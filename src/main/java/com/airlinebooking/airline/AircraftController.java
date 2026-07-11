package com.airlinebooking.airline;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircrafts")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @GetMapping
    public ResponseEntity<List<AircraftDto>> getAllAircrafts() {
        return ResponseEntity.ok(aircraftService.getAllAircrafts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftDto> getAircraftById(@PathVariable Integer id) {
        return ResponseEntity.ok(aircraftService.getAircraftById(id));
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<AircraftDto>> getAircraftsByAirline(@PathVariable Integer airlineId) {
        return ResponseEntity.ok(aircraftService.getAircraftsByAirline(airlineId));
    }

    @PostMapping
    public ResponseEntity<AircraftDto> createAircraft(@Valid @RequestBody CreateAircraftRequest request) {
        return new ResponseEntity<>(aircraftService.createAircraft(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftDto> updateAircraft(@PathVariable Integer id, @Valid @RequestBody CreateAircraftRequest request) {
        return ResponseEntity.ok(aircraftService.updateAircraft(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Integer id) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }
}
