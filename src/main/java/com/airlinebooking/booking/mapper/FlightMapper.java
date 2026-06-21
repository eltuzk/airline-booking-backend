package com.airlinebooking.booking.mapper;

import com.airlinebooking.booking.entity.FlightEntity;
import com.airlinebooking.booking.payload.response.FlightSearchResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    // 1. Chuyển đổi 1 Entity thành 1 Response
    public FlightSearchResponse toSearchResponse(FlightEntity entity) {
        if (entity == null) {
            return null;
        }

        FlightSearchResponse response = new FlightSearchResponse();
        response.setFlightId(entity.getFlightId());
        response.setFlightNumber(entity.getFlightNumber());
        response.setDepartureAirportCode(entity.getDepartureAirportCode());
        response.setArrivalAirportCode(entity.getArrivalAirportCode());
        response.setDepartureTime(entity.getDepartureTime());
        response.setArrivalTime(entity.getArrivalTime());
        response.setBasePrice(entity.getBasePrice());
        response.setAvailableSeats(entity.getAvailableSeats());

        return response;
    }

    // 2. Chuyển đổi 1 List Entity thành 1 List Response (Dùng Stream API cho "Pro")
    public List<FlightSearchResponse> toSearchResponseList(List<FlightEntity> flightEntityList) {
        if (flightEntityList == null || flightEntityList.isEmpty()) {
            return new ArrayList<>();
        }

        List<FlightSearchResponse> flightSearchResponseList = new ArrayList<>();

        for(FlightEntity f : flightEntityList){
            FlightSearchResponse flightSearchResponse = toSearchResponse(f);
            flightSearchResponseList.add(flightSearchResponse);

        }

        return flightSearchResponseList;
    }
}