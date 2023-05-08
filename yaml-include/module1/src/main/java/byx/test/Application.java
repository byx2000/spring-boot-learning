package byx.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties({Module1Config.class, Module2Config.class})
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        Module1Config c1 = ctx.getBean(Module1Config.class);
        Module2Config c2 = ctx.getBean(Module2Config.class);
        System.out.println(c1);
        System.out.println(c2);
    }
}