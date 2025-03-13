package com.shutiao.leasingsystem;

import com.shutiao.leasingsystem.pojo.entity.*;
import com.shutiao.leasingsystem.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

//    @BeforeEach
//    public void setup() {
//        // 清空数据库中的所有用户
//        userRepository.deleteAll();
//    }

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
        double radius = 0.02; // 设定一个小范围半径（大约2公里内）

        for (int i = 1; i <= 5; i++) {
            Scooter scooter = new Scooter();
            scooter.setLatitude(centerLat + (random.nextDouble() * radius - radius / 2));
            scooter.setLongitude(centerLng + (random.nextDouble() * radius - radius / 2));
            scooter.setStatus(0); // 0=可用, 1=不可用, 2=维修中
            scooter.setPower(random.nextInt(101)); // 0~100 随机电量
            scooter.setConfig("Speed: 30 km/h, 包含头盔与手机支架"); // 速度 5~30km/h
            scooterRepository.save(scooter);
        }
    }


    @Test
    public void generateHireOption() {
        Integer[] hours = {1, 4, 24};
        Integer[] money = {10, 30, 100};
        for (int i = 0; i < 3; i++){
            HireOption hireOption = new HireOption();
            hireOption.setHours(hours[i]);
            hireOption.setMoney(money[i]);
            hireOptionRepository.save(hireOption);
        }
    }


    @Test
    public void generateTestBookings() {
        Integer[] user_ids = {1, 4, 6};
        Integer[] scooter_ids = {1, 2, 3};
        Integer[] hire_ids = {1, 1, 3};

        for (int i = 0; i < 3; i++){
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
            book.setStatus("active");

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
        feedback.setStatus("new");           // 设置反馈状态（例如 new、resolved 等）

        // 保存反馈记录到数据库
        feedbackRepository.save(feedback);
    }

}
