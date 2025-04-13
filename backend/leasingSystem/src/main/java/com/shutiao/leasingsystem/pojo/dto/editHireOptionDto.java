// editHireOptionDto.java
package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class editHireOptionDto {
    @NotNull(message = "ID 不能为空")
    private Integer id;
    private Integer hours;
    private Integer money;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}