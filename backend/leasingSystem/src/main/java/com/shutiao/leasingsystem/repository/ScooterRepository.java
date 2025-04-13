package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository  extends JpaRepository<Scooter, Integer> {
    // 根据站点 ID 和状态（0 表示可用）查询空闲电动车
    List<Scooter> findByStationIdAndStatus(Integer stationId, Integer status);
}
