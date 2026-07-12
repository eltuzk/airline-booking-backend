package com.airlinebooking.seat;

import com.airlinebooking.flight.Flight;
import com.airlinebooking.flight.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;

    public List<SeatDto> getSeatsByFlightId(Integer flightId) {
        return seatRepository.findByFlightFlightId(flightId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<SeatDto> generateSeatsForFlight(Integer flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (seatRepository.existsByFlightFlightId(flightId)) {
            throw new RuntimeException("Seats already generated for this flight");
        }

        int totalSeats = flight.getAircraft().getTotalSeats();
        List<Seat> newSeats = new ArrayList<>();

        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        int seatsPerRow = cols.length;
        int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        int currentSeatCount = 0;
        
        // Let's assume first 3 rows are Business class (if totalRows >= 3)
        int businessClassRows = Math.min(3, totalRows);

        for (int row = 1; row <= totalRows; row++) {
            for (char col : cols) {
                if (currentSeatCount >= totalSeats) {
                    break;
                }

                String seatNumber = row + String.valueOf(col);
                SeatClass seatClass = row <= businessClassRows ? SeatClass.Business : SeatClass.Economy;
                SeatType seatType = getSeatType(col);
                BigDecimal priceMultiplier = seatClass == SeatClass.Business ? BigDecimal.valueOf(2.5) : BigDecimal.ONE;

                Seat seat = Seat.builder()
                        .flight(flight)
                        .seatNumber(seatNumber)
                        .seatClass(seatClass)
                        .seatType(seatType)
                        .priceMultiplier(priceMultiplier)
                        .isEmpty(true)
                        .build();

                newSeats.add(seat);
                currentSeatCount++;
            }
        }

        return seatRepository.saveAll(newSeats).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SeatType getSeatType(char col) {
        if (col == 'A' || col == 'F') {
            return SeatType.Window;
        } else if (col == 'C' || col == 'D') {
            return SeatType.Aisle;
        } else {
            return SeatType.Middle;
        }
    }

    private SeatDto mapToDto(Seat seat) {
        return SeatDto.builder()
                .seatId(seat.getSeatId())
                .flightId(seat.getFlight().getFlightId())
                .seatNumber(seat.getSeatNumber())
                .seatClass(seat.getSeatClass())
                .seatType(seat.getSeatType())
                .priceMultiplier(seat.getPriceMultiplier())
                .isEmpty(seat.getIsEmpty())
                .build();
    }
}
