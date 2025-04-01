package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FeedbackRepository  extends JpaRepository<Feedback, Integer> {

}