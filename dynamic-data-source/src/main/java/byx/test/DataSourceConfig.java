package byx.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

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
    @Primary // 必须加这个注解，否则MyBatis找不到主数据源
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
