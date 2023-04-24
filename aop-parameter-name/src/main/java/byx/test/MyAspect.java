package byx.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Aspect
@Controller
public class MyAspect {
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
