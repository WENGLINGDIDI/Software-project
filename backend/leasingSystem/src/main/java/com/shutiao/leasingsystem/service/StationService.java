package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addStationDto;
import com.shutiao.leasingsystem.pojo.dto.editStationDto;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.pojo.entity.Station;
import com.shutiao.leasingsystem.repository.ScooterRepository;
import com.shutiao.leasingsystem.repository.StationRepository;
import com.shutiao.leasingsystem.service.Interface.IStationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StationService implements IStationService {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private ScooterRepository scooterRepository;

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
    public Station getStationById(Integer id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found!"));
    }

    @Override
    public List<Map<String, Object>> getAllStationsWithScooters() {
        List<Station> stations = stationRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Station station : stations) {
            List<Scooter> scooters = scooterRepository.findByStationId(station.getId());
            Map<String, Object> stationMap = new HashMap<>();
            stationMap.put("station", station);
            stationMap.put("scooters", scooters);
            result.add(stationMap);
        }

        return result;
    }

    @Override
    public Station editStation(editStationDto stationDto) {
        Station existingStation = stationRepository.findById(stationDto.getId())
                .orElseThrow(() -> new RuntimeException("Station not found!"));
        BeanUtils.copyProperties(stationDto, existingStation, "id"); // 避免覆盖 ID
        return stationRepository.save(existingStation);
    }

    @Override
    public Station deleteStation(Integer id) {
        Station stationToDelete = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found!"));
        stationRepository.deleteById(id);
        return stationToDelete;
    }

}