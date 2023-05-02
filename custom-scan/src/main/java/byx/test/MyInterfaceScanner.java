package byx.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;

import java.util.ArrayList;
import java.util.List;

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
