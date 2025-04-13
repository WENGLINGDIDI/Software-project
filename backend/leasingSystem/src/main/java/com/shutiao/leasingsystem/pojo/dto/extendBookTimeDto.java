package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class extendBookTimeDto {
    @NotNull(message = "订单 ID 不能为空")
    private Integer bookId;
    @NotNull(message = "延长的小时数不能为空")
    private Integer extendHours;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getExtendHours() {
        return extendHours;
    }

    public void setExtendHours(Integer extendHours) {
        this.extendHours = extendHours;
    }
}