package com.bike.controller;

import com.bike.dto.SpeedReportDTO;
import com.bike.entity.BikeSpeedRecord;
import com.bike.repository.BikeSpeedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/congestion")
@CrossOrigin
public class CongestionController {

    @Autowired
    private BikeSpeedRecordRepository speedRecordRepository;

    /**
     * 用户骑行时上报当前速度
     */
    @PostMapping("/reportSpeed")
    public Map<String, Object> reportSpeed(@RequestBody SpeedReportDTO dto) {
        Map<String, Object> result = new HashMap<>();

        if (dto.getEdgeKey() == null || dto.getEdgeKey().trim().isEmpty()) {
            result.put("code", 0);
            result.put("msg", "路段不能为空");
            return result;
        }

        if (dto.getSpeedKmh() == null || dto.getSpeedKmh() <= 0) {
            result.put("code", 0);
            result.put("msg", "速度数据不合法");
            return result;
        }

        BikeSpeedRecord record = new BikeSpeedRecord();
        record.setBikeId(dto.getBikeId());
        record.setUserId(dto.getUserId());
        record.setEdgeKey(dto.getEdgeKey());
        record.setSpeedKmh(dto.getSpeedKmh());
        record.setRecordTime(LocalDateTime.now());

        speedRecordRepository.save(record);

        result.put("code", 1);
        result.put("msg", "速度上报成功");
        return result;
    }

    /**
     * 获取最近5分钟动态拥堵表
     */
    @GetMapping("/dynamicMap")
    public Map<String, Object> getDynamicMap() {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<BikeSpeedRecord> records = speedRecordRepository.findByRecordTimeAfter(fiveMinutesAgo);

        Map<String, List<BikeSpeedRecord>> groupMap = records.stream()
                .filter(r -> r.getEdgeKey() != null)
                .collect(Collectors.groupingBy(BikeSpeedRecord::getEdgeKey));

        Map<String, Object> dynamicMap = new HashMap<>();

        for (String edgeKey : groupMap.keySet()) {
            List<BikeSpeedRecord> list = groupMap.get(edgeKey);

            double avgSpeed = list.stream()
                    .mapToDouble(BikeSpeedRecord::getSpeedKmh)
                    .average()
                    .orElse(0);

            String level = "normal";

            if (avgSpeed > 0 && avgSpeed < 6) {
                level = "high";
            } else if (avgSpeed >= 6 && avgSpeed < 10) {
                level = "medium";
            }

            Map<String, Object> item = new HashMap<>();
            item.put("avgSpeed", round2(avgSpeed));
            item.put("level", level);

            dynamicMap.put(edgeKey, item);
        }

        result.put("code", 1);
        result.put("data", dynamicMap);
        return result;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}