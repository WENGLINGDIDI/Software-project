package com.shutiao.leasingsystem.controller;


import com.shutiao.leasingsystem.service.Interface.IAdminService;
import com.shutiao.leasingsystem.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    IAdminService adminService;



}
