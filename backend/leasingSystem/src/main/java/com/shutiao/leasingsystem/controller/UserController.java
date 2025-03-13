package com.shutiao.leasingsystem.controller;


import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.loginDto;
import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.pojo.dto.addUserDto;
import com.shutiao.leasingsystem.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/{userId}")
    public ResponseMessage<User> get(@PathVariable Integer userId){
        User user = userService.getById(userId);
        return ResponseMessage.success(user);
    }

}
