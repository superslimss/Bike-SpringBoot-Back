package com.bike.repository;

import com.bike.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    // 基础功能已经由 JpaRepository 提供
}
