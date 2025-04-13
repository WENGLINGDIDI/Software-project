package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.addFeedbackDto;
import com.shutiao.leasingsystem.pojo.dto.editFeedbackDto;
import com.shutiao.leasingsystem.pojo.entity.Feedback;

import java.util.List;

public interface IFeedbackService {
    Feedback addFeedback(addFeedbackDto feedbackDto);
    List<Feedback> getFeedbacksByUserId(Integer userId);
    List<Feedback> getFeedbacksByStatus(String status);
    List<Feedback> getAllFeedbacks();
    Feedback updateFeedbackPriority(Integer feedbackId, Integer priority);
    Feedback editFeedback(editFeedbackDto dto);
}