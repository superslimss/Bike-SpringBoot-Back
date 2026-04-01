package com.bike.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_area")
public class ParkingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // MySQL JSON 用 String 承接最省事
    @Column(name = "polygon_json", columnDefinition = "json")
    private String polygonJson;

    @Column(name = "min_lat")
    private Double minLat;

    @Column(name = "max_lat")
    private Double maxLat;

    @Column(name = "min_lng")
    private Double minLng;

    @Column(name = "max_lng")
    private Double maxLng;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ParkingArea() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPolygonJson() {
        return polygonJson;
    }

    public Double getMinLat() {
        return minLat;
    }

    public Double getMaxLat() {
        return maxLat;
    }

    public Double getMinLng() {
        return minLng;
    }

    public Double getMaxLng() {
        return maxLng;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPolygonJson(String polygonJson) {
        this.polygonJson = polygonJson;
    }

    public void setMinLat(Double minLat) {
        this.minLat = minLat;
    }

    public void setMaxLat(Double maxLat) {
        this.maxLat = maxLat;
    }

    public void setMinLng(Double minLng) {
        this.minLng = minLng;
    }

    public void setMaxLng(Double maxLng) {
        this.maxLng = maxLng;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}