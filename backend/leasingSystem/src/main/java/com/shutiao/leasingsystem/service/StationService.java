package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addStationDto;
import com.shutiao.leasingsystem.pojo.entity.Station;
import com.shutiao.leasingsystem.repository.StationRepository;
import com.shutiao.leasingsystem.service.Interface.IStationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService implements IStationService {
    @Autowired
    private StationRepository stationRepository;

    @Override
    public Station addStation(addStationDto station) {
        Station stationPojo = new Station();
        BeanUtils.copyProperties(station, stationPojo);
        return stationRepository.save(stationPojo);
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public void deleteStation(Integer id) {
        stationRepository.deleteById(id);
    }

    @Override
    public Station getStationById(Integer id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found!"));
    }
}