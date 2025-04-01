package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScooterRepository  extends JpaRepository<Scooter, Integer> {

}
