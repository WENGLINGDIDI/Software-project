package com.shutiao.leasingsystem;

import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.repository.*;
import com.shutiao.leasingsystem.service.EmailService;
import com.shutiao.leasingsystem.pojo.dto.*;
import com.shutiao.leasingsystem.service.Interface.IBookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EntityTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScooterRepository scooterRepository;
    @Autowired
    private HireOptionRepository hireOptionRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private IBookService bookService;

    @Test
    public void generateTestUsers() {
        // 生成 20 个用户
        for (int i = 1; i <= 20; i++) {
            User user = new User();
            user.setName("name" + i);  // name1, name2, name3, ...
            user.setEmail("email" + i + "@example.com");  // email1@example.com, email2@example.com, ...
            user.setPassword("password" + i);  // password1, password2, password3, ...
            user.setRole("user");
            user.setDiscount(1.0);  // 默认折扣为 1.0
            userRepository.save(user);  // 保存用户
        }

        // 验证生成的用户数量
        List<User> allUsers = (List<User>) userRepository.findAll();
        assertEquals(20, allUsers.size());

        // 验证用户信息的正确性
        allUsers.forEach(user -> {
            assert user.getEmail().startsWith("email");
            assert user.getPassword().startsWith("password");
            assert user.getName().startsWith("name");
        });
    }

    @Test
    public void generateScooters() {
        Random random = new Random();

        double centerLat = 30.657; // 成都天府广场纬度
        double centerLng = 104.065; // 成都天府广场经度
        double radius = 0.05; // 设定一个小范围半径（大约2公里内）

        // 获取所有站点
        List<Station> stations = stationRepository.findAll();

        for (int i = 1; i <= 10; i++) {
            Scooter scooter = new Scooter();
            scooter.setLatitude(centerLat + (random.nextDouble() * radius - radius / 2));
            scooter.setLongitude(centerLng + (random.nextDouble() * radius - radius / 2));
            scooter.setStatus(0); // 0=可用, 1=不可用, 2=维修中
            scooter.setPower(random.nextInt(101)); // 0~100 随机电量
            scooter.setConfig("Speed: 30 km/h, Includes helmet and phone holder"); // 速度 5~30km/h

            // 随机选择一个站点
            if (!stations.isEmpty()) {
                int randomIndex = random.nextInt(stations.size());
                Station randomStation = stations.get(randomIndex);
                scooter.setStation(randomStation);
            }

            scooterRepository.save(scooter);
        }
    }


    @Test
    public void generateHireOption() {
        Integer[] hours = {1, 4, 24, 168};
        Integer[] money = {10, 30, 100, 200};
        for (int i = 0; i < 4; i++){
            HireOption hireOption = new HireOption();
            hireOption.setHours(hours[i]);
            hireOption.setMoney(money[i]);
            hireOptionRepository.save(hireOption);
        }
    }


    @Test
    public void generateTestBookings() {
        Integer[] user_ids = {1,2,3,4,5};
        Integer[] scooter_ids = {1,2,3,4};
        Integer[] hire_ids = {1, 3, 5, 6};

        for (int i = 0; i < 4; i++){
            final int index = i;  // 将 i 赋值给一个 final 变量
            User user = userRepository.findById(user_ids[index])
                    .orElseThrow(() -> new RuntimeException("User not found: " + user_ids[index]));
            Scooter scooter = scooterRepository.findById(scooter_ids[index])
                    .orElseThrow(() -> new RuntimeException("Scooter not found: " + scooter_ids[index]));
            HireOption hireOption = hireOptionRepository.findById(hire_ids[index])
                    .orElseThrow(() -> new RuntimeException("HireOption not found: " + hire_ids[index]));

            // 创建 Book 实例，并设置相关字段
            Book book = new Book();
            book.setUser(user);               // 假设 Book 中的字段已命名为 user 而非 userId
            book.setScooter(scooter);         // 同样，Scooter 字段命名为 scooter
            book.setHireOption(hireOption);   // 租用选项

            // 设置租用起始和结束时间，这里假设起始时间为当前时间加上 i 天
            LocalDateTime startTime = LocalDateTime.now().plusDays(i);
            LocalDateTime endTime = startTime.plusHours(hireOption.getHours());
            book.setStartTime(startTime);
            book.setEndTime(endTime);

            // 计算总费用，考虑用户折扣
            double totalCost = hireOption.getMoney() * user.getDiscount();
            book.setTotalCost(totalCost);
            // 设置订单状态，示例中统一设置为 "active"
            book.setStatus(1);

            // 保存订单
            bookRepository.save(book);
        }
    }

    @Test
    public void generateTestFeedback() {
        // 从 Book 表中获取一条记录（这里假设ID为1的记录存在）
        Book book = bookRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Book not found with id: 1"));

        // 创建一个新的反馈记录
        Feedback feedback = new Feedback();
        feedback.setBook(book);              // 设置关联的预订记录
        feedback.setPriority(1);             // 设置反馈优先级
        feedback.setMessage("这是一个测试反馈，系统运行正常。");
        feedback.setStatus("未处理");           // 设置反馈状态（例如 new、resolved 等）

        // 保存反馈记录到数据库
        feedbackRepository.save(feedback);
    }

    @Test
    public void timeTest(){
        LocalDateTime now = LocalDateTime.now();
        // 定义日期时间的格式化模式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        // 格式化日期时间并打印
        String formattedDateTime = now.format(formatter);
        System.out.println(now);
        System.out.println("当前日期时间: " + formattedDateTime);
    }

    @Test
    public void emailTest1(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("1550320492@qq.com");
        message.setSubject("test");
        message.setText("hello");
        message.setFrom("1550320492@qq.com");
        javaMailSender.send(message);
        System.out.println("发送邮件");
    }

    @Test
    public void emailTest2(){
        bookService.snedEmail();
    }

    @Test
    public void generatePresetStations() {
        // 预设四个成都市区街道站点
        String[] streetNames = {"春熙路街道", "书院街街道", "双水碾街道", "玉林街道"};
        double[][] coordinates = {
                {30.6586, 104.0757}, // 春熙路街道大致经纬度
                {30.6678, 104.0873}, // 书院街街道大致经纬度
                {30.7048, 104.0731}, // 双水碾街道大致经纬度
                {30.6383, 104.0526}  // 玉林街道大致经纬度
        };

        for (int i = 0; i < streetNames.length; i++) {
            Station station = new Station();
            station.setName(streetNames[i]);
            station.setLatitude(coordinates[i][0]);
            station.setLongitude(coordinates[i][1]);
            stationRepository.save(station);
        }
    }


    @Test
    public void changeUserPassword() {
        // 获取密码编码器
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 查找 id 为 10 的用户
        User user = userRepository.findById(21).orElseThrow(() ->
                new RuntimeException("用户不存在，id: 10")
        );

        // 设置新密码并加密
        String newPassword = "123456";
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        // 保存更新后的用户信息
        userRepository.save(user);
    }

    @Test
    public void testUserWithId10PlaceOrder() {
        // 获取 id 为 10 的用户
        User user = userRepository.findById(10)
                .orElseThrow(() -> new RuntimeException("User not found with id: 10"));

        // 获取一个可用的滑板车
        Scooter scooter = scooterRepository.findAll().stream()
                .filter(s -> s.getStatus() == 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available scooter found"));

        // 获取一个租用选项
        HireOption hireOption = hireOptionRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hire option found"));

        // 设置订单的开始和结束时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(hireOption.getHours());
        String startTimeStr = startTime.format(formatter);
        String endTimeStr = endTime.format(formatter);

        // 创建 addBookDto 对象
        addBookDto bookDto = new addBookDto();
        bookDto.setUserId(user.getId());
        bookDto.setScooterId(scooter.getId());
        bookDto.setHireOptionId(hireOption.getId());
        bookDto.setStartTime(startTimeStr);
        bookDto.setEndTime(endTimeStr);

        // 调用服务层的方法下订单
        Book newBook = bookService.addBook(bookDto);

        // 验证订单是否成功创建
        assertNotNull(newBook.getId(), "Order creation failed");
    }
    @Test
    public void changePasswords(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findById(10)
                .orElseThrow(() -> new RuntimeException("User not found with id: 10"));
        String originalPassword = "123456";
        String encryptedPassword = passwordEncoder.encode(originalPassword);
        user.setPassword(encryptedPassword);
        // 保存更新后的用户信息
        userRepository.save(user);
    }

    @Test
    public void encryptUserPasswords() {
        // 创建 BCrypt 密码编码器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 获取所有用户
        List<User> allUsers = userRepository.findAll();

        // 遍历所有用户
        for (User user : allUsers) {
            // 获取用户的原始密码
            String originalPassword = user.getPassword();

            // 对密码进行加密
            String encryptedPassword = passwordEncoder.encode(originalPassword);

            // 设置加密后的密码
            user.setPassword(encryptedPassword);

            // 保存更新后的用户信息
            userRepository.save(user);
        }
    }


    @Test
    public void generate10RecentBookingsByHireOptionId() {
        // 假设我们有一个租赁选项 ID
        Integer hireOptionId = 1;

        // 获取指定 ID 的租赁选项
        HireOption hireOption = hireOptionRepository.findById(hireOptionId)
                .orElseThrow(() -> new RuntimeException("HireOption not found with id: " + hireOptionId));

        // 获取所有用户
        List<User> allUsers = userRepository.findAll();
        // 获取所有可用的滑板车
        List<Scooter> availableScooters = scooterRepository.findAll().stream()
                .filter(s -> s.getStatus() == 0)
                .collect(Collectors.toList());

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            // 随机选择一个用户
            User user = allUsers.get(random.nextInt(allUsers.size()));
            // 随机选择一个可用的滑板车
            Scooter scooter = availableScooters.get(random.nextInt(availableScooters.size()));

            // 设置订单的开始和结束时间，这里假设起始时间为当前时间减去 i 小时
            LocalDateTime startTime = LocalDateTime.now().minusHours(i);
            LocalDateTime endTime = startTime.plusHours(hireOption.getHours());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            String startTimeStr = startTime.format(formatter);
            String endTimeStr = endTime.format(formatter);

            // 创建 addBookDto 对象
            addBookDto bookDto = new addBookDto();
            bookDto.setUserId(user.getId());
            bookDto.setScooterId(scooter.getId());
            bookDto.setHireOptionId(hireOption.getId());
            bookDto.setStartTime(startTimeStr);
            bookDto.setEndTime(endTimeStr);

            // 调用服务层的方法下订单
            Book newBook = bookService.addBook(bookDto);

            // 验证订单是否成功创建
            assertNotNull(newBook.getId(), "Order creation failed");
        }
    }
    @Test
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
