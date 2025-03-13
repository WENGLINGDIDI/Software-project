package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Scooter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScooterRepository  extends CrudRepository<Scooter, Integer> {

}
