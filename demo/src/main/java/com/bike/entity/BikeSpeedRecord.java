package com.bike.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bike_speed_record")
public class BikeSpeedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bike_id")
    private Long bikeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "edge_key")
    private String edgeKey;

    @Column(name = "speed_kmh")
    private Double speedKmh;

    @Column(name = "record_time")
    private LocalDateTime recordTime;
}