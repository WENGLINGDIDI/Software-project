package com.shutiao.leasingsystem.pojo.entity;
import jakarta.persistence.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name="Feedback")
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 关联 Book（外键 book_id）
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book bookId;
    private Integer priority;
    private String message;  // 反馈内容
    private String status;
}
