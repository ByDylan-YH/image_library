package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertysUtils {

    private static Properties properties;

    static {
        properties = new Properties();
        InputStream resourceAsStream = PropertysUtils.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getPropertyAsStr(String key) {
        return String.valueOf(properties.getProperty(key));
    }

    public static Integer getPropertyAsInt(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }


}
