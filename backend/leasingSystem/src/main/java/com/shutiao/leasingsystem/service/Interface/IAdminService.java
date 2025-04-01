package com.shutiao.leasingsystem.service.Interface;


import com.shutiao.leasingsystem.pojo.dto.addScooterDto;
import com.shutiao.leasingsystem.pojo.dto.editScooterDto;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.pojo.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IAdminService {


    Scooter addScooter(addScooterDto scooter);

    Scooter editScooter(editScooterDto scooter);

    List<Scooter> getAllScooters();

    Map<Integer, Double> getRevenueForThisWeek();

    List<Map<String, Object>> getAllUsers();
}
