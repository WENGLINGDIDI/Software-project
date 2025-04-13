package com.shutiao.leasingsystem.controller;

import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.addFeedbackDto;
import com.shutiao.leasingsystem.pojo.dto.editFeedbackDto;
import com.shutiao.leasingsystem.pojo.entity.Feedback;
import com.shutiao.leasingsystem.service.Interface.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping("/user/feedback/add")
    public ResponseMessage<Feedback> addFeedback(@Validated @RequestBody addFeedbackDto feedbackDto) {
        Feedback newFeedback = feedbackService.addFeedback(feedbackDto);
        return ResponseMessage.success(newFeedback);
    }

    @GetMapping("/user/feedback/{userId}")
    public ResponseMessage<List<Feedback>> getFeedbacksByUserId(@PathVariable Integer userId) {
        List<Feedback> feedbacks = feedbackService.getFeedbacksByUserId(userId);
        return ResponseMessage.success(feedbacks);
    }

    @GetMapping("/admin/feedback/{status}")
    public ResponseMessage<List<Feedback>> getFeedbacksByStatus(@PathVariable String status) {
        List<Feedback> feedbacks = feedbackService.getFeedbacksByStatus(status);
        return ResponseMessage.success(feedbacks);
    }

    @GetMapping("/admin/feedback/all")
    public ResponseMessage<List<Feedback>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseMessage.success(feedbacks);
    }

    @PutMapping("/admin/editPriority/{feedbackId}/{priority}")
    public ResponseMessage<Feedback> updateFeedbackPriority(@PathVariable Integer feedbackId, @PathVariable Integer priority) {
        Feedback updatedFeedback = feedbackService.updateFeedbackPriority(feedbackId, priority);
        return ResponseMessage.success(updatedFeedback);
    }

    @PostMapping("/admin/feedback/edit")
    public ResponseMessage<Feedback> editFeedback(@Validated @RequestBody editFeedbackDto dto) {
        Feedback handledFeedback = feedbackService.editFeedback(dto);
        return ResponseMessage.success(handledFeedback);
    }
}