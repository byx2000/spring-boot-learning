# Spring Boot实现动态多数据源切换

Spring Boot应用中可以配置多个数据源，并根据注解灵活指定当前使用的数据源

## 步骤

1. 在`application.yml`中配置数据源信息
    ```yml
    spring:
      datasource:
        ds1:
          driverClassName: org.sqlite.JDBC
          jdbcUrl: jdbc:sqlite::resource:ds1.db
        ds2:
          driverClassName: org.sqlite.JDBC
          jdbcUrl: jdbc:sqlite::resource:ds2.db
    ```
2. 实现`DataSourceHolder`，用于保存当前数据源
   ```java
   public class DataSourceHolder {
       private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();
   
       public static void setDataSource(String ds) {
           HOLDER.set(ds);
       }
   
       public static String getDataSource() {
           return HOLDER.get();
       }
   
       public static void clear() {
           HOLDER.remove();
       }
   }
   ```
3. 基于Spring Boot自带的`AbstractRoutingDataSource`实现`DynamicDataSource`
   ```java
   public class DynamicDataSource extends AbstractRoutingDataSource {
       @Override
       protected Object determineCurrentLookupKey() {
           // 获取代表当前数据源的key
           return DataSourceHolder.getDataSource();
       }
   }
   ```
4. 添加配置类`DataSourceConfig`，向容器中注入所有数据源，并将`DynamicDataSource`标注为`Primary`
   ```java
   @Configuration
   public class DataSourceConfig {
       /**
        * 数据源1配置
        */
       @Bean("ds1")
       @ConfigurationProperties("spring.datasource.ds1")
       public DataSource ds1() {
           return DataSourceBuilder.create().build();
       }
   
       /**
        * 数据源2配置
        */
       @Bean("ds2")
       @ConfigurationProperties("spring.datasource.ds2")
       public DataSource ds2() {
           return DataSourceBuilder.create().build();
       }
   
       /**
        * 动态数据源配置
        */
       @Bean
       @Primary
       public DataSource dynamicDataSource(@Qualifier("ds1") DataSource ds1, @Qualifier("ds2") DataSource ds2) {
           DynamicDataSource ds = new DynamicDataSource();
           // 设置数据源映射关系
           ds.setTargetDataSources(Map.of(
                   "ds1", ds1,
                   "ds2", ds2
           ));
           // 设置默认数据源
           ds.setDefaultTargetDataSource(ds1);
           return ds;
       }
   }
   ```
5. 添加自定义注解`Db`，标注在方法上，指定方法内部执行时所使用的数据源
   ```java
   @Target(ElementType.METHOD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Db {
       String value();
   }
   ```
6. 实现切面类`DynamicDataSourceAspect`，对所有标注了`Db`注解的方法进行增强
   ```java
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
   ```
