package com.shutiao.leasingsystem.service.Interface;


import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IAdminService {
    Map<Integer, Double> getRevenueForThisWeek();
    List<User> getAllUsers();
    User editDiscountDto(editDiscountDto dto);

}
