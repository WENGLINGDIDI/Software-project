package com.shutiao.leasingsystem.controller;

import com.shutiao.leasingsystem.pojo.ResponseMessage;
import com.shutiao.leasingsystem.pojo.dto.addBookDto;
import com.shutiao.leasingsystem.pojo.dto.completeBookDto;
import com.shutiao.leasingsystem.pojo.dto.extendBookTimeDto;
import com.shutiao.leasingsystem.pojo.entity.Book;
import com.shutiao.leasingsystem.service.Interface.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private IBookService bookService;

    // 新增订单
    @PostMapping("/user/book/add")
    public ResponseMessage<Book> addBook(@RequestBody addBookDto bookDto) {
        Book newBook = bookService.addBook(bookDto);
        return ResponseMessage.success(newBook);
    }

    // 获取订单 by 用户 id
    @GetMapping("/user/book/{userId}")
    public ResponseMessage<List<Book>> getBooksByUserId(@PathVariable Integer userId) {
        List<Book> books = bookService.getBooksByUserId(userId);
        return ResponseMessage.success(books);
    }
    @GetMapping("/user/book/id/{bookId}")
    public ResponseMessage<Book> getBookById(@PathVariable Integer bookId) {
        Book book = bookService.getBookById(bookId);
        return ResponseMessage.success(book);
    }
    // 取消订单
    @DeleteMapping("/user/book/cancel/{bookId}")
    public ResponseMessage<Book> cancelBook(@PathVariable Integer bookId) {
        Book cancelledBook = bookService.cancelBook(bookId);
        return ResponseMessage.success(cancelledBook);
    }

    // 延长订单
    @PostMapping("/user/book/extend")
    public ResponseMessage<Book> extendBookTime(@RequestBody extendBookTimeDto dto) {
        Book extendedBook = bookService.extendBookTime(dto);
        return ResponseMessage.success(extendedBook);
    }

    // 完成订单
    @PostMapping("/user/book/complete")
    public ResponseMessage<Book> completeBook(@RequestBody completeBookDto dto) {
        Book completedBook = bookService.completeBook(dto);
        return ResponseMessage.success(completedBook);
    }

    // 支付订单
    @PostMapping("/user/book/pay/{bookId}")
    public ResponseMessage<Book> payBook(@PathVariable Integer bookId) {
        Book payedBook = bookService.payBook(bookId);
        return ResponseMessage.success(payedBook);
    }

    // 延长订单
    @GetMapping("/admin/book/all")
    public ResponseMessage<List<Book>> getAllBook() {
        List<Book> allBooks = bookService.getAllBook();
        return ResponseMessage.success(allBooks);
    }
}