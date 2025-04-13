package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addHireOptionDto;
import com.shutiao.leasingsystem.pojo.dto.editHireOptionDto;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import com.shutiao.leasingsystem.repository.HireOptionRepository;
import com.shutiao.leasingsystem.service.Interface.IHireOptionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HireOptionService implements IHireOptionService {

    @Autowired
    private HireOptionRepository hireOptionRepository;

    @Override
    public HireOption addHireOption(addHireOptionDto hireOptionDto) {
        HireOption hireOption = new HireOption();
        BeanUtils.copyProperties(hireOptionDto, hireOption);
        return hireOptionRepository.save(hireOption);
    }

    @Override
    public HireOption editHireOption(editHireOptionDto hireOptionDto) {
        HireOption existHireOption = hireOptionRepository.findById(hireOptionDto.getId())
                .orElseThrow(() -> new RuntimeException("HireOption not found!"));
        if (hireOptionDto.getHours() != null) {
            existHireOption.setHours(hireOptionDto.getHours());
        }
        if (hireOptionDto.getMoney() != null) {
            existHireOption.setMoney(hireOptionDto.getMoney());
        }
        return hireOptionRepository.save(existHireOption);
    }

    @Override
    public void deleteHireOption(Integer id) {
        hireOptionRepository.deleteById(id);
    }

    @Override
    public List<HireOption> getAllHireOptions() {
        return hireOptionRepository.findAll();
    }

    @Override
    public HireOption getHireOptionById(Integer id) {
        return hireOptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HireOption not found!"));
    }
}