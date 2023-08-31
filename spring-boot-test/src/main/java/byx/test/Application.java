package byx.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    public List<User> listAllUsers() {
        return userMapper.listAllUsers();
    }
}

@Mapper
interface UserMapper {
    @Select("SELECT * FROM users ORDER BY id")
    List<User> listAllUsers();
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    private Integer id;
    private String username;
    private String password;
}