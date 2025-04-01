package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.addScooterDto;
import com.shutiao.leasingsystem.pojo.dto.editScooterDto;
import com.shutiao.leasingsystem.pojo.entity.Book;
import com.shutiao.leasingsystem.pojo.entity.HireOption;
import com.shutiao.leasingsystem.pojo.entity.Scooter;
import com.shutiao.leasingsystem.pojo.entity.User;
import com.shutiao.leasingsystem.repository.BookRepository;
import com.shutiao.leasingsystem.repository.HireOptionRepository;
import com.shutiao.leasingsystem.repository.ScooterRepository;
import com.shutiao.leasingsystem.repository.UserRepository;
import com.shutiao.leasingsystem.service.Interface.IAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminService implements IAdminService {
    @Autowired
    ScooterRepository scooterRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private HireOptionRepository hireOptionRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Scooter addScooter(addScooterDto scooter) {
        // 复制 DTO 数据到 scooter 实体
        Scooter scooterpojo = new Scooter();
        BeanUtils.copyProperties(scooter, scooterpojo);
        return scooterRepository.save(scooterpojo);
    }

    @Override
    public Scooter editScooter(editScooterDto scooter) {
        // 1. 查询电动车，找不到就抛异常
        Scooter existScooter = scooterRepository.findById(scooter.getId())
                .orElseThrow(() -> new RuntimeException("Scooter not found!"));

        // 2. 更新字段（确保不覆盖非空字段）
        existScooter.setLatitude(scooter.getLatitude());
        existScooter.setLongitude(scooter.getLongitude());
        existScooter.setStatus(scooter.getStatus());
        existScooter.setPower(scooter.getPower());
        existScooter.setConfig(scooter.getConfig());  // 可能为空，没关系

        // 3. 保存更新后的实体
        return scooterRepository.save(existScooter);
    }

//  获取全部电动车
    @Override
    public List<Scooter> getAllScooters() {
        return (List<Scooter>) scooterRepository.findAll();
    }

    @Override
    public Map<Integer, Double> getRevenueForThisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).truncatedTo(ChronoUnit.DAYS).plusDays(1).minusNanos(1);
        List<Book> bookingsThisWeek = bookRepository.findByStartTimeBetween(startOfWeek, endOfWeek);

        Map<Integer, Double> revenueMap = new HashMap<>();
        List<HireOption> allHireOptions = hireOptionRepository.findAll();
        for (HireOption hireOption : allHireOptions) {
            revenueMap.put(hireOption.getHours(), 0.0);  // 初始化每个租赁选项的营业额为 0
        }

        // 遍历所有订单，根据租赁选项累加营业额
        for (Book booking : bookingsThisWeek) {
            System.out.println(booking);
            Integer hireOption = booking.getHireOption().getHours();
            Double totalCost = booking.getTotalCost();

            // 如果租赁选项已存在，则累加营业额
            revenueMap.put(hireOption, revenueMap.getOrDefault(hireOption, 0.0) + totalCost);
        }

        return revenueMap;
    }

    @Override
    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (User user : users) {
            // 只处理 role 为 "user" 的用户
            if ("user".equals(user.getRole())) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("discount", user.getDiscount());
                responseList.add(userMap);
            }
        }
        return responseList;
    }


    //获取本周营业额

}
