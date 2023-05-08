package byx.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("module1")
@Data
public class Module1Config {
    private String key1;
    private String key2;
    private String key3;
}
