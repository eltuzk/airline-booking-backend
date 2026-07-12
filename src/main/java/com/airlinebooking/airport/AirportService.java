package com.airlinebooking.airport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;

    public List<AirportDto> getAllAirports() {
        return airportRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AirportDto getAirportByCode(String code) {
        return airportRepository.findById(code)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Airport not found"));
    }

    public AirportDto createAirport(CreateAirportRequest request) {
        if (airportRepository.existsById(request.getAirportCode())) {
            throw new RuntimeException("Airport code already exists");
        }

        Airport airport = Airport.builder()
                .airportCode(request.getAirportCode())
                .airportName(request.getAirportName())
                .city(request.getCity())
                .countryId(request.getCountryId())
                .timezone(request.getTimezone())
                .build();

        return mapToDto(airportRepository.save(airport));
    }

    public AirportDto updateAirport(String code, CreateAirportRequest request) {
        Airport airport = airportRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Airport not found"));

        if (!airport.getAirportCode().equals(request.getAirportCode()) &&
            airportRepository.existsById(request.getAirportCode())) {
            throw new RuntimeException("Airport code already exists");
        }

        // Technically we can't change PK easily without creating a new entity, 
        // but since this is PUT /api/airports/{code}, we update the fields of the existing code.
        // We will ignore changing the AirportCode itself in this simple implementation to avoid PK issues.
        
        airport.setAirportName(request.getAirportName());
        airport.setCity(request.getCity());
        airport.setCountryId(request.getCountryId());
        airport.setTimezone(request.getTimezone());

        return mapToDto(airportRepository.save(airport));
    }

    public void deleteAirport(String code) {
        if (!airportRepository.existsById(code)) {
            throw new RuntimeException("Airport not found");
        }
        airportRepository.deleteById(code);
    }

    private AirportDto mapToDto(Airport airport) {
        return AirportDto.builder()
                .airportCode(airport.getAirportCode())
                .airportName(airport.getAirportName())
                .city(airport.getCity())
                .countryId(airport.getCountryId())
                .timezone(airport.getTimezone())
                .build();
    }
}
