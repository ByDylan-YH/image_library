package service;

import com.alibaba.fastjson.JSONObject;
import com.bingocloud.auth.BasicAWSCredentials;
import com.bingocloud.services.s3.AmazonS3Client;
import com.bingocloud.services.s3.model.GetObjectRequest;
import com.bingocloud.services.s3.model.S3Object;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import utils.EditPicture;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:BY
 * Date:2020/4/1
 * Description:通过地址下载图片
 * {cqdsjbpic4/share/ZHIAN113_ZP/0/200064377640_1575380.jpg=2018-08-09 23:48:16}
 */
@Service
public class GetPicture {
    private static final Logger logger = LoggerFactory.getLogger(GetPictureAddr.class);
    private static final GetPicture getPicture = new GetPicture();
    //    private static final TextMarkProcessor textMarkProcessor = new TextMarkProcessor();
    private static final EditPicture editPicture = new EditPicture();
    private static final AmazonS3Client client = new AmazonS3Client(new BasicAWSCredentials("1SLEMJJTW2JKWDPQ1FI5", "NgAp2FBBfxTYEBBojdMDG0iEfKhXQTyMgae7P0VQ"));

    //落地S3上的文件到本地
    public JSONObject downloadAndDecode(Map<Object, Object> map, String tenantname) {
        JSONObject jsonObject = new JSONObject();
        String bucketName;
        String addr;
        String filename;
        String insertime;
        String pathname;
//        File inifile = null;
        File resultFile = null;
        client.setEndpoint("http://169.254.169.3:80"); // S3的内部S3服务接口
        for (Object key : map.keySet()) {
            String[] strings = key.toString().split("/");
            bucketName = strings[0];
            filename = strings[strings.length - 1];
            addr = key.toString().replace(bucketName + "/", "");
            insertime = map.get(key).toString().replace("-", "").replace(" ", "").replace(":", "");
            logger.info("s3路径切分信息 bucketName: {} , addr: {} , filename: {} ,insertime: {}", bucketName, addr, filename, insertime);
            GetObjectRequest request = new GetObjectRequest(bucketName, addr);
            S3Object res = client.getObject(request);
            try {
                if (res != null) {
                    BufferedInputStream in = new BufferedInputStream((res.getObjectContent()));
//                    inifile = new File(GetPicture.class.getProtectionDomain().getCodeSource().getLocation().getPath() + filename);
                    String resultFilename = GetPicture.class.getProtectionDomain().getCodeSource().getLocation().getPath() + insertime + "_" + filename;
                    resultFile = new File(resultFilename);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(resultFile));
                    byte[] bb = new byte[1024];
                    int n;
                    while ((n = in.read(bb)) != -1) {
                        out.write(bb, 0, n);
                    }
                    out.close();
                    in.close();
//                    第一种加水印的方法
//                    textMarkProcessor.createWaterMarkByText(inifile, tenantname, resultFile, 0);
//                    String base64 = textMarkProcessor.imageToBase64(resultFile);
//                    EditPicture.pressText("C:\\Users\\Administrator\\Desktop\\解码4.jpg", "人相对比商汤", "楷体", Font.BOLD, width / 15, Color.blue, width / 2, height / 2, 0.5f);
//                    第二种加水印的方法
                    editPicture.pressText(resultFilename, tenantname, "楷体", Font.BOLD, Color.blue, 0.3f);
//                    转base64
                    String base64 = editPicture.imageToBase64(resultFile);
//                    存入结果
                    jsonObject.put(insertime + "_" + filename, base64);
                }
            } catch (IOException e) {
                logger.error("文件下载异常: {}", e.getMessage());
            } finally {
//                inifile.deleteOnExit();
                resultFile.deleteOnExit();
            }
        }
        return jsonObject;
    }

    @Test
    public void demo() {
        Map<Object, Object> map = new HashMap<>();
        map.put("cqdsjbpic4/share/ZHIAN113_ZP/0/200064377640_1575380.jpg", "2018-08-09 23:48:16");
        JSONObject jsonObject = getPicture.downloadAndDecode(map, "厂商名字");
        System.out.println(jsonObject.toJSONString());
    }
}
