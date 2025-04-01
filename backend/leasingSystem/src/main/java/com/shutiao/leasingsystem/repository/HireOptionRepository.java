package com.shutiao.leasingsystem.repository;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HireOptionRepository  extends JpaRepository<HireOption, Integer> {

}
