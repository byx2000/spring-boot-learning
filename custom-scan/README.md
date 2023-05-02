# Spring Boot自定义扫描

通过使用Spring Boot自带的扩展机制，可以实现自定义类扫描和自定义接口扫描。

## 创建自定义注解

`MyClass`标注在需要扫描的类上：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyClass {
}
```

`MyInterface`标注在需要扫描的接口上：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyInterface {
    String value();
}
```

## 实现类扫描逻辑

`MyClassScanner`实现了`ImportBeanDefinitionRegistrar`接口，使用`Import`注解引入后，会在容器初始化阶段执行`registerBeanDefinitions`方法的逻辑。`MyClassScanner`内部包含了自定义扫描和注册bean的逻辑。

```java
@Slf4j
public class MyClassScanner implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        try {
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(MyClass.class));
            String mainClassName = metadata.getClassName();
            String basePackage = Class.forName(mainClassName).getPackage().getName();
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(basePackage);
            definitions.forEach(d -> {
                log.info("register bean: {}", d.getBeanClassName());
                registry.registerBeanDefinition(d.getBeanClassName(), d);
            });
        } catch (Exception e) {
            log.error("error when scan classes: {}", e.getMessage());
        }
    }
}
```

`ClassPathBeanDefinitionScanner`是Spring自带的工具类，用于扫描指定包下的类，可以使用`addIncludeFilter`方法添加过滤器，这里添加了`AnnotationTypeFilter`根据注解过滤。

`findCandidateComponents`方法会执行类扫描操作，并将扫描到的类封装成`BeanDefinition`，会自动解析依赖。

`registry.registerBeanDefinition`用于向容器中注册bean，传入id和`BeanDefinition`实例。

## 实现接口扫描逻辑

由于接口无法实例化，所以需要以动态代理的方式注入Spring容器。`MyInterfaceFactoryBean`实现了`FactoryBean<T>`，内部的`getObject`包含了生成代理对象的逻辑。

```java
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
```

`MyInterfaceScanner`同样实现了`ImportBeanDefinitionRegistrar`，内部包含了接口扫描和注册的逻辑。

```java
@Slf4j
public class MyInterfaceScanner implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);

        List<Class<?>> interfaces = new ArrayList<>();
        scanner.addIncludeFilter(new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                // 匹配被MyInterface标注的接口
                try {
                    if (!metadata.isInterface()) {
                        return false;
                    }

                    Class<?> type = Class.forName(metadata.getClassName());
                    if (type.getAnnotation(MyInterface.class) == null) {
                        return false;
                    }

                    interfaces.add(type);
                    return true;
                } catch (Exception e) {
                    log.error("error when scan interfaces: {}", e.getMessage());
                    return false;
                }
            }
        });

        try {
            String mainClassName = metadata.getClassName();
            String basePackage = Class.forName(mainClassName).getPackage().getName();
            scanner.findCandidateComponents(basePackage);

            interfaces.forEach(type -> {
                log.info("register bean: {}", type.getCanonicalName());
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyInterfaceFactoryBean.class);
                AbstractBeanDefinition definition = builder.getRawBeanDefinition();
                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                constructorArgumentValues.addIndexedArgumentValue(0, type);
                definition.setConstructorArgumentValues(constructorArgumentValues);
                registry.registerBeanDefinition(type.getCanonicalName(), definition);
            });
        } catch (Exception e) {
            log.error("error when scan interfaces: {}", e.getMessage());
        }
    }
}
```

与类扫描不同，接口扫描不能直接获取`scanner.findCandidateComponents`的返回值，因为接口无法被解析成`BeanDefinition`。所以需要自定义一个`TypeFilter`，在扫描过程中手动收集所有接口，然后通过`FactoryBean`的方式注入到容器中。

## 开启自定义扫描

`EnableCustomScan`注解导入了上面编写的两个`ImportBeanDefinitionRegistrar`实现，将其标注在启动类上即可启用这两个自定义扫描器。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({MyClassScanner.class, MyInterfaceScanner.class})
public @interface EnableCustomScan {
}
```
