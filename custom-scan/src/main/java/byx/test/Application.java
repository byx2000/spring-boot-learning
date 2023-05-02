package byx.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCustomScan
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

        MyClass1 c1 = ctx.getBean(MyClass1.class);
        MyClass2 c2 = ctx.getBean(MyClass2.class);
        c1.sayHello();
        c2.sayHello();

        MyInterface1 i1 = ctx.getBean(MyInterface1.class);
        MyInterface2 i2 = ctx.getBean(MyInterface2.class);
        i1.sayHello();
        i2.sayHello();
    }
}