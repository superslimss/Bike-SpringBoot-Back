package com.bike.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bikes")
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;  // 纬度
    private Double longitude; // 经度

    /**
     * 0: 空闲, 1: 使用中, 2: 故障, 3: 维修中
     */
    private Integer status;

    @Column(name = "bike_no")
    private String bikeNo;    // 单车编号

    // 👇 新增字段（和数据库对应）


    @Column(name = "parking_area_id")
    private Long parkingAreaId;

    public Long getParkingAreaId() {
        return parkingAreaId == null ? 0L : parkingAreaId;
    }

    @Column(name = "fault_desc")
    private String faultDesc;

    @Column(name = "last_dispatch_time")
    private LocalDateTime lastDispatchTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}