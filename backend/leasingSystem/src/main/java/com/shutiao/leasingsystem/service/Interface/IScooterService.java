package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.addScooterDto;
import com.shutiao.leasingsystem.pojo.dto.editScooterDto;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import java.util.List;

public interface IScooterService {
    Scooter addScooter(addScooterDto scooter);
    Scooter editScooter(editScooterDto scooter);
    List<Scooter> getAllScooters();

    Scooter deleteScooter(Integer id);
}