package com.bike.controller;

import com.bike.entity.BikeOrder;
import com.bike.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

  @Autowired
  private OrderRepository orderRepository;

  @PostMapping("/create")
  public ResponseEntity<?> createOrder(@RequestBody BikeOrder order) {
    order.setStartTime(LocalDateTime.now());
    order.setStatus(0);
    BikeOrder savedOrder = orderRepository.save(order);
    return ResponseEntity.ok(savedOrder);
  }

  @PostMapping("/finish")
  public ResponseEntity<?> finishOrder(@RequestBody BikeOrder req) {
    return orderRepository.findById(req.getId()).map(order -> {
      // 1. 更新基本状态
      order.setStatus(1);
      LocalDateTime now = LocalDateTime.now();
      order.setEndTime(now);

      // 2. 接收前端传来的还车位置
      order.setEndLat(req.getEndLat());
      order.setEndLng(req.getEndLng());

      // 3. 计算时长并格式化为 00:00:00
      java.time.Duration duration = java.time.Duration.between(order.getStartTime(), now);
      long seconds = duration.getSeconds();
      long h = seconds / 3600;
      long m = (seconds % 3600) / 60;
      long s = seconds % 60;
      String formattedTime = String.format("%02d:%02d:%02d", h, m, s);

      order.setRideTime(formattedTime); // 存入新增的字段

      return ResponseEntity.ok(orderRepository.save(order));
    }).orElse(ResponseEntity.notFound().build());
  }
} // ? 确保类结尾有一个收尾的大括号