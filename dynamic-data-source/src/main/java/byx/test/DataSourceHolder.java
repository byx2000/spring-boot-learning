package byx.test;

/**
 * 使用ThreadLocal保存当前数据源
 */
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
