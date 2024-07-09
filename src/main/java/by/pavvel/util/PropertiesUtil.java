package by.pavvel.util;

import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    private static final String PROPERTIES_FILE = "application.properties";

    static {
        loadProperties();
    }

    private PropertiesUtil() {}

    public static String getProperties(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static void setProperties(String key, String value) {
        PROPERTIES.setProperty(key, value);
    }

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            PROPERTIES.load(inputStream);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }
}
