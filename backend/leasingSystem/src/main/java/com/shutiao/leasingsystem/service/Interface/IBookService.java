package com.shutiao.leasingsystem.service.Interface;

import com.shutiao.leasingsystem.pojo.dto.addBookDto;
import com.shutiao.leasingsystem.pojo.dto.completeBookDto;
import com.shutiao.leasingsystem.pojo.dto.extendBookTimeDto;
import com.shutiao.leasingsystem.pojo.entity.Book;

import java.util.List;

public interface IBookService {
    Book addBook(addBookDto bookDto);
    List<Book> getBooksByUserId(Integer userId);
    Book cancelBook(Integer bookId);
    Book extendBookTime(extendBookTimeDto dto);
    Book completeBook(completeBookDto dto);

    List<Book> getAllBook();
}