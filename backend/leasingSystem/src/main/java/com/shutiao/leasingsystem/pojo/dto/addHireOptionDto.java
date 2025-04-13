// addHireOptionDto.java
package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class addHireOptionDto {
    @NotNull(message = "租赁时长不能为空")
    private Integer hours;
    @NotNull(message = "费用不能为空")
    private Integer money;

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