# Spring Boot应用启动时自动建表和插入测试数据

Spring Boot应用程序往往需要依赖数据库，如果想把自己的项目交给其他人运行，或者自己运行其他人的项目，需要手动执行建表语句，以及插入测试数据，十分麻烦。为了方便项目的运行，可以提前编写好建表语句和插入测试数据的语句，然后配置Spring Boot在启动时自动执行这些语句。

## 步骤

1. 在`resources`目录下添加`schema.sql`文件，编写建表语句
    ```sql
    CREATE TABLE users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT NOT NULL,
        password TEXT NOT NULL
    );
    ```
2. 在`resources`目录下添加`data.sql`文件，编写插入测试数据的insert语句
    ```sql
    INSERT INTO users (id, username, password) VALUES (1, 'user1', '123');
    INSERT INTO users (id, username, password) VALUES (2, 'user2', '456');
    INSERT INTO users (id, username, password) VALUES (3, 'user3', '789');
    ```
3. `application.yml`文件中添加以下配置
    ```yml
    spring:
      datasource:
        driverClassName: xxx
        url: xxx
        username: xxx
        password: xxx
        schema:
          - classpath:schema.sql # 建表语句
        data:
          - classpath:data.sql # 数据插入语句
        initialization-mode: always # 始终初始化
        continue-on-error: true # 初始化语句执行失败时继续启动应用
    ```
4. 启动应用，`schema.sql`和`data.sql`中的sql语句会自动执行

## 注意事项

1. 务必保证sql语句的正确性，如果sql执行失败不会有任何提示
2. 如果设置了主键自增，insert语句最好指定id，防止多次启动应用导致插入重复数据