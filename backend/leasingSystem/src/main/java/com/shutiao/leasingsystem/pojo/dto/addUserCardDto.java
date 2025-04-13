package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class addUserCardDto {
    @NotNull(message = "用户 ID 不能为空")
    private Integer id;
    @NotNull(message = "银行卡信息不能为空")
    private String card;

    public Integer getId() {
        return id;
    }

    public void setId(Integer userId) {
        this.id = userId;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
