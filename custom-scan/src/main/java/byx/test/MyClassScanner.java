package byx.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

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
