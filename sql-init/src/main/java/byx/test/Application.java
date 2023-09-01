package byx.test;

import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        UserMapper userMapper = ctx.getBean(UserMapper.class);
        List<User> users = userMapper.listAllUsers();
        users.forEach(System.out::println);
    }
}

@Mapper
interface UserMapper {
    @Select("SELECT * FROM users")
    List<User> listAllUsers();
}

@Data
class User {
    private Integer id;
    private String username;
    private String password;
}
