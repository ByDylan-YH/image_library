package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.JRedisUtil;

/**
 * Author:BY
 * Date:2020/3/26
 * Description:
 */
public class JedisTest {
    //    @Autowired
//    @Qualifier("jRedisUtil")
//    private static JRedisUtil jRedisUtil;
    private static final Logger logger = LoggerFactory.getLogger(JedisTest.class);

    public static void main(String[] args) {
        String xmlPath = "applicationContext.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
        JRedisUtil jRedisUtil = applicationContext.getBean("jRedisUtil", JRedisUtil.class);
        Object tenantname = jRedisUtil.hgetKey("FXCD5UFGGFAZ0B4LOOOG", "tenantname");
        Object tenantid = jRedisUtil.hgetKey("FXCD5UFGGFAZ0B4LOOOG", "tenantid");
        Object resourcename = jRedisUtil.hgetKey("a7e6b12e-6b4b-4099-9346-b1716256ca03|81e1911a-7a85-40bf-86fa-42cc89494b52", "resourcename");
        logger.info("tenantname: {} , tenantid: {}, resourcename: {} ", tenantname, tenantid, resourcename);
    }

//    @Test
//    public void demo(){
//        System.out.println(jRedisUtil.hgetKey("NDS8AI3C9OKW2D464C01","tenantname"));
//    }
}
