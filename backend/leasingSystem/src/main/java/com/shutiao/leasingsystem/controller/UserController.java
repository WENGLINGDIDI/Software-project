package com.shutiao.leasingsystem.controller;


import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;

    //注册
    @PostMapping
    public ResponseMessage<User> add(@Validated @RequestBody addUserDto user){
        User new_user = userService.add(user);
        return ResponseMessage.success(new_user);
    }

    //登录
    @PostMapping("/login")
    public ResponseMessage<User> login(@RequestBody loginDto user){
        User exist_user = userService.login(user);
        return ResponseMessage.success(exist_user);
    }

    //获取用户byId
    @GetMapping("/{userId}")
    public ResponseMessage<User> getById(@PathVariable Integer userId){
        User user = userService.getById(userId);
        return ResponseMessage.success(user);
    }

    @PostMapping("/card")
    public ResponseMessage<User> setUserCard(@RequestBody addUserCardDto dto) {
        User updatedUser = userService.addUserCard(dto);
        return ResponseMessage.success(updatedUser);
    }
}
