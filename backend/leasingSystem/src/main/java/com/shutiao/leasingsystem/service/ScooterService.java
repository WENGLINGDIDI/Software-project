package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addScooterDto;
import com.shutiao.leasingsystem.pojo.dto.editScooterDto;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.pojo.entity.Station;
import com.shutiao.leasingsystem.repository.ScooterRepository;
import com.shutiao.leasingsystem.repository.StationRepository;
import com.shutiao.leasingsystem.service.Interface.IScooterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScooterService implements IScooterService {
    @Autowired
    private ScooterRepository scooterRepository;
    @Autowired
    private StationRepository stationRepository;

    @Override
    public Scooter addScooter(addScooterDto scooter) {
        Scooter scooterpojo = new Scooter();
        BeanUtils.copyProperties(scooter, scooterpojo);

        Station station = stationRepository.findById(scooter.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found!"));
        scooterpojo.setStation(station);

        return scooterRepository.save(scooterpojo);
    }

    @Override
    public Scooter editScooter(editScooterDto scooter) {
        Scooter existScooter = scooterRepository.findById(scooter.getId())
                .orElseThrow(() -> new RuntimeException("Scooter not found!"));

        existScooter.setLatitude(scooter.getLatitude());
        existScooter.setLongitude(scooter.getLongitude());
        existScooter.setStatus(scooter.getStatus());
        existScooter.setPower(scooter.getPower());
        existScooter.setConfig(scooter.getConfig());

        Station station = stationRepository.findById(scooter.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found!"));
        existScooter.setStation(station);

        return scooterRepository.save(existScooter);
    }

    @Override
    public List<Scooter> getAllScooters() {
        return (List<Scooter>) scooterRepository.findAll();
    }
}