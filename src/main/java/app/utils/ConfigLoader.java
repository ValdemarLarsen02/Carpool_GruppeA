package app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties"); // Samler essensen fra begge fejlmeddelelser
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage()); // Log en fejl i stedet for at smide en exception
            throw new RuntimeException("Critical configuration loading error", e); // Eskaler hvis nødvendigt
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
