package byx.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        UserMapper userMapper = ctx.getBean(UserMapper.class);
        userMapper.listUsersFromDs1().forEach(System.out::println);
        userMapper.listUsersFromDs2().forEach(System.out::println);
    }
}
