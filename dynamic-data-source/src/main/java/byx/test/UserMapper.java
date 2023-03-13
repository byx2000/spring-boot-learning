package byx.test;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users")
    @Db("ds1")
    List<User> listUsersFromDs1();

    @Select("SELECT * FROM users")
    @Db("ds2")
    List<User> listUsersFromDs2();
}
