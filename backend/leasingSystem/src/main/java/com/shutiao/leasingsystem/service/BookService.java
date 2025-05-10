package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addBookDto;
import com.shutiao.leasingsystem.pojo.dto.completeBookDto;
import com.shutiao.leasingsystem.pojo.dto.extendBookTimeDto;
import com.shutiao.leasingsystem.pojo.entity.Book;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.pojo.entity.Station;
import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.repository.BookRepository;
import com.shutiao.leasingsystem.repository.HireOptionRepository;
import com.shutiao.leasingsystem.repository.ScooterRepository;
import com.shutiao.leasingsystem.repository.StationRepository;
import com.shutiao.leasingsystem.repository.UserRepository;
import com.shutiao.leasingsystem.service.Interface.IBookService;
import com.shutiao.leasingsystem.service.EmailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookService implements IBookService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ScooterRepository scooterRepository;
    @Autowired
    private HireOptionRepository hireOptionRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public Book addBook(addBookDto bookDto) {
        User user = userRepository.findById(bookDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Scooter scooter = scooterRepository.findById(bookDto.getScooterId())
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
        HireOption hireOption = hireOptionRepository.findById(bookDto.getHireOptionId())
                .orElseThrow(() -> new RuntimeException("HireOption not found"));

        Book book = new Book();
        book.setUser(user);
        book.setScooter(scooter);
        book.setHireOption(hireOption);
        book.setExtend_time(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime startTime = LocalDateTime.parse(bookDto.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(bookDto.getEndTime(), formatter);
        book.setStartTime(startTime);
        book.setEndTime(endTime);

        double totalCost = hireOption.getMoney();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime discountTime = user.getDiscountTime();
        if (discountTime != null && now.isBefore(discountTime)) {
            totalCost = totalCost * user.getDiscount();
        }

        book.setTotalCost(totalCost);
        if (now.isBefore(startTime)) {
            book.setStatus(1);
        } else if (now.isAfter(startTime) && now.isBefore(endTime)) {
            book.setStatus(2);
        } else {
            book.setStatus(3);
        }
        scooter.setStatus(1);
        scooterRepository.save(scooter);

        book.setPayed(0);
        Book savedBook = bookRepository.save(book);
        emailService.sendEmail(user.getEmail(), "confirm", savedBook.toString());

        return savedBook;
    }

    @Override
    public List<Book> getBooksByUserId(Integer userId) {
        return bookRepository.findByUserId(userId);
    }

    @Override
    public Book cancelBook(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("订单未找到"));
        if (book.getStatus() != 1) {
            throw new RuntimeException("只有处于预定阶段的订单才能取消");
        }
        book.setStatus(4);
        Scooter scooter = book.getScooter();
        scooter.setStatus(0);
        scooterRepository.save(scooter);

        return bookRepository.save(book);
    }

    @Override
    public Book extendBookTime(extendBookTimeDto dto) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("订单未找到"));
        book.setExtend_time(book.getExtend_time() + dto.getExtendHours());

        HireOption hireOption = book.getHireOption();
        double totalCost = hireOption.getMoney();
        User user = book.getUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime discountTime = user.getDiscountTime();
        if (discountTime != null && now.isBefore(discountTime)) {
            totalCost = totalCost * user.getDiscount();
        }
        totalCost += book.getExtend_time() * 5;
        book.setTotalCost(totalCost);
        book.setEndTime(book.getEndTime().plusHours(dto.getExtendHours()));

        return bookRepository.save(book);
    }

    @Override
    public Book completeBook(completeBookDto dto) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("订单未找到"));
        Station station = stationRepository.findById(dto.getNewStationId())
                .orElseThrow(() -> new RuntimeException("新站点未找到"));
        book.setStatus(3);
        book.setEndTime(LocalDateTime.now());
        Scooter scooter = book.getScooter();
        scooter.setStatus(0);
        scooter.setStation(station);
        scooterRepository.save(scooter);

        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBook() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("订单未找到"));
    }

    @Override
    public Book payBook(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("订单未找到"));
        book.setPayed(1);
        book.setStatus(3);
        return bookRepository.save(book);
    }

    @Override
    public List<Book> snedEmail() {
        emailService.sendEmail("1550320492@qq.com", "confirm", "book confirmed");
        return null;
    }


    @Scheduled(fixedRate = 600000) // 每分钟执行一次，可以根据需要调整
    public void updateUnpaidBookStatus() {
        List<Book> unpaidBooks = bookRepository.findByPayed(0); // 获取所有未支付的订单
        LocalDateTime now = LocalDateTime.now();

        for (Book book : unpaidBooks) {
            if (now.isBefore(book.getStartTime())) {
                // 预定状态
                book.setStatus(1);
            } else if (now.isAfter(book.getStartTime()) && now.isBefore(book.getEndTime())) {
                // 激活状态
                book.setStatus(2);
            } else {
                // 完成状态
                book.setStatus(3);
            }
            bookRepository.save(book); // 保存更新后的订单状态
        }
    }
}