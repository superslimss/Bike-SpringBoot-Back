package com.bike.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bikes")
public class Bike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;  // 纬度
    private Double longitude; // 经度
    
    // 0: 空闲, 1: 使用中, 2: 故障
    private Integer status;   
    
    private String bikeNo;    // 单车编号
}
