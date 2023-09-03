# Spring Boot单元测试

1. 引入以下依赖

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
   
    <!--内存数据库-->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.220</version>
        <scope>test</scope>
    </dependency>
    ```

2. 在`test/resources`路径下新建配置文件`application.yml`，配置测试用的h2数据库

   Spring Boot 2.5.0以前：   

    ```yaml
    spring:
      datasource:
        url: jdbc:h2:mem:test_db;MODE=MYSQL;
        schema:
          - classpath:schema.sql # 建表语句
        data:
          - classpath:data.sql # 数据插入语句
        continue-on-error: true # 初始化语句执行失败时继续启动应用
    ```
   
   Spring Boot 2.5.0以后：
      
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:test_db;MODE=MYSQL;
     sql:
       init:
         schema-locations:
           - classpath:schema.sql # 建表语句
         data-locations:
           - classpath:data.sql # 数据插入语句
         mode: always # 始终初始化
         continue-on-error: true # 初始化语句执行失败时继续启动应用
   ```

3. 在`test/resources`路径下新建`schema.sql`文件，编写建表语句

    ```sql
    CREATE TABLE users (
        id INTEGER PRIMARY KEY AUTO_INCREMENT,
        username TEXT UNIQUE NOT NULL,
        password TEXT UNIQUE NOT NULL
    );
    ```
   
4. 在`test/resources`路径下新建`data.sql`文件，编写数据插入语句

    ```sql
    INSERT INTO users (id, username, password) VALUES (1, 'user1', '123');
    INSERT INTO users (id, username, password) VALUES (2, 'user2', '456');
    INSERT INTO users (id, username, password) VALUES (3, 'user3', '789');
    ```

5. 测试MyBatis Mapper接口

    ```java
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
    ```

    要在Spring Boot容器中启动测试用例，需要在测试类上添加`@SpringBootTest`注解

6. 测试Controller

    ```java
    @SpringBootTest
    @AutoConfigureMockMvc
    public class UserControllerTest {
        @Autowired
        private MockMvc mockMvc;
    
        @Autowired
        private ObjectMapper mapper;
    
        @Test
        public void testListAllUsers() throws Exception {
            MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
            String json = result.getResponse().getContentAsString();
            List<User> users = mapper.readValue(json, new TypeReference<>() {});
            assertEquals(3, users.size());
            assertEquals(new User(1, "user1", "123"), users.get(0));
            assertEquals(new User(2, "user2", "456"), users.get(1));
            assertEquals(new User(3, "user3", "789"), users.get(2));
        }
    }
    ```
   要使用`MockMvc`，需要在测试类上添加`@AutoConfigureMockMvc`注解