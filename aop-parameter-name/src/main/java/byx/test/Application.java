package byx.test;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        MyController c = ctx.getBean(MyController.class);
        System.out.println(c.method1(1001, "hello"));
    }
}

@Aspect
@Controller
class MyAspect {
    @Around("execution(* byx.test.MyController.method1(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        System.out.println("before " + signature.getName());
        System.out.println("parameters: " + Arrays.toString(signature.getParameterNames()));
        System.out.println("args: " + Arrays.toString(pjp.getArgs()));
        Object ret = pjp.proceed();
        System.out.println("after " + signature.getName());
        return ret;
    }
}

@Component
@Slf4j
class MyController {
    public String method1(Integer id, String msg) {
        System.out.println("in MyController.method1");
        return id + " " + msg;
    }
}
