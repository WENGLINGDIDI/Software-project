package com.shutiao.leasingsystem.repository;

import com.shutiao.leasingsystem.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
