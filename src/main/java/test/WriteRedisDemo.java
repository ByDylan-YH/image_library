package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.Map;
import java.util.Set;

/**
 * Author:BY
 * Date:2020/3/24
 * Description:
 * reids启动
 * redis-cli.exe -h 127.0.0.1 -p 6379
 */
public class WriteRedisDemo {
    private static final Jedis jedis = new Jedis("77.1.37.197", 6379);
//    private static final Jedis jedis = new Jedis();
    private static final Logger logger = LoggerFactory.getLogger(WriteRedisDemo.class);
    private static Connection connection;

    static {
        try {
            jedis.auth("cqdsjb");
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/daas?useSSL=false&characterEncoding=UTF-8", "root", "By921644606");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        logger.info("Redis运行状态 {}", jedis.ping());
        WriteRedisDemo redisDemo = new WriteRedisDemo();
//        String sql = "select * from daas_user_key;";
//        String sql = "select * from daas_user_resource;";
        String sql = "select * from daas_resource_api;";
        redisDemo.hsetKey(sql);
        logger.info("共有键值: {} 个", jedis.keys("*").size());
//        System.out.println(redisDemo.hgetAll("NDS8AI3C9OKW2D464C01"));
        System.out.println(redisDemo.hgetAll("0f1b657a-f76b-4ccf-8192-7b627f64df89|778ef5fe-f13e-4c21-9ef8-6af1e9e25338"));
    }

    private void hsetKey(String sql) {
        ResultSet res = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            while (res.next()) {
//                daas_user_key
//                String tenantid = res.getString("tenantid");
//                String tenantname = res.getString("tenantname");
//                String accesskey = res.getString("accesskey");
//                String secretkey = res.getString("secretkey");
//                logger.info("tenantid: {} , tenantname: {} , accesskey: {} , secretkey: {}", tenantid, tenantname, accesskey, secretkey);
//                jedis.hset(accesskey, "tenantid", tenantid);
//                jedis.hset(accesskey, "tenantname", tenantname);
//                jedis.hset(accesskey, "secretkey", secretkey);

//                daas_user_resource
//                String tenantid = res.getString("tenantid");
//                String resourceid = res.getString("resourceid");
//                String tenantname = res.getString("tenantname");
//                String resourcename = res.getString("resourcename");
//                logger.info("tenantid: {} , tenantname: {} , resourceid: {} , resourcename: {}", tenantid, tenantname, resourceid, resourcename);
//                jedis.hset(tenantid + "|" + resourceid, "tenantname", tenantname);
//                jedis.hset(tenantid + "|" + resourceid, "resourcename", resourcename);
//                daas_resource_api

                String resourceid = res.getString("resourceid");
                String resourceapicode = res.getString("resourceapicode");
                String resourceapirequestfield = res.getString("resourceapirequestfield");
                String resourceapiresultfield = res.getString("resourceapiresultfield");
                String pictureapicode = res.getString("pictureapicode");
                String pictureapirequestfield = res.getString("pictureapirequestfield");
                String pictureapitimefield = res.getString("pictureapitimefield");
                String pictureapiresultfield = res.getString("pictureapiresultfield");
                logger.info("resourceid: {} , resourceapicode: {} , resourceapirequestfield: {} , resourceapiresultfield: {} , pictureapicode: {} , pictureapirequestfield: {} , pictureapitimefield: {} , pictureapiresultfield: {}", resourceid, resourceapicode, resourceapirequestfield, resourceapiresultfield, pictureapicode, pictureapirequestfield, pictureapitimefield, pictureapiresultfield);
                jedis.hset(resourceid, "resourceapicode", resourceapicode);
                jedis.hset(resourceid, "resourceapirequestfield", resourceapirequestfield);
                jedis.hset(resourceid, "resourceapiresultfield", resourceapiresultfield);
                jedis.hset(resourceid, "pictureapicode", pictureapicode);
                jedis.hset(resourceid, "pictureapirequestfield", pictureapirequestfield);
                jedis.hset(resourceid, "pictureapitimefield", pictureapitimefield);
                jedis.hset(resourceid, "pictureapiresultfield", pictureapiresultfield);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                res.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setKey(String sql) {
        ResultSet res = null;
        Statement statement = null;
        Set<String> set;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            while (res.next()) {
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                res.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String hgetKey(String key, String field) {
        return jedis.hget(key, field);
    }

    private Map<String, String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }
}
