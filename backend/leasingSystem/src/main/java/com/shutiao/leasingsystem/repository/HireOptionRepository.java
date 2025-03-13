package com.shutiao.leasingsystem.repository;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HireOptionRepository  extends CrudRepository<HireOption, Integer> {

}
