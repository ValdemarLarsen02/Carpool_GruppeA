package app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class ConfigLoader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Kunne ikke finde vores properties");
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Fejl ved load af config: " + e.getMessage());
        }
    }


    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}