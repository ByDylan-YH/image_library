package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import utils.JRedisUtil;
import utils.RequestApi;

import java.util.Map;

/**
 * Author:BY
 * Date:2020/3/25
 * Description:通过传入的 resourceid,requestcode 获取s3路径
 */
@Service
public class GetPictureAddr {
    private static final Logger logger = LoggerFactory.getLogger(GetPictureAddr.class);
    private static final JRedisUtil jRedisUtil;
    private static final RequestApi requestApi = new RequestApi();

    static {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        jRedisUtil = applicationContext.getBean("jRedisUtil", JRedisUtil.class);
    }

    public Map<Object, Object> getAddr(String resourceid, String requestcode) {
        String resourceapicode = jRedisUtil.hgetKey(resourceid, "resourceapicode").toString();
        String resourceapirequestfield = jRedisUtil.hgetKey(resourceid, "resourceapirequestfield").toString();
        String resourceapiresultfield = jRedisUtil.hgetKey(resourceid, "resourceapiresultfield").toString();
        String pictureapicode = jRedisUtil.hgetKey(resourceid, "pictureapicode").toString();
        String pictureapirequestfield = jRedisUtil.hgetKey(resourceid, "pictureapirequestfield").toString();
        String pictureapitimefield = jRedisUtil.hgetKey(resourceid, "pictureapitimefield").toString();
        String pictureapiresultfield = jRedisUtil.hgetKey(resourceid, "pictureapiresultfield").toString();
        Map<Object, Object> s3Addr;
        s3Addr = requestApi.request(requestcode, resourceapicode, resourceapirequestfield, resourceapiresultfield, pictureapicode, pictureapirequestfield, pictureapitimefield, pictureapiresultfield);
        return s3Addr;
    }
}
