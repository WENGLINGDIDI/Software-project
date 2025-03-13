package com.shutiao.leasingsystem.pojo.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name="Book")
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // 预订用户 ID
    @ManyToOne
    @JoinColumn(name = "scooter_id", nullable = false)
    private Scooter scooter; // 预订的滑板车 ID
    @ManyToOne
    @JoinColumn(name = "hire_id", nullable = false)  // 外键 hire_id 关联 Hire 表
    private HireOption hireOption;
    private Double totalCost;
    private String status;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public HireOption getHireOption() {
        return hireOption;
    }

    public void setHireOption(HireOption hireOption) {
        this.hireOption = hireOption;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
