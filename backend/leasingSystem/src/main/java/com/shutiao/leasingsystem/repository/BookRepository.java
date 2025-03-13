package com.shutiao.leasingsystem.repository;
import com.shutiao.leasingsystem.pojo.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository  extends CrudRepository<Book, Integer> {

}