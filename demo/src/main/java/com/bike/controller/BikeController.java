package com.bike.controller;

import com.bike.entity.Bike;
import com.bike.repository.BikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.bike.dto.BikeDispatchDTO;
import com.bike.dto.BikeFaultDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @PutMapping("/admin/dispatch")
    public Map<String, Object> dispatchBike(@RequestBody BikeDispatchDTO dto) {
        Map<String, Object> result = new HashMap<>();

        // 权限校验
        if (!"admin".equals(dto.getRole())) {
            result.put("code", 0);
            result.put("msg", "无权限操作");
            return result;
        }

        Optional<Bike> optionalBike = bikeRepository.findById(dto.getBikeId());
        if (optionalBike.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "单车不存在");
            return result;
        }

        Bike bike = optionalBike.get();

        // 使用中的车不能调度
        if (bike.getStatus() != null && bike.getStatus() == 1) {
            result.put("code", 0);
            result.put("msg", "单车正在使用中，不能调度");
            return result;
        }

        bike.setLatitude(dto.getLatitude());
        bike.setLongitude(dto.getLongitude());
        bike.setParkingAreaId(dto.getParkingAreaId());
        bike.setLastDispatchTime(LocalDateTime.now());

        bikeRepository.save(bike);

        result.put("code", 1);
        result.put("msg", "success");
        return result;
    }

    @PutMapping("/admin/fault")
    public Map<String, Object> handleBikeFault(@RequestBody BikeFaultDTO dto) {
        Map<String, Object> result = new HashMap<>();

        // 权限校验
        if (!"admin".equals(dto.getRole())) {
            result.put("code", 0);
            result.put("msg", "无权限操作");
            return result;
        }

        Optional<Bike> optionalBike = bikeRepository.findById(dto.getBikeId());
        if (optionalBike.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "单车不存在");
            return result;
        }

        Bike bike = optionalBike.get();

        // 状态合法性校验
        if (dto.getStatus() == null || !(dto.getStatus() == 0 || dto.getStatus() == 2 || dto.getStatus() == 3)) {
            result.put("code", 0);
            result.put("msg", "状态值不合法");
            return result;
        }

        // 使用中的车不能改状态
        if (bike.getStatus() != null && bike.getStatus() == 1 && dto.getStatus() != 1) {
            result.put("code", 0);
            result.put("msg", "单车正在使用中，不能修改状态");
            return result;
        }

        bike.setStatus(dto.getStatus());
        bike.setFaultDesc(dto.getFaultDesc());

        bikeRepository.save(bike);

        result.put("code", 1);
        result.put("msg", "处理成功");
        return result;
    }
}
