package com.bike.repository;
import com.bike.entity.BikeOrder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<BikeOrder, Long> {
    
    // JpaRepository 已经自带了：
    // .save(order)     -> 对应数据库的 INSERT/UPDATE
    // .findById(id)    -> 对应 SELECT
    // .findAll()       -> 查询所有订单

    // 自动按 userId 过滤，status 匹配，且按开始时间降序排列
    List<BikeOrder> findByUserIdAndStatusOrderByStartTimeDesc(Long userId, Integer status);
}

