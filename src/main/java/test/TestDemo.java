package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import utils.JRedisUtil;

/**
 * Author:BY
 * Date:2020/3/30
 * Description:
 */
public class TestDemo {
    private static final Logger logger = LoggerFactory.getLogger(TestDemo.class);
    private static final Jedis jedis = new Jedis("77.1.37.197", 6379);
    private static JRedisUtil jRedisUtil = null;

    static {
        jedis.auth("cqdsjb");
    }
    @Autowired
    public TestDemo(JRedisUtil jRedisUtil) {
        this.jRedisUtil = jRedisUtil;
    }

    public static void main(String[] args) {
        logger.info("Redis运行状态 {}", jedis.ping());
        System.out.println(jRedisUtil.hgetKey("FXCD5UFGGFAZ0B4LOOOG", "tenantname"));
    }
}
