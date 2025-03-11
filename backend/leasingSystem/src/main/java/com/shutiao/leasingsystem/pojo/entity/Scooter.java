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


}
