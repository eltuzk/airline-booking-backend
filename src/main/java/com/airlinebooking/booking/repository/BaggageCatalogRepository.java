package com.airlinebooking.booking.repository;

import com.airlinebooking.booking.entity.BaggageCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaggageCatalogRepository extends JpaRepository<BaggageCatalogEntity, Integer> {

    //đã có hàm finAll()
}
