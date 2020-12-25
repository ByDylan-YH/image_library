package test;

import controller.GetPictureController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Author:BY
 * Date:2020/3/26
 * Description:
 */
public class SpringTest {
    public static void main(String[] args) {
        String xmlPath = "applicationContext.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
        GetPictureController secUserController = applicationContext.getBean("getPictureController", GetPictureController.class);
    }
}
