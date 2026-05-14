package com.bike.repository;

import com.bike.entity.BikeSpeedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BikeSpeedRecordRepository extends JpaRepository<BikeSpeedRecord, Long> {

    List<BikeSpeedRecord> findByRecordTimeAfter(LocalDateTime time);

    void deleteByRecordTimeBefore(LocalDateTime time);
}