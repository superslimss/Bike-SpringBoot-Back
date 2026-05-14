package com.bike.dto;

import lombok.Data;

@Data
public class SpeedReportDTO {
    private Long bikeId;
    private Long userId;
    private String edgeKey;
    private Double speedKmh;
}