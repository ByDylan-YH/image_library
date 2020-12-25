package controller;

import com.alibaba.fastjson.JSONObject;
import test.JedisTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.GetPicture;
import service.GetPictureAddr;
import service.InsertRequestLogServer;
import utils.JRedisUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(method = RequestMethod.POST, value = "/picture")
public class GetPictureController {
    private final Logger logger = LoggerFactory.getLogger(JedisTest.class);
    private final InsertRequestLogServer insertRequestLogServer;
    private final GetPictureAddr getS3Addr;
    private final GetPicture getPicture;
    private final JRedisUtil jRedisUtil;
    private String accessKey;
    private String resourceid;
    private String requestcode;
    private String tenantname;
    private Map<Object, Object> map = new HashMap<>();

//    通过构造器从容器中导入对象
    @Autowired
    public GetPictureController(InsertRequestLogServer insertRequestLogServer, GetPictureAddr getS3Addr, GetPicture getPicture, JRedisUtil jRedisUtil) {
        this.insertRequestLogServer = insertRequestLogServer;
        this.getS3Addr = getS3Addr;
        this.getPicture = getPicture;
        this.jRedisUtil = jRedisUtil;
    }

//   初始化注解方法
    @ModelAttribute
    public void ini(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
//        从Spring容器中获得对象
//        String xmlPath = "applicationContext.xml";
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
//        JRedisUtil jRedisUtil = applicationContext.getBean("jRedisUtil", JRedisUtil.class);

//        解析请求
        accessKey = request.getHeader("accessKey");
        resourceid = jsonObject.getString("resourceid");
        requestcode = jsonObject.getString("requestcode");
        tenantname = jRedisUtil.hgetKey(accessKey, "tenantname").toString();
        String tenantid = jRedisUtil.hgetKey(accessKey, "tenantid").toString();
        String resourcename = jRedisUtil.hgetKey(tenantid + "|" + resourceid, "resourcename").toString();
        map.put("host", request.getHeader("host"));
        map.put("resourceid", resourceid);
        map.put("resourcename", resourcename);
        map.put("projectname", tenantname);
        map.put("requestcode", requestcode);
    }

    @ResponseBody
    @PostMapping("/getPicture")
    public JSONObject getPicture(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        logger.info("GetPictureController 传入参数 accessKey: {} , resourceid: {} ,requestcode : {} ,解析项目名为: {} ", accessKey, resourceid, requestcode, tenantname);
        Map<Object, Object> picAddr = getS3Addr.getAddr(resourceid, requestcode);
        JSONObject resultJson = getPicture.downloadAndDecode(picAddr, tenantname);
        map.put("resultsize", resultJson.size());
//        写入日志
        insertRequestLogServer.intoLog(map);
        if (resultJson.size() != 0) {
            logger.info("请求requestcode: {} 获取到 {} 张照片^_^\n", requestcode, resultJson.size());
        } else {
            resultJson.clear();
            resultJson.put("未获取到照片", "");
        }
        return resultJson;
    }
}
