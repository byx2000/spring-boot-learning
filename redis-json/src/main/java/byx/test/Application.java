package byx.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        RedisUtils redisUtils = ctx.getBean(RedisUtils.class);

        redisUtils.set("val", 123345);
        int val = redisUtils.get("val");
        System.out.println("val = " + val);

        redisUtils.set("nums", List.of(100, 200, 300, 400, 500));
        List<Integer> nums = redisUtils.get("nums");
        System.out.println("nums = " + nums);

        redisUtils.set("student", new Student(1001, "aaa"));
        Student student = redisUtils.get("student", Student.class);
        System.out.println("student = " + student);

        redisUtils.set("students", List.of(
                new Student(1001, "aaa"),
                new Student(1002, "bbb"),
                new Student(1003, "ccc")
        ));
        List<Student> ss = redisUtils.get("students");
        System.out.println("students = " + ss);
    }
}