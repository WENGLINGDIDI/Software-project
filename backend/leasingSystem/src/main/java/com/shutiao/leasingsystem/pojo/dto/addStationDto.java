package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class addStationDto {
    @NotNull
    private String name;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}