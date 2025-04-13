package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.repository.*;
import com.shutiao.leasingsystem.service.Interface.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    ScooterRepository scooterRepository;
    @Autowired
    HireOptionRepository hireOptionRepository;
    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    StationRepository stationRepository;

    @Override
    public User add(addUserDto user) {
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 复制 DTO 数据到 User 实体
        User userpojo = new User();
        BeanUtils.copyProperties(user, userpojo);

        // 保存到数据库
        return userRepository.save(userpojo);
    }

    @Override
    public User getById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(()->{
            throw new IllegalArgumentException("用户不存在");
        });
    }

    @Override
    public User login(loginDto user) {
        // 1. 根据邮箱查找用户
        User exist_user = userRepository.findByEmail(user.getEmail());
        if (exist_user == null || !exist_user.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("邮箱或密码错误");
        }
        if (!exist_user.getRole().equals(user.getRole())){
            throw new IllegalArgumentException("身份验证错误");
        }

        return exist_user;
    }

    @Override
    public User addUserCard(addUserCardDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        user.setCard(dto.getCard());
        return userRepository.save(user);
    }

}
