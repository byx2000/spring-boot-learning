# 自定义Spring Boot Starter

1. 在`resources`目录下新建文件`META-INF/spring.factories`
2. 写入如下内容，声明需要自动导入的配置类
    ```properties
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=XXX
    ```