package com.bike.dto;

import lombok.Data;

@Data
public class BikeReportFaultDTO {
    private String bikeNo;
    private String faultDesc;
}