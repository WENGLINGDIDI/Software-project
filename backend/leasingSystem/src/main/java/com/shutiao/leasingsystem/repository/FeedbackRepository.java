package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FeedbackRepository  extends JpaRepository<Feedback, Integer> {

    List<Feedback> findByBookUserId(Integer userId);

    List<Feedback> findByStatus(String status);
}