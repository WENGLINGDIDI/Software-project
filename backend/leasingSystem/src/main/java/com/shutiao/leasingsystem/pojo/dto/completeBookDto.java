package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class completeBookDto {
    @NotNull(message = "订单 ID 不能为空")
    private Integer bookId;
    @NotNull(message = "新站点 ID 不能为空")
    private Integer newStationId;

    public Integer getNewStationId() {
        return newStationId;
    }

    public void setNewStationId(Integer newStationId) {
        this.newStationId = newStationId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}
