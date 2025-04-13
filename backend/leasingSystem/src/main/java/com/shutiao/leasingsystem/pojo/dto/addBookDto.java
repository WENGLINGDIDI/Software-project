package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class addBookDto {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer scooterId;
    @NotNull
    private Integer hireOptionId;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScooterId() {
        return scooterId;
    }

    public void setScooterId(Integer scooterId) {
        this.scooterId = scooterId;
    }

    public Integer getHireOptionId() {
        return hireOptionId;
    }

    public void setHireOptionId(Integer hireOptionId) {
        this.hireOptionId = hireOptionId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "addBookDto{" +
                "userId=" + userId +
                ", scooterId=" + scooterId +
                ", hireOptionId=" + hireOptionId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
