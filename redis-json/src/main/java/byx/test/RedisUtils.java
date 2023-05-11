package byx.test;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;
    private final ValueOperations<String, String> stringValueOps;
    private final JsonUtils jsonUtils;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate,
                      StringRedisTemplate stringRedisTemplate,
                      JsonUtils jsonUtils) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.stringValueOps = stringRedisTemplate.opsForValue();
        this.jsonUtils = jsonUtils;
    }

    /**
     * 设置key的值并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        valueOps.set(key, value, timeout, unit);
    }

    /**
     * 设置key的值
     */
    public void set(String key, Object value) {
        valueOps.set(key, value);
    }

    /**
     * 获取key的值并转换为指定类型
     */
    public <T> T get(String key, Class<T> type) {
        try {
            String json = stringValueOps.get(key);
            return jsonUtils.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取key的值
     */
    public <T> T get(String key) {
        try {
            String json = stringValueOps.get(key);
            return jsonUtils.fromJson(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置key的过期时间
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}
