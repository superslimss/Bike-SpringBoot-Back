package com.bike.repository;
import com.bike.entity.BikeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<BikeOrder, Long> {
    
    // 目前这里空着就行！
    // JpaRepository 已经自带了：
    // .save(order)     -> 对应数据库的 INSERT/UPDATE
    // .findById(id)    -> 对应 SELECT
    // .findAll()       -> 查询所有订单
}

