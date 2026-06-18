package com.bike.controller;

import java.util.Optional;
import com.bike.entity.AppUser;
import com.bike.entity.Bike;
import com.bike.entity.BikeOrder;
import com.bike.entity.BikeSpeedRecord;
import com.bike.entity.ParkingArea;
import com.bike.repository.AppUserRepository;
import com.bike.repository.BikeRepository;
import com.bike.repository.BikeSpeedRecordRepository;
import com.bike.repository.OrderRepository;
import com.bike.repository.ParkingAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin", produces = "application/json;charset=UTF-8")
@CrossOrigin
public class AdminController {

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private BikeRepository bikeRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ParkingAreaRepository parkingAreaRepository;

  @Autowired
  private BikeSpeedRecordRepository bikeSpeedRecordRepository;

  @GetMapping("/statistics")
  public Map<String, Object> statistics() {
    Map<String, Object> result = new HashMap<>();

    result.put("userCount", appUserRepository.count());
    result.put("bikeCount", bikeRepository.count());
    result.put("orderCount", orderRepository.count());
    result.put("parkingAreaCount", parkingAreaRepository.count());
    result.put("speedRecordCount", bikeSpeedRecordRepository.count());

    return result;
  }

  @GetMapping("/users")
  public List<AppUser> listUsers() {
    return appUserRepository.findAll();
  }

  @GetMapping("/bikes")
  public List<Bike> listBikes() {
    return bikeRepository.findAll();
  }

  @GetMapping("/orders")
  public List<BikeOrder> listOrders() {
    return orderRepository.findAll();
  }

  @GetMapping("/parking-areas")
  public List<ParkingArea> listParkingAreas() {
    return parkingAreaRepository.findAll();
  }

  @GetMapping("/speed-records")
  public List<BikeSpeedRecord> listSpeedRecords() {
    return bikeSpeedRecordRepository.findAll();
  }

  @DeleteMapping("/orders/{id}")
  public Map<String, Object> deleteOrder(@PathVariable Long id) {
    Map<String, Object> result = new HashMap<>();

    if (!orderRepository.existsById(id)) {
      result.put("code", 0);
      result.put("msg", "订单不存在");
      return result;
    }

    orderRepository.deleteById(id);

    result.put("code", 1);
    result.put("msg", "删除成功");
    return result;
  }

  @PostMapping("/bikes")
  public Map<String, Object> addBike(@RequestBody Bike bike) {
    Map<String, Object> result = new HashMap<>();

    if (bike.getBikeNo() == null || bike.getBikeNo().trim().isEmpty()) {
      result.put("code", 0);
      result.put("msg", "单车编号不能为空");
      return result;
    }

    if (bike.getLatitude() == null || bike.getLongitude() == null) {
      result.put("code", 0);
      result.put("msg", "经纬度不能为空");
      return result;
    }

    if (bike.getStatus() == null) {
      bike.setStatus(0);
    }

    bikeRepository.save(bike);

    result.put("code", 1);
    result.put("msg", "新增成功");
    return result;
  }

  @DeleteMapping("/bikes/{id}")
  public Map<String, Object> deleteBike(@PathVariable Long id) {
    Map<String, Object> result = new HashMap<>();

    if (!bikeRepository.existsById(id)) {
      result.put("code", 0);
      result.put("msg", "单车不存在");
      return result;
    }

    bikeRepository.deleteById(id);

    result.put("code", 1);
    result.put("msg", "删除成功");
    return result;
  }

  @PostMapping("/users")
  public Map<String, Object> addUser(@RequestBody AppUser user) {
    Map<String, Object> result = new HashMap<>();

    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      result.put("code", 0);
      result.put("msg", "用户名不能为空");
      return result;
    }

    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
      result.put("code", 0);
      result.put("msg", "密码不能为空");
      return result;
    }

    if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
      result.put("code", 0);
      result.put("msg", "手机号不能为空");
      return result;
    }

    if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
      result.put("code", 0);
      result.put("msg", "手机号格式不正确");
      return result;
    }

    if (appUserRepository.existsByUsername(user.getUsername())) {
      result.put("code", 0);
      result.put("msg", "用户名已存在");
      return result;
    }

    if (appUserRepository.existsByPhone(user.getPhone())) {
      result.put("code", 0);
      result.put("msg", "手机号已存在");
      return result;
    }

    if (user.getRole() == null || user.getRole().trim().isEmpty()) {
      user.setRole("user");
    }

    user.setCreateTime(java.time.LocalDateTime.now());

    appUserRepository.save(user);

    result.put("code", 1);
    result.put("msg", "新增成功");
    return result;
  }

  @DeleteMapping("/users/{id}")
  public Map<String, Object> deleteUser(@PathVariable Long id) {
    Map<String, Object> result = new HashMap<>();

    if (!appUserRepository.existsById(id)) {
      result.put("code", 0);
      result.put("msg", "用户不存在");
      return result;
    }

    try {
      appUserRepository.deleteById(id);
      result.put("code", 1);
      result.put("msg", "删除成功");
    } catch (Exception e) {
      result.put("code", 0);
      result.put("msg", "该用户存在订单记录，暂时无法删除");
    }

    return result;
  }

  @PostMapping("/parking-areas")
  public Map<String, Object> addParkingArea(@RequestBody ParkingArea parkingArea) {
    Map<String, Object> result = new HashMap<>();

    if (parkingArea.getName() == null || parkingArea.getName().trim().isEmpty()) {
      result.put("code", 0);
      result.put("msg", "停车区名称不能为空");
      return result;
    }

    if (parkingArea.getMinLat() == null || parkingArea.getMaxLat() == null ||
        parkingArea.getMinLng() == null || parkingArea.getMaxLng() == null) {
      result.put("code", 0);
      result.put("msg", "经纬度范围不能为空");
      return result;
    }

    if (parkingArea.getCreatedAt() == null) {
      parkingArea.setCreatedAt(java.time.LocalDateTime.now());
    }

    parkingAreaRepository.save(parkingArea);

    result.put("code", 1);
    result.put("msg", "新增成功");
    return result;
  }

  @DeleteMapping("/parking-areas/{id}")
  public Map<String, Object> deleteParkingArea(@PathVariable Long id) {
    Map<String, Object> result = new HashMap<>();

    if (!parkingAreaRepository.existsById(id)) {
      result.put("code", 0);
      result.put("msg", "停车区域不存在");
      return result;
    }

    try {
      parkingAreaRepository.deleteById(id);
      result.put("code", 1);
      result.put("msg", "删除成功");
    } catch (Exception e) {
      result.put("code", 0);
      result.put("msg", "该停车区域已被单车或订单关联，暂时无法删除");
    }

    return result;
  }
@PutMapping("/bikes/{id}")
public Map<String, Object> updateBike(@PathVariable Long id, @RequestBody Bike bike) {
    Map<String, Object> result = new HashMap<>();

    Optional<Bike> optionalBike = bikeRepository.findById(id);

    if (optionalBike.isEmpty()) {
        result.put("code", 0);
        result.put("msg", "单车不存在");
        return result;
    }

    Bike oldBike = optionalBike.get();

    if (bike.getBikeNo() == null || bike.getBikeNo().trim().isEmpty()) {
        result.put("code", 0);
        result.put("msg", "单车编号不能为空");
        return result;
    }

    if (bike.getLatitude() == null || bike.getLongitude() == null) {
        result.put("code", 0);
        result.put("msg", "经纬度不能为空");
        return result;
    }

    oldBike.setBikeNo(bike.getBikeNo());
    oldBike.setLatitude(bike.getLatitude());
    oldBike.setLongitude(bike.getLongitude());
    oldBike.setStatus(bike.getStatus());
    oldBike.setParkingAreaId(bike.getParkingAreaId());
    oldBike.setFaultDesc(bike.getFaultDesc());
    oldBike.setUpdateTime(java.time.LocalDateTime.now());

    bikeRepository.save(oldBike);

    result.put("code", 1);
    result.put("msg", "修改成功");
    return result;
}

@PutMapping("/users/{id}")
public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody AppUser user) {
    Map<String, Object> result = new HashMap<>();

    Optional<AppUser> optionalUser = appUserRepository.findById(id);

    if (optionalUser.isEmpty()) {
        result.put("code", 0);
        result.put("msg", "用户不存在");
        return result;
    }

    AppUser oldUser = optionalUser.get();

    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
        result.put("code", 0);
        result.put("msg", "用户名不能为空");
        return result;
    }

    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
        result.put("code", 0);
        result.put("msg", "密码不能为空");
        return result;
    }

    if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
        result.put("code", 0);
        result.put("msg", "手机号不能为空");
        return result;
    }

    if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
        result.put("code", 0);
        result.put("msg", "手机号格式不正确");
        return result;
    }

    if (!oldUser.getUsername().equals(user.getUsername())
            && appUserRepository.existsByUsername(user.getUsername())) {
        result.put("code", 0);
        result.put("msg", "用户名已存在");
        return result;
    }

    if (!oldUser.getPhone().equals(user.getPhone())
            && appUserRepository.existsByPhone(user.getPhone())) {
        result.put("code", 0);
        result.put("msg", "手机号已存在");
        return result;
    }

    oldUser.setUsername(user.getUsername());
    oldUser.setPassword(user.getPassword());
    oldUser.setPhone(user.getPhone());
    oldUser.setRole(user.getRole());

    appUserRepository.save(oldUser);

    result.put("code", 1);
    result.put("msg", "修改成功");
    return result;
}

}