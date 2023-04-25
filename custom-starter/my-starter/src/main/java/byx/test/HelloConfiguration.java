package byx.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class HelloConfiguration {
    @Bean
    public Hello hello() {
        return new Hello();
    }

    @PostConstruct
    public void init() {
        System.out.println("HelloConfiguration init");
    }
}
