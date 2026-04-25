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
            // --- 1. 使用数组来绕过 Lambda 的 final 限制 ---
            final Long[] result = { 0L };

            List<ParkingArea> areas = parkingAreaRepository.findAll();
            for (ParkingArea area : areas) {
                try {
                    List<Map<String, Double>> points = objectMapper.readValue(
                            area.getPolygonJson(), new TypeReference<List<Map<String, Double>>>() {
                            });
                    List<double[]> polygon = new ArrayList<>();
                    for (Map<String, Double> p : points) {
                        Double latObj = p.get("lat");
                        Double lngObj = p.get("lng");
                        if (latObj != null && lngObj != null) {
                            polygon.add(new double[] { latObj, lngObj });
                        }
                    }
                    if (!polygon.isEmpty() && GeoUtil.pointInPolygon(req.getEndLat(), req.getEndLng(), polygon)) {
                        result[0] = area.getId();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // --- 2. 核心计费逻辑 (阶梯计费) ---
            LocalDateTime now = LocalDateTime.now();
            java.time.Duration duration = java.time.Duration.between(order.getStartTime(), now);
            long seconds = duration.getSeconds();
            
            double fee = 0.0;
            // 规则：前30秒免费，15分钟内2元，超过15分钟5元
            if (seconds > 30) {
                if (seconds <= 900) { // 15分钟 = 900秒
                    fee = 2.0;
                } else {
                    fee = 5.0;
                }
            }
            
            // 违停规则：如果不在停车区 (result[0] == 0)，额外收10元
            if (result[0] == 0L) {
                fee += 10.0;
            }

            // --- 3. 更新订单信息 ---
            order.setStatus(1);
            order.setEndTime(now);
            order.setEndLat(req.getEndLat());
            order.setEndLng(req.getEndLng());
            order.setParkingAreaId(result[0]); 
            order.setFee(fee); // 【新增】存入计算好的费用

            String formattedTime = String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
            order.setRideTime(formattedTime);
            orderRepository.save(order);

            // --- 4. 更新单车信息 ---
            bikeRepository.findById(order.getBikeId()).ifPresent(bike -> {
                bike.setLatitude(req.getEndLat());
                bike.setLongitude(req.getEndLng());
                bike.setStatus(0);
                bike.setUpdateTime(now);
                bike.setParkingAreaId(result[0]);
                bikeRepository.save(bike);
            });

            return ResponseEntity.ok(order);
        }).orElse(ResponseEntity.notFound().build());
    }
    /**
     * 1. 查询指定用户的所有已完成订单
     * 用于“历史订单”列表页
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BikeOrder>> getUserOrders(@PathVariable Long userId) {
        // status 为 1 代表已完成，按时间倒序排列（最新的在前面）
        List<BikeOrder> orders = orderRepository.findByUserIdAndStatusOrderByStartTimeDesc(userId, 1);
        return ResponseEntity.ok(orders);
    }

    /**
     * 2. 查询单条订单详情
     * 用于“订单详情”地图轨迹页
     */
    @GetMapping("/{id}")
    public ResponseEntity<BikeOrder> getOrderDetail(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}