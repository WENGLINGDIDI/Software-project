package com.shutiao.leasingsystem.pojo.entity;


import jakarta.persistence.*;

@Table(name="Book")
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;   // 预订用户 ID
    @ManyToOne
    @JoinColumn(name = "scooter_id", nullable = false)
    private Scooter scooterId; // 预订的滑板车 ID
    @ManyToOne
    @JoinColumn(name = "hire_id", nullable = false)  // 外键 hire_id 关联 Hire 表
    private HireOption hireOption;
    private Double totalCost;
    private String status;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;

}
