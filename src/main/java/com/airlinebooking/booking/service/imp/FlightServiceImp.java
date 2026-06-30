package com.airlinebooking.booking.service.imp;

import com.airlinebooking.booking.entity.FlightEntity;
import com.airlinebooking.booking.exceptions.AppException;
import com.airlinebooking.booking.exceptions.ErrorCode;
import com.airlinebooking.booking.mapper.FlightMapper;
import com.airlinebooking.booking.payload.request.FlightSearchRequest;
import com.airlinebooking.booking.payload.response.FlightSearchResponse;
import com.airlinebooking.booking.repository.FlightRepository;
import com.airlinebooking.booking.service.FlightService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class FlightServiceImp implements FlightService {

    private static final String NEED_SEARCH = "SCHEDULED";

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightMapper flightMapper;

    @Override
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest flightSearchRequest) {
        // kiểm tra trẻ sơ sinh có được đi không

        if (flightSearchRequest.getAdults() < 1) {
            throw new AppException(ErrorCode.INVALID_PASSENGER_RULE);
        }

        if (flightSearchRequest.getInfants() > flightSearchRequest.getAdults()) {
            throw new AppException(ErrorCode.ONE_ADULT_ONLY_ONE_INFANT);
        }

        // tính ghế cần là bao nhiêu
        Integer countSeats = flightSearchRequest.getAdults() + flightSearchRequest.getChildren();


        // tìm danh sách
        List<FlightEntity> flightEntityList = flightRepository.searchAvailableFlights(
                flightSearchRequest.getDepartureCode(),
                flightSearchRequest.getArrivalCode(),
                flightSearchRequest.getDate().atStartOfDay(),
                flightSearchRequest.getDate().atTime(LocalTime.MAX),
                countSeats,
                NEED_SEARCH);

        // mappper sang
        List<FlightSearchResponse> flightSearchResponseList = flightMapper.toSearchResponseList(flightEntityList);





        return flightSearchResponseList;
    }
}
