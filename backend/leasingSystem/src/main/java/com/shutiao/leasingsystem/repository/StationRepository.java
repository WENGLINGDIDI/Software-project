package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
}