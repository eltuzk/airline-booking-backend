package com.airlinebooking.flight;

import com.airlinebooking.airline.Aircraft;
import com.airlinebooking.airline.AircraftRepository;
import com.airlinebooking.airline.Airline;
import com.airlinebooking.airline.AirlineRepository;
import com.airlinebooking.airport.Airport;
import com.airlinebooking.airport.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;
    private final AirportRepository airportRepository;

    public List<FlightDto> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public FlightDto getFlightById(Integer id) {
        return flightRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    public List<FlightDto> searchFlights(FlightSearchRequest request) {
        LocalDateTime startOfDay = request.getDepartureDate() != null ? request.getDepartureDate().atStartOfDay() : null;
        LocalDateTime endOfDay = request.getDepartureDate() != null ? request.getDepartureDate().plusDays(1).atStartOfDay() : null;

        return flightRepository.searchFlights(
                request.getDepartureAirportCode(),
                request.getArrivalAirportCode(),
                startOfDay,
                endOfDay
        ).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public FlightDto createFlight(CreateFlightRequest request) {
        validateFlightRequest(request);

        if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new RuntimeException("Flight number already exists");
        }

        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Airline not found"));
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));
        Airport departureAirport = airportRepository.findById(request.getDepartureAirportCode())
                .orElseThrow(() -> new RuntimeException("Departure airport not found"));
        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportCode())
                .orElseThrow(() -> new RuntimeException("Arrival airport not found"));

        if (!aircraft.getAirline().getAirlineId().equals(airline.getAirlineId())) {
            throw new RuntimeException("Aircraft does not belong to the selected airline");
        }

        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airline(airline)
                .aircraft(aircraft)
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .basePrice(request.getBasePrice())
                .status(request.getStatus() != null ? request.getStatus() : FlightStatus.Scheduled)
                .build();

        return mapToDto(flightRepository.save(flight));
    }

    public FlightDto updateFlight(Integer id, CreateFlightRequest request) {
        validateFlightRequest(request);

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (!flight.getFlightNumber().equals(request.getFlightNumber()) &&
            flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new RuntimeException("Flight number already exists");
        }

        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Airline not found"));
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));
        Airport departureAirport = airportRepository.findById(request.getDepartureAirportCode())
                .orElseThrow(() -> new RuntimeException("Departure airport not found"));
        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportCode())
                .orElseThrow(() -> new RuntimeException("Arrival airport not found"));

        if (!aircraft.getAirline().getAirlineId().equals(airline.getAirlineId())) {
            throw new RuntimeException("Aircraft does not belong to the selected airline");
        }

        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirline(airline);
        flight.setAircraft(aircraft);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setBasePrice(request.getBasePrice());
        if (request.getStatus() != null) {
            flight.setStatus(request.getStatus());
        }

        return mapToDto(flightRepository.save(flight));
    }

    public void deleteFlight(Integer id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Flight not found");
        }
        flightRepository.deleteById(id);
    }

    private void validateFlightRequest(CreateFlightRequest request) {
        if (request.getDepartureAirportCode().equals(request.getArrivalAirportCode())) {
            throw new RuntimeException("Departure and arrival airports cannot be the same");
        }
        if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            throw new RuntimeException("Arrival time cannot be before departure time");
        }
    }

    private FlightDto mapToDto(Flight flight) {
        return FlightDto.builder()
                .flightId(flight.getFlightId())
                .flightNumber(flight.getFlightNumber())
                .airlineId(flight.getAirline().getAirlineId())
                .airlineName(flight.getAirline().getAirlineName())
                .aircraftId(flight.getAircraft().getAircraftId())
                .aircraftModel(flight.getAircraft().getModel())
                .departureAirportCode(flight.getDepartureAirport().getAirportCode())
                .arrivalAirportCode(flight.getArrivalAirport().getAirportCode())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .basePrice(flight.getBasePrice())
                .status(flight.getStatus())
                .build();
    }
}
