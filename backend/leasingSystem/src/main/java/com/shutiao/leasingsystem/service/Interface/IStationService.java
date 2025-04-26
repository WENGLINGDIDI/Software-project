package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.addStationDto;
import com.shutiao.leasingsystem.pojo.dto.editStationDto;
import com.shutiao.leasingsystem.pojo.entity.Station;
import java.util.List;
import java.util.Map;

public interface IStationService {
    Station addStation(addStationDto station);
    List<Station> getAllStations();
    Station getStationById(Integer id);

    List<Map<String, Object>> getAllStationsWithScooters();

    Station editStation(editStationDto stationDto);

    Station deleteStation(Integer id);
}