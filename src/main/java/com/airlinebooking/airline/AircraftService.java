package com.airlinebooking.airline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AirlineRepository airlineRepository;

    public List<AircraftDto> getAllAircrafts() {
        return aircraftRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AircraftDto getAircraftById(Integer id) {
        return aircraftRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));
    }

    public List<AircraftDto> getAircraftsByAirline(Integer airlineId) {
        return aircraftRepository.findByAirlineAirlineId(airlineId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AircraftDto createAircraft(CreateAircraftRequest request) {
        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().isEmpty() &&
            aircraftRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Airline not found"));

        Aircraft aircraft = Aircraft.builder()
                .airline(airline)
                .model(request.getModel())
                .registrationNumber(request.getRegistrationNumber())
                .totalSeats(request.getTotalSeats())
                .build();

        return mapToDto(aircraftRepository.save(aircraft));
    }

    public AircraftDto updateAircraft(Integer id, CreateAircraftRequest request) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));

        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().isEmpty() &&
            !request.getRegistrationNumber().equals(aircraft.getRegistrationNumber()) &&
            aircraftRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Airline not found"));

        aircraft.setAirline(airline);
        aircraft.setModel(request.getModel());
        aircraft.setRegistrationNumber(request.getRegistrationNumber());
        aircraft.setTotalSeats(request.getTotalSeats());

        return mapToDto(aircraftRepository.save(aircraft));
    }

    public void deleteAircraft(Integer id) {
        if (!aircraftRepository.existsById(id)) {
            throw new RuntimeException("Aircraft not found");
        }
        aircraftRepository.deleteById(id);
    }

    private AircraftDto mapToDto(Aircraft aircraft) {
        return AircraftDto.builder()
                .aircraftId(aircraft.getAircraftId())
                .airlineId(aircraft.getAirline().getAirlineId())
                .airlineName(aircraft.getAirline().getAirlineName())
                .model(aircraft.getModel())
                .registrationNumber(aircraft.getRegistrationNumber())
                .totalSeats(aircraft.getTotalSeats())
                .build();
    }
}
