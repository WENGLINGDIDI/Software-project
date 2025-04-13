package com.shutiao.leasingsystem.repository;
import com.shutiao.leasingsystem.pojo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookRepository  extends JpaRepository<Book, Integer> {

    List<Book> findByStartTimeBetween(LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    List<Book> findByUserId(Integer userId);

    List<Book> findByPayed(int i);
}