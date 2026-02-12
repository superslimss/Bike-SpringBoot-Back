package com.bike.controller;

import com.bike.entity.Bike;
import com.bike.repository.BikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@CrossOrigin // 允许跨域，方便小程序调试
public class BikeController {

    @Autowired
    private BikeRepository bikeRepository;

    // 小程序调用：GET http://localhost:8080/api/bikes/list
    @GetMapping("/list")
    public List<Bike> getAllBikes() {
        return bikeRepository.findAll();
    }
}
