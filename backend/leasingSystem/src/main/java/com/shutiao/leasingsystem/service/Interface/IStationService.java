package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.addStationDto;
import com.shutiao.leasingsystem.pojo.entity.Station;
import java.util.List;

public interface IStationService {
    Station addStation(addStationDto station);
    List<Station> getAllStations();
    void deleteStation(Integer id);
    Station getStationById(Integer id);
}