package com.airlinebooking.airline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirlineService {
    private final AirlineRepository airlineRepository;

    public List<AirlineDto> getAllAirlines() {
        return airlineRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AirlineDto getAirlineById(Integer id) {
        return airlineRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Airline not found"));
    }

    public AirlineDto createAirline(CreateAirlineRequest request) {
        if (airlineRepository.existsByAirlineCode(request.getAirlineCode())) {
            throw new RuntimeException("Airline code already exists");
        }
        Airline airline = Airline.builder()
                .airlineCode(request.getAirlineCode())
                .airlineName(request.getAirlineName())
                .logoUrl(request.getLogoUrl())
                .build();
        return mapToDto(airlineRepository.save(airline));
    }

    public AirlineDto updateAirline(Integer id, CreateAirlineRequest request) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline not found"));
                
        if (!airline.getAirlineCode().equals(request.getAirlineCode()) && 
            airlineRepository.existsByAirlineCode(request.getAirlineCode())) {
            throw new RuntimeException("Airline code already exists");
        }

        airline.setAirlineCode(request.getAirlineCode());
        airline.setAirlineName(request.getAirlineName());
        airline.setLogoUrl(request.getLogoUrl());

        return mapToDto(airlineRepository.save(airline));
    }

    public void deleteAirline(Integer id) {
        if (!airlineRepository.existsById(id)) {
            throw new RuntimeException("Airline not found");
        }
        airlineRepository.deleteById(id);
    }

    private AirlineDto mapToDto(Airline airline) {
        return AirlineDto.builder()
                .airlineId(airline.getAirlineId())
                .airlineCode(airline.getAirlineCode())
                .airlineName(airline.getAirlineName())
                .logoUrl(airline.getLogoUrl())
                .build();
    }
}
