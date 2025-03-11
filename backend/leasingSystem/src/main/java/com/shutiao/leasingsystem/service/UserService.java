package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.pojo.dto.addUserDto;
import com.shutiao.leasingsystem.repository.UserRepository;
import com.shutiao.leasingsystem.service.Interface.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User add(addUserDto user) {
        User userpojo = new User();
        BeanUtils.copyProperties(user, userpojo);
        return userRepository.save(userpojo);
    }

    @Override
    public User getById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(()->{
            throw new IllegalArgumentException("用户不存在");
        });
    }
}
