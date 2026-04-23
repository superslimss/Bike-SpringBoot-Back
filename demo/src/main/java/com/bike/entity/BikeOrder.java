package com.bike.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bike_order")
public class BikeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private Long bikeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // ? 检查这里！必须有这一行
    private Double endLat;  // ? 对应前端传的 endLat
    private Double endLng;  // ? 对应前端传的 endLng
    private String rideTime; // ? 存储 "00:05:20" 这种格式
    private Integer status; 
    private Long parkingAreaId; // 记录还车时的区域ID
    private Double startLat;
    private Double startLng;
}