package com.shutiao.leasingsystem.pojo.entity;

import jakarta.persistence.*;


@Table(name="Scooter")
@Entity
public class Scooter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double latitude;  // 纬度
    private Double longitude; // 经度

    private Integer status;  // 状态，例如 0=可用, 1=不可用, 2=维修中
    private Integer power;
    private String config;   // 额外配置（如速度等）
    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "Scooter{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                ", power=" + power +
                ", config='" + config + '\'' +
                ", station=" + station +
                '}';
    }
}
