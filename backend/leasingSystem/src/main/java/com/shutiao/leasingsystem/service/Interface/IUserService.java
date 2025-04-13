package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    User add(addUserDto user);

    User getById(Integer userId);

    User login(loginDto user);

    User addUserCard(addUserCardDto dto);

}
