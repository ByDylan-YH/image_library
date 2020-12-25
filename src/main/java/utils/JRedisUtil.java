package utils;

import org.springframework.data.redis.core.StringRedisTemplate;

public class JRedisUtil {
    private StringRedisTemplate stringRedisTemplate;

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String getKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Object hgetKey(String key,String field){
        return stringRedisTemplate.opsForHash().get(key, field);
    }
}
