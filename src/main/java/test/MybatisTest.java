package test;

import dao.InsertRequestLogDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:BY
 * Date:2020/3/24
 * Description:
 */
public class MybatisTest {

    public static void main(String[] args) {
        Map<Object, Object> map = new HashMap<>();
        map.put("host", null);
        map.put("resourceid", "resourceid");
        map.put("resourcename", "resourcename");
        map.put("projectname", "projectname");
        map.put("requestcode", "requestcode");
        map.put("resultsize", 3);

        Jedis jedis = new Jedis("localhost");
        //1.读取配置文件
        InputStream in = null;
        try {
            in = Resources.getResourceAsStream("mybatis/SqlMapConfig.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.创建SqlSessionFactory工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        SqlSession session = factory.openSession();
        //4.使用SqlSession创建Dao接口的代理对象
        InsertRequestLogDao insertRequestLogDao = session.getMapper(InsertRequestLogDao.class);
        //5.使用代理对象执行方法
        insertRequestLogDao.insertLog(map);
        //6.释放资源
        session.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
