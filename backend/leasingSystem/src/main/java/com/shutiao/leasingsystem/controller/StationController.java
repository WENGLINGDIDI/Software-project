package com.shutiao.leasingsystem.controller;

import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.addStationDto;
import com.shutiao.leasingsystem.pojo.dto.editStationDto;
import com.shutiao.leasingsystem.pojo.entity.Station;
import com.shutiao.leasingsystem.service.Interface.IStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class StationController {
    @Autowired
    private IStationService stationService;

    @PostMapping("/station/add")
    public ResponseMessage<Station> addStation(@RequestBody addStationDto station) {
        Station newStation = stationService.addStation(station);
        return ResponseMessage.success(newStation);
    }

    @GetMapping("/station")
    public ResponseMessage<List<Station>> getAllStations() {
        List<Station> stations = stationService.getAllStations();
        return ResponseMessage.success(stations);
    }

    @GetMapping("/stationAndScooter")
    public ResponseMessage<List<Map<String, Object>>> getAllStationsWithScooters() {
        List<Map<String, Object>> stationsWithScooters = stationService.getAllStationsWithScooters();
        return ResponseMessage.success(stationsWithScooters);
    }
    @PostMapping("/station/edit")
    public ResponseMessage<Station> editStation(@RequestBody editStationDto stationDto) {
        Station updatedStation = stationService.editStation(stationDto);
        return ResponseMessage.success(updatedStation);
    }

    @DeleteMapping("/station/delete/{id}")
    public ResponseMessage<Station> deleteStation(@PathVariable Integer id) {
        Station deletedStation = stationService.deleteStation(id);
        return ResponseMessage.success(deletedStation);
    }
}