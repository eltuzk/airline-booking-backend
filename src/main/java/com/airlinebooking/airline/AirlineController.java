package com.airlinebooking.airline;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    public ResponseEntity<List<AirlineDto>> getAllAirlines() {
        return ResponseEntity.ok(airlineService.getAllAirlines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineDto> getAirlineById(@PathVariable Integer id) {
        return ResponseEntity.ok(airlineService.getAirlineById(id));
    }

    @PostMapping
    public ResponseEntity<AirlineDto> createAirline(@Valid @RequestBody CreateAirlineRequest request) {
        return new ResponseEntity<>(airlineService.createAirline(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirlineDto> updateAirline(@PathVariable Integer id, @Valid @RequestBody CreateAirlineRequest request) {
        return ResponseEntity.ok(airlineService.updateAirline(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirline(@PathVariable Integer id) {
        airlineService.deleteAirline(id);
        return ResponseEntity.noContent().build();
    }
}
