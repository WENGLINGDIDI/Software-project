package com.shutiao.leasingsystem.service;

import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.repository.*;
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

    //获取本周营业额
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
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<User> nonAdminUsers = new ArrayList<>();

        for (User user : allUsers) {
            if (!"admin".equals(user.getRole())) {
                nonAdminUsers.add(user);
            }
        }
        return nonAdminUsers;
    }

    @Override
    public User editDiscountDto(editDiscountDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        user.setDiscount(dto.getDiscount());
        user.setDiscountTime(LocalDateTime.now().plusHours(dto.getDiscountHours()));
        return userRepository.save(user);
    }


    @Override
    public Map<Integer, Map<Integer, Double>> getRevenueForThisYear() {
        // 获取所有租赁选项
        List<HireOption> allHireOptions = hireOptionRepository.findAll();
        // 获取当前月份
        int currentMonth = LocalDateTime.now().getMonthValue();
        // 存储每个月每个租赁选项的销售额
        Map<Integer, Map<Integer, Double>> revenueMap = new HashMap<>();

        // 初始化每个月的租赁选项销售额
        for (int month = 1; month <= currentMonth; month++) {
            Map<Integer, Double> monthlyRevenueMap = new HashMap<>();
            for (HireOption hireOption : allHireOptions) {
                monthlyRevenueMap.put(hireOption.getHours(), 0.0);
            }
            revenueMap.put(month, monthlyRevenueMap);
        }

        // 获取本周的销售额
        Map<Integer, Double> thisWeekRevenue = getRevenueForThisWeek();

        // 将本周的销售额累加到当前月份的销售额中
        Map<Integer, Double> currentMonthRevenue = revenueMap.get(currentMonth);
        for (Map.Entry<Integer, Double> entry : thisWeekRevenue.entrySet()) {
            Integer hireOptionHours = entry.getKey();
            Double revenue = entry.getValue();
            currentMonthRevenue.put(hireOptionHours, currentMonthRevenue.getOrDefault(hireOptionHours, 0.0) + revenue);
        }

        // 生成除当前月外其他月份每个租赁选项的销售额
        for (int month = 1; month < currentMonth; month++) {
            Map<Integer, Double> monthlyRevenueMap = revenueMap.get(month);
            for (HireOption hireOption : allHireOptions) {
                // 这里简单生成一个随机销售额，实际应用中需要从数据库计算
                double revenue = Math.random() * 200 + 100; // 生成 100 到 300 之间的随机数
                monthlyRevenueMap.put(hireOption.getHours(), revenue);
            }
        }

        return revenueMap;
    }

}
