package com.shutiao.leasingsystem.controller;

import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.addScooterDto;
import com.shutiao.leasingsystem.pojo.dto.editScooterDto;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.service.Interface.IScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class ScooterController {
    @Autowired
    private IScooterService scooterService;

    @PostMapping("/scooter/add")
    public ResponseMessage<Scooter> addScooter(@RequestBody addScooterDto scooter) {
        Scooter new_scooter = scooterService.addScooter(scooter);
        return ResponseMessage.success(new_scooter);
    }

    @PostMapping("/scooter/edit")
    public ResponseMessage<Scooter> editScooter(@RequestBody editScooterDto scooter) {
        Scooter new_scooter = scooterService.editScooter(scooter);
        return ResponseMessage.success(new_scooter);
    }

    @GetMapping("/scooter")
    public ResponseMessage<List<Scooter>> getAllScooter() {
        List<Scooter> scooters = scooterService.getAllScooters();
        return ResponseMessage.success(scooters);
    }
}