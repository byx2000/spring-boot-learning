package byx.test;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源，基于Spring Boot自带的AbstractRoutingDataSource实现
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        // 获取代表当前数据源的key
        return DataSourceHolder.getDataSource();
    }
}
