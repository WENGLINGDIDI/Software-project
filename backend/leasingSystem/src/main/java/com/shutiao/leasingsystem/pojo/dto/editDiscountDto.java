package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class editDiscountDto {
    @NotNull(message = "用户 ID 不能为空")
    private Integer userId;
    @NotNull(message = "折扣不能为空")
    private Double discount;
    @NotNull(message = "折扣截止时间不能为空")
    private Integer discountHours;

    public Integer getDiscountHours() {
        return discountHours;
    }

    public void setDiscountHours(Integer discountHours) {
        this.discountHours = discountHours;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
