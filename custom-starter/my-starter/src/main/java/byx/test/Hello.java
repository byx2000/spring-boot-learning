package byx.test;

import org.springframework.beans.factory.annotation.Value;

public class Hello {
    @Value("${hello.msg:hello from custom starter}")
    private String msg;

    public void sayHello() {
        System.out.println(msg);
    }
}
