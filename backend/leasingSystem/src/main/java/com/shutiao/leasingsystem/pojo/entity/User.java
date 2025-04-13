package com.shutiao.leasingsystem.pojo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Table(name="User")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String card;
    private String role = "user";
    private LocalDateTime createTime = LocalDateTime.now();
    private double discount = 1.0;
    private LocalDateTime discountTime; // 新增属性

    public LocalDateTime getDiscountTime() {
        return discountTime;
    }

    public void setDiscountTime(LocalDateTime discountTime) {
        this.discountTime = discountTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getRole() {return role;}

    public void setRole(String role) {this.role = role;}
}
