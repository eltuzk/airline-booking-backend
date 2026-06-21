package com.airlinebooking.booking.mapper;

import com.airlinebooking.booking.entity.BaggageCatalogEntity;
import com.airlinebooking.booking.payload.response.BaggageResponse;
import org.springframework.stereotype.Component;

@Component
public class BaggageMapper {

    public BaggageResponse toResponse(BaggageCatalogEntity baggageCatalogEntity){


        if(baggageCatalogEntity == null){
            return null;
        }


        BaggageResponse baggageResponse = new BaggageResponse();
        baggageResponse.setBaggageConfigId(baggageCatalogEntity.getBaggageConfigId());
        baggageResponse.setWeightInKg(baggageCatalogEntity.getWeightInKg());
        baggageResponse.setPrice(baggageCatalogEntity.getPrice());

        return baggageResponse;
    }
}
