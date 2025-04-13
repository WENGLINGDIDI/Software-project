package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.addHireOptionDto;
import com.shutiao.leasingsystem.pojo.dto.editHireOptionDto;
import com.shutiao.leasingsystem.pojo.entity.HireOption;

import java.util.List;

public interface IHireOptionService {
    HireOption addHireOption(addHireOptionDto hireOptionDto);
    HireOption editHireOption(editHireOptionDto hireOptionDto);
    void deleteHireOption(Integer id);
    List<HireOption> getAllHireOptions();
    HireOption getHireOptionById(Integer id);
}