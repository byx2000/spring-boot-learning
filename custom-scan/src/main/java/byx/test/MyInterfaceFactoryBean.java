package byx.test;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class MyInterfaceFactoryBean implements FactoryBean<Object> {
    private final Class<?> type;

    public MyInterfaceFactoryBean(Class<?> type) {
        this.type = type;
    }

    @Override
    public Object getObject() {
        MyInterface anno = type.getAnnotation(MyInterface.class);
        if (anno == null) {
            return null;
        }

        // 为接口创建代理对象
        return  Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            if ("sayHello".equals(method.getName())) {
                System.out.println("hello from " + anno.value());
                return null;
            }
            return method.invoke(proxy, args);
        });
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
