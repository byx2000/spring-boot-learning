# yaml文件复用

如果项目的配置过多，可以把一些配置项放在单独的yaml文件中，并以`application-xxx.yaml`命名。这些配置文件可以保存在当前项目的`resources`目录中，也可以保存在maven依赖的`resources`目录中，并在当前项目引入。

在`application.yml`文件中使用`spring.profiles.include`引入额外的配置文件：

```yaml
spring:
  profiles:
    include: module1,module2 # 引入了application-module1.yaml和application-module2.yaml
```
