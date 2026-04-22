package com.bike.controller;

import com.bike.entity.Bike;
import com.bike.entity.BikeOrder;
import com.bike.entity.ParkingArea;
import com.bike.repository.BikeRepository;
import com.bike.repository.OrderRepository;
import com.bike.repository.ParkingAreaRepository;
import com.bike.util.GeoUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<?> createOrder(@RequestBody BikeOrder order) {
        // 打印一下前端传过来的 userId，方便调试
        System.out.println("收到订单请求，用户ID为: " + order.getUserId());

        if (order.getUserId() == null) {
            return ResponseEntity.badRequest().body("用户ID不能为空");
        }

        order.setStartTime(LocalDateTime.now());
        order.setStatus(0); // 0 代表进行中

        BikeOrder savedOrder = orderRepository.save(order);

        // 同时更新单车状态为使用中
        bikeRepository.findById(order.getBikeId()).ifPresent(bike -> {
            bike.setStatus(1);
            bikeRepository.save(bike);
        });

        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/finish")
    @Transactional
    public ResponseEntity<?> finishOrder(@RequestBody BikeOrder req) {
        return orderRepository.findById(req.getId()).map(order -> {
            // 1. 更新订单信息
            order.setStatus(1);
            LocalDateTime now = LocalDateTime.now();
            order.setEndTime(now);
            order.setEndLat(req.getEndLat());
            order.setEndLng(req.getEndLng());

            // 计算骑行时长
            java.time.Duration duration = java.time.Duration.between(order.getStartTime(), now);
            long seconds = duration.getSeconds();
            String formattedTime = String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
            order.setRideTime(formattedTime);
            orderRepository.save(order);

            // 2. 同步更新单车表状态与地理围栏判定
            bikeRepository.findById(order.getBikeId()).ifPresent(bike -> {
                bike.setLatitude(req.getEndLat());
                bike.setLongitude(req.getEndLng());
                bike.setStatus(0); // 恢复空闲
                bike.setUpdateTime(now);

                List<ParkingArea> areas = parkingAreaRepository.findAll();
                Long currentAreaId = 0L; // 默认值为 0L（违停区域 ID）

                for (ParkingArea area : areas) {
                    try {
                        // 解析 JSON 格式的多边形顶点
                        List<Map<String, Double>> points = objectMapper.readValue(
                                area.getPolygonJson(), new TypeReference<List<Map<String, Double>>>() {
                                });

                        List<double[]> polygon = new ArrayList<>();
                        // 修复 Type mismatch: 确保循环变量类型与 points 定义一致
                        for (Map<String, Double> p : points) {
                            Double latObj = p.get("lat");
                            Double lngObj = p.get("lng");
                            if (latObj != null && lngObj != null) {
                                polygon.add(new double[] { latObj, lngObj });
                            }
                        }

                        // 执行判定
                        if (!polygon.isEmpty() && GeoUtil.pointInPolygon(req.getEndLat(), req.getEndLng(), polygon)) {
                            currentAreaId = area.getId();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                bike.setParkingAreaId(currentAreaId);
                bikeRepository.save(bike);
            });

            return ResponseEntity.ok(order);
        }).orElse(ResponseEntity.notFound().build());
    }
}