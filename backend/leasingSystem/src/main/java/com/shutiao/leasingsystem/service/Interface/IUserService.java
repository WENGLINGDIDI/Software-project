package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.loginDto;
import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.pojo.dto.addUserDto;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User add(addUserDto user);

    User getById(Integer userId);

    User login(loginDto user);
}
