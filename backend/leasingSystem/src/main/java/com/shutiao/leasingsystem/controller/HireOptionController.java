package com.shutiao.leasingsystem.controller;

import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.addHireOptionDto;
import com.shutiao.leasingsystem.pojo.dto.editHireOptionDto;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import com.shutiao.leasingsystem.service.Interface.IHireOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class HireOptionController {

    @Autowired
    private IHireOptionService hireOptionService;

    @PostMapping("/hireOption/add")
    public ResponseMessage<HireOption> addHireOption(@Validated @RequestBody addHireOptionDto hireOptionDto) {
        HireOption newHireOption = hireOptionService.addHireOption(hireOptionDto);
        return ResponseMessage.success(newHireOption);
    }

    @PostMapping("/hireOption/edit")
    public ResponseMessage<HireOption> editHireOption(@Validated @RequestBody editHireOptionDto hireOptionDto) {
        HireOption updatedHireOption = hireOptionService.editHireOption(hireOptionDto);
        return ResponseMessage.success(updatedHireOption);
    }

    @DeleteMapping("/hireOption/{id}")
    public ResponseMessage<String> deleteHireOption(@PathVariable Integer id) {
        hireOptionService.deleteHireOption(id);
        return ResponseMessage.success("HireOption deleted successfully");
    }

    @GetMapping("/hireOption")
    public ResponseMessage<List<HireOption>> getAllHireOptions() {
        List<HireOption> hireOptions = hireOptionService.getAllHireOptions();
        return ResponseMessage.success(hireOptions);
    }

    @GetMapping("/hireOption/{id}")
    public ResponseMessage<HireOption> getHireOptionById(@PathVariable Integer id) {
        HireOption hireOption = hireOptionService.getHireOptionById(id);
        return ResponseMessage.success(hireOption);
    }
}