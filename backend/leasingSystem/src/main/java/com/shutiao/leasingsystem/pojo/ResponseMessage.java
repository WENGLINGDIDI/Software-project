package com.shutiao.leasingsystem.pojo;

import org.springframework.http.HttpStatus;

public class ResponseMessage<T> {

    private int statusCode;  // 响应状态码
    private String message;  // 响应消息
    private T data;          // 响应数据，可以是任意类型

    // 默认构造方法
    public ResponseMessage() {
    }

    // 构造方法
    public ResponseMessage(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseMessage(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    // 获取状态码
    public int getStatusCode() {
        return statusCode;
    }

    // 设置状态码
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    // 获取消息
    public String getMessage() {
        return message;
    }

    // 设置消息
    public void setMessage(String message) {
        this.message = message;
    }

    // 获取数据
    public T getData() {
        return data;
    }

    // 设置数据
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

//    返回成功请求
    public static <T> ResponseMessage<T> success(T data){
        return new ResponseMessage(HttpStatus.OK.value(), "success", data);
    }

    public static <T> ResponseMessage<T> error(T data) {
        return new ResponseMessage<>(HttpStatus.BAD_REQUEST.value(), "error", data);
    }
}