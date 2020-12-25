package filter;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.JRedisUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private JRedisUtil jRedisUtil;

    @Override
    public void init(FilterConfig filterConfig) {
        String xmlPath = "applicationContext.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
        jRedisUtil = applicationContext.getBean("jRedisUtil", JRedisUtil.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String accessKey = null;
        String resourceid;
        String requestcode = null;
        String tenantname = null;
        String tenantid;
        String resourcename = null;
        String host;
        //        tomcat启动需要添加参数 -Dfile.encoding=UTF-8
        //处理登录权限信息,判断用户传入的资源是否有权限
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        //        获取body
        String body = HttpHelper.getBodyString(requestWrapper);
        ServletOutputStream out = response.getOutputStream();
        try {
            accessKey = request.getHeader("accesskey");
            host = request.getHeader("host");
            logger.info("过滤器解析传入参数 host: {} , accesskey: {} , body: {}", host, accessKey, body);
            JSONObject requestJson = JSONObject.parseObject(body);
            resourceid = requestJson.getString("resourceid");
            requestcode = requestJson.getString("requestcode");
            tenantname = jRedisUtil.hgetKey(accessKey, "tenantname").toString();
            tenantid = jRedisUtil.hgetKey(accessKey, "tenantid").toString();
            resourcename = jRedisUtil.hgetKey(tenantid + "|" + resourceid, "resourcename").toString();
            logger.info("用户验证成功^_^ , 用户项目名为: {} ,请求资源名为: {}", tenantname, resourcename);
            filterChain.doFilter(requestWrapper, servletResponse);
        } catch (NullPointerException e) {
            logger.error("资源权限验证失败 ! ! ! 解析参数 accessKey: {} , 项目名tenantname: {} , 资源名resourcename : {} , 请求代码requestcode : {}", accessKey, tenantname, resourcename, requestcode);
            out.write(("资源权限验证失败 ! ! ! 解析参数 accessKey: " + accessKey + " 项目名: " + tenantname + ", 资源名: " + resourcename + " ,请求代码 : " + requestcode).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void destroy() {
    }
}
