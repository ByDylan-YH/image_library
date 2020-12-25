package utils;

import com.bingosoft.services.commons.DaaSCredentials;
import com.bingosoft.services.daasDMS.DaasDMSClient;
import com.bingosoft.services.daasDMS.model.ExecuteDataServiceRequest;
import com.bingosoft.services.daasDMS.model.ExecuteDataServiceResult;
import com.bingosoft.services.daasDMS.model.common.RowType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Author:BY
 * Date:2020/3/25
 * Description:根据业务逻辑,分两次请求API获得图片s3路径
 */
public class RequestApi {
    private static DaasDMSClient client;
    private static ExecuteDataServiceRequest request;
    private static RequestApi requestApi = new RequestApi();
    private static final Logger logger = LoggerFactory.getLogger(RequestApi.class);

    static {
        client = new DaasDMSClient(new DaaSCredentials("A1CE973FC797E160FFC0", "fx1fPuHcfCnnkKtNSwAZQzBsdH82OKUm2uBOezMMYzxxNgpUPgU6fBrr"), "550C75A5A2EF43E7914DBAD6FB4583B2");
        client.setEndpoint("http://77.1.37.15:8080/daasDMS/api");
        request = new ExecuteDataServiceRequest();
    }

    private Set<Object> firstRequestApi(String requestcode, String resourceapicode, String resourceapirequestfield, String resourceapiresultfield) {
        HashSet<Object> set = new HashSet<>();
        Object returnResult = null;
        Map<String, Object> paramsMap = new HashMap<>();
        request.setRowType(RowType.map.toString());
        request.setDataServiceCode(resourceapicode);
        paramsMap.put(resourceapirequestfield, requestcode);
        request.setDataServiceParams(paramsMap);
        ExecuteDataServiceResult result = client.executeDataService(request);
        try {
            for (Map resultMap : result.getRows()) {
                set.add(resultMap.get(resourceapiresultfield));
            }
        } catch (NullPointerException e) {
            logger.warn("RequestApi 第一次请求API报错: {} , 可能是没有找到这个requestcode", e.getMessage());
        }
        return set;
    }

    private Map<Object, Object> secondRequestApi(String pictureapicode, String pictureapirequestfield, String pictureapitimefield, String pictureapiresultfield, Set<Object> requestcodeSet) {
        HashMap<Object, Object> map = new HashMap<>();
        Object returnResult = null;
        Map<String, Object> paramsMap = new HashMap<>();
        request.setRowType(RowType.map.toString());
        request.setDataServiceCode(pictureapicode);
        for (Object requestcode : requestcodeSet) {
            paramsMap.put(pictureapirequestfield, requestcode);
            request.setDataServiceParams(paramsMap);
            ExecuteDataServiceResult result = client.executeDataService(request);
            try {
                for (Map resultMap : result.getRows()) {
                    map.put(resultMap.get(pictureapiresultfield), resultMap.get(pictureapitimefield));
                }
            } catch (Exception e) {
                logger.warn("第二次请求API报错: {} 可能某个rid没有s3路径信息", e.getMessage());
            }
        }
        logger.info("获取到s3路径 {} 个", map.size());
        return map;
    }

    public Map<Object, Object> request(String requestcode, String resourceapicode, String resourceapirequestfield, String resourceapiresultfield, String pictureapicode, String pictureapirequestfield, String pictureapitimefield, String pictureapiresultfield) {
        logger.info("第一次请求参数 requestcode: {} , resourceapicode: {} , resourceapirequestfield: {} , resourceapiresultfield: {}", requestcode, resourceapicode,resourceapirequestfield,resourceapiresultfield);
        Set<Object> firstRequestApi = requestApi.firstRequestApi(requestcode, resourceapicode, resourceapirequestfield, resourceapiresultfield);
        logger.info("第一次请求结果有: {} 个 , 明细: {}", firstRequestApi.size(), firstRequestApi);
        Map<Object, Object> map;
        logger.info("第二次请求参数 pictureapicode: {} , pictureapirequestfield: {} , pictureapitimefield: {} , pictureapiresultfield: {}", pictureapicode, pictureapirequestfield,pictureapitimefield,pictureapiresultfield);
        map = requestApi.secondRequestApi(pictureapicode, pictureapirequestfield, pictureapitimefield, pictureapiresultfield, firstRequestApi);
        logger.info("第二次请求结果有: {} 个 , 明细: {}", map.size(), map);
        return map;
    }

    @Test
    public void demo() {
//        全国在逃
        requestApi.request("132302196812303814", "tbywztryapi", "ZJHM", "ZTRYBH", "cqdsjbquanguozaitao", "ztrybh", "insert_time", "pic_path");
//        常住人口
//        requestApi.request("500232200410082798","cqdsjbzack","sfzhm","rid","cqdsjbedzsy","rid","insert_time","pic_path");
    }
}
