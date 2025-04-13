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
//  1 预定， 2 激活， 3 完成
    private Integer status;
//    0 未付， 1 付款
    private Integer payed;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;
    private Integer extend_time = 0; // 新增属性，默认值为 0

    public Integer getExtend_time() {
        return extend_time;
    }

    public void setExtend_time(Integer extend_time) {
        this.extend_time = extend_time;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayed() {
        return payed;
    }

    public void setPayed(Integer payed) {
        this.payed = payed;
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
