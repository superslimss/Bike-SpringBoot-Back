package com.bike.dto;

import lombok.Data;
import java.util.List;

@Data
public class BikeBatchDispatchDTO {
    private List<Long> bikeIds;
    private Long parkingAreaId;
    private String role;
}