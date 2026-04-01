package com.bike.repository;

import com.bike.entity.ParkingArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {
}