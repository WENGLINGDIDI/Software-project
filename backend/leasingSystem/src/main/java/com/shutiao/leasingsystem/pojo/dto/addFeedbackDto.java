package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;

public class addFeedbackDto {
    @NotNull
    private Integer bookId;
    @NotNull
    private String message;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}