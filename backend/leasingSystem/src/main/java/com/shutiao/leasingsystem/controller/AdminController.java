package com.shutiao.leasingsystem.controller;


import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.addScooterDto;
import com.shutiao.leasingsystem.pojo.dto.editScooterDto;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.service.Interface.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    IAdminService adminService;

    //  创建车
    @PostMapping("/scooter/add")
    public ResponseMessage<Scooter> addScooter(@RequestBody addScooterDto scooter){
        Scooter new_scooter = adminService.addScooter(scooter);
        return ResponseMessage.success(new_scooter);
    }

    @PostMapping("/scooter/edit")
    public ResponseMessage<Scooter> editScooter(@RequestBody editScooterDto scooter){
        Scooter new_scooter = adminService.editScooter(scooter);
        return ResponseMessage.success(new_scooter);
    }

    @GetMapping("/scooter")
    public ResponseMessage<List<Scooter>> getAllScooter(){
        List<Scooter> scooters = adminService.getAllScooters();
        return ResponseMessage.success(scooters);
    }

    @GetMapping("/week")
    public ResponseMessage<Map<Integer, Double>> getRevenueForThisWeek() {
        Map<Integer, Double> revenueMap = adminService.getRevenueForThisWeek();
        return ResponseMessage.success(revenueMap);  // 返回结果
    }

    // 获取所有用户
    @GetMapping("/user")
    public ResponseMessage<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = adminService.getAllUsers();
        return ResponseMessage.success(users);
    }

}
