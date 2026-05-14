package com.bike.task;

import com.bike.repository.BikeSpeedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Component
public class SpeedRecordCleanTask {

    @Autowired
    private BikeSpeedRecordRepository speedRecordRepository;

    /**
     * 每30分钟清理一次，只保留最近30分钟速度记录
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    @Transactional
    public void cleanOldSpeedRecords() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(30);

        speedRecordRepository.deleteByRecordTimeBefore(expireTime);

        System.out.println("Clean old speed records before: " + expireTime);
    }
}