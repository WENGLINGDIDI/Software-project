package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addFeedbackDto;
import com.shutiao.leasingsystem.pojo.dto.editFeedbackDto;
import com.shutiao.leasingsystem.pojo.entity.Feedback;
import com.shutiao.leasingsystem.repository.BookRepository;
import com.shutiao.leasingsystem.repository.FeedbackRepository;
import com.shutiao.leasingsystem.service.Interface.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Feedback addFeedback(addFeedbackDto feedbackDto) {
        var book = bookRepository.findById(feedbackDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        var feedback = new Feedback();
        feedback.setBook(book);
        feedback.setMessage(feedbackDto.getMessage());
        feedback.setPriority(1);
        feedback.setStatus("未处理");
        feedback.setCreateTime(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getFeedbacksByUserId(Integer userId) {
        return feedbackRepository.findByBookUserId(userId);
    }

    @Override
    public List<Feedback> getFeedbacksByStatus(String status) {
        return feedbackRepository.findByStatus(status);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback updateFeedbackPriority(Integer feedbackId, Integer priority) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setPriority(priority);
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback editFeedback(editFeedbackDto dto) {
        Feedback feedback = feedbackRepository.findById(dto.getFeedbackId())
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setResult(dto.getResult());
        feedback.setStatus("已处理");
        return feedbackRepository.save(feedback);
    }
}