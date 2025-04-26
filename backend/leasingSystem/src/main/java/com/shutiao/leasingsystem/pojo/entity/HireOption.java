package com.shutiao.leasingsystem.pojo.entity;

import jakarta.persistence.*;

@Table(name="HireOption")
@Entity
public class HireOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer hours;
    private Integer money;

    public Integer getId() {return id;}

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

    @Override
    public String toString() {
        return "HireOption{" +
                "id=" + id +
                ", hours=" + hours +
                ", money=" + money +
                '}';
    }
}
