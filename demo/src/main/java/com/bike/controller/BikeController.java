package com.bike.controller;

import com.bike.entity.Bike;
import com.bike.repository.BikeRepository;
import com.bike.repository.ParkingAreaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.bike.dto.BikeDispatchDTO;
import com.bike.dto.BikeFaultDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bike.entity.ParkingArea;
import com.bike.repository.ParkingAreaRepository;
import com.bike.dto.BikeBatchDispatchDTO;
import com.bike.dto.BikeReportFaultDTO;

import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@CrossOrigin // 允许跨域，方便小程序调试
public class BikeController {

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

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

    @PutMapping("/admin/batchDispatch")
    public Map<String, Object> batchDispatchBikes(@RequestBody BikeBatchDispatchDTO dto) {
        Map<String, Object> result = new HashMap<>();

        if (!"admin".equals(dto.getRole())) {
            result.put("code", 0);
            result.put("msg", "无权限操作");
            return result;
        }

        if (dto.getBikeIds() == null || dto.getBikeIds().isEmpty()) {
            result.put("code", 0);
            result.put("msg", "请选择要调度的单车");
            return result;
        }

        Optional<ParkingArea> optionalArea = parkingAreaRepository.findById(dto.getParkingAreaId());
        if (optionalArea.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "目标停车区不存在");
            return result;
        }

        ParkingArea area = optionalArea.get();

        if (area.getMinLat() == null || area.getMaxLat() == null ||
                area.getMinLng() == null || area.getMaxLng() == null) {
            result.put("code", 0);
            result.put("msg", "停车区范围数据不完整");
            return result;
        }

        List<Bike> bikes = bikeRepository.findAllById(dto.getBikeIds());

        int successCount = 0;

        for (Bike bike : bikes) {
            // 使用中的车不调度
            if (bike.getStatus() != null && bike.getStatus() == 1) {
                continue;
            }

            double lat = area.getMinLat() + Math.random() * (area.getMaxLat() - area.getMinLat());
            double lng = area.getMinLng() + Math.random() * (area.getMaxLng() - area.getMinLng());

            bike.setLatitude(round6(lat));
            bike.setLongitude(round6(lng));
            bike.setParkingAreaId(area.getId());
            bike.setLastDispatchTime(LocalDateTime.now());

            // 调度后设为空闲
            bike.setStatus(0);

            bikeRepository.save(bike);
            successCount++;
        }

        result.put("code", 1);
        result.put("msg", "批量调度成功");
        result.put("successCount", successCount);
        return result;
    }

    @PutMapping("/reportFault")
    public Map<String, Object> reportBikeFault(@RequestBody BikeReportFaultDTO dto) {
        Map<String, Object> result = new HashMap<>();

        if (dto.getBikeNo() == null || dto.getBikeNo().trim().isEmpty()) {
            result.put("code", 0);
            result.put("msg", "请输入单车编号");
            return result;
        }

        Optional<Bike> optionalBike = bikeRepository.findByBikeNo(dto.getBikeNo());

        if (optionalBike.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "单车不存在");
            return result;
        }

        Bike bike = optionalBike.get();

        if (bike.getStatus() != null && bike.getStatus() == 1) {
            result.put("code", 0);
            result.put("msg", "单车正在使用中，暂不能上报故障");
            return result;
        }

        bike.setStatus(2);
        bike.setFaultDesc(dto.getFaultDesc());
        bike.setUpdateTime(LocalDateTime.now());

        bikeRepository.save(bike);

        result.put("code", 1);
        result.put("msg", "故障上报成功");
        return result;
    }

    private double round6(double value) {
        return Math.round(value * 1000000.0) / 1000000.0;
    }
}
