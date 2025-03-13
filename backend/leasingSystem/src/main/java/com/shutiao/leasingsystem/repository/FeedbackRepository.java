package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.Feedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FeedbackRepository  extends CrudRepository<Feedback, Integer> {

}