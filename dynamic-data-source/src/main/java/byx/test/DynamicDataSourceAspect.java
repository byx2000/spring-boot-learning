package byx.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DynamicDataSourceAspect {
    /**
     * Mapper方法切面实现，对所有标注了Db注解的方法生效
     */
    @Around("@annotation(byx.test.Db)")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法上的Db注解
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();
        Db db = method.getAnnotation(Db.class);

        try {
            // 方法执行前先设置当前数据源，再执行方法
            DataSourceHolder.setDataSource(db.value());
            return jp.proceed();
        } finally {
            // 方法结束后清理当前数据源
            DataSourceHolder.clear();
        }
    }
}
