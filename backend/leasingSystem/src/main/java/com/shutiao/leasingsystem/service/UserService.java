package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.repository.*;
import com.shutiao.leasingsystem.service.Interface.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Override
    public User add(addUserDto user) {
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 复制 DTO 数据到 User 实体
        User userpojo = new User();
        BeanUtils.copyProperties(user, userpojo);

        // 对密码进行加密
        userpojo.setPassword(passwordEncoder.encode(user.getPassword()));

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
        if (exist_user == null) {
            logger.info("用户不存在，邮箱: {}", user.getEmail());
            throw new IllegalArgumentException("邮箱或密码错误");
        }

        // 打印登录时加密后的密码
        String encryptedLoginPassword = passwordEncoder.encode(user.getPassword());
        logger.info("登录时加密后的密码: {}", encryptedLoginPassword);
        logger.info("数据库中的加密密码: {}", exist_user.getPassword());

        if (!passwordEncoder.matches(user.getPassword(), exist_user.getPassword())) {
            logger.info("密码不匹配，邮箱: {}", user.getEmail());
            throw new IllegalArgumentException("邮箱或密码错误");
        }
        if (!exist_user.getRole().equals(user.getRole())) {
            logger.info("身份验证错误，邮箱: {}", user.getEmail());
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
