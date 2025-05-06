package com.shutiao.leasingsystem.controller;


import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.service.Interface.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    IAdminService adminService;


    @GetMapping("/week")
    public ResponseMessage<Map<Integer, Double>> getRevenueForThisWeek() {
        Map<Integer, Double> revenueMap = adminService.getRevenueForThisWeek();
        return ResponseMessage.success(revenueMap);  // 返回结果
    }
    @GetMapping("/year")
    public ResponseMessage<Map<Integer, Map<Integer, Double>>> getRevenueForThisYear() {
        Map<Integer, Map<Integer, Double>> revenueMap = adminService.getRevenueForThisYear();
        return ResponseMessage.success(revenueMap);  // 返回结果
    }
    // 获取所有用户
    @GetMapping("/user")
    public ResponseMessage<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseMessage.success(users);
    }

    @PostMapping("/discount")
    public ResponseMessage<User> updateUserDiscount(@RequestBody editDiscountDto dto) {
        User updatedUser = adminService.editDiscountDto(dto);
        return ResponseMessage.success(updatedUser);
    }
}
