package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.loginDto;
import com.shutiao.leasingsystem.pojo.entity.Book;
import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.pojo.dto.addUserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    User add(addUserDto user);

    User getById(Integer userId);

    User login(loginDto user);

    List<Book> bookById(Integer userId);
}
