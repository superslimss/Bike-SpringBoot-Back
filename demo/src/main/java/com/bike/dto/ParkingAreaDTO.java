package com.bike.dto;

import java.util.List;

public class ParkingAreaDTO {
    private Long id;
    private String name;
    private List<LatLngDTO> points;

    public ParkingAreaDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LatLngDTO> getPoints() {
        return points;
    }

    public void setPoints(List<LatLngDTO> points) {
        this.points = points;
    }
}