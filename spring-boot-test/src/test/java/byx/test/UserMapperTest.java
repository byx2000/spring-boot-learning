package byx.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapper() {
        List<User> users = userMapper.listAllUsers();
        assertEquals(3, users.size());
        assertEquals(new User(1, "user1", "123"), users.get(0));
        assertEquals(new User(2, "user2", "456"), users.get(1));
        assertEquals(new User(3, "user3", "789"), users.get(2));
    }
}
