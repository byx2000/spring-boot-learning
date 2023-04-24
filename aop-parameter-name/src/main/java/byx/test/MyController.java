package byx.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyController {
    public String method1(Integer id, String msg) {
        System.out.println("in MyController.method1");
        return id + " " + msg;
    }
}
