package config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final Properties properties;
    private final Dotenv dotenv;

    private ConfigurationManager() {
        this.properties = new Properties();
        this.dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        loadProperties();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private void loadProperties() {
        String environment = getEnvironment();
        String propertiesFile = String.format("config/%s.properties", environment);

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input != null) {
                properties.load(input);
                logger.info("Loaded configuration for environment: {}", environment);
            } else {
                logger.warn("Configuration file not found: {}", propertiesFile);
                loadDefaultProperties();
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", propertiesFile, e);
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        properties.setProperty("base.url", "https://demowebshop.tricentis.com");
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "true");
        properties.setProperty("timeout.implicit", "10");
        properties.setProperty("timeout.explicit", "20");
        properties.setProperty("timeout.page.load", "30");
        properties.setProperty("parallel.threads", "3");
        properties.setProperty("screenshot.on.failure", "true");
        logger.info("Loaded default configuration properties");
    }

    public String getProperty(String key) {
        // Priority: Environment variable > .env file > properties file > system property
        String value = dotenv.get(key.toUpperCase().replace(".", "_"));
        if (value != null) {
            return value;
        }

        value = properties.getProperty(key);
        if (value != null) {
            return value;
        }

        return System.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property '{}': {}", key, getProperty(key));
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    // Configuration getters
    public String getBaseUrl() {
        return getProperty("base.url", "https://demowebshop.tricentis.com");
    }

    public String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    public boolean isHeadless() {
        return getBooleanProperty("headless", false);
    }

    public int getImplicitTimeout() {
        return getIntProperty("timeout.implicit", 10);
    }

    public int getExplicitTimeout() {
        return getIntProperty("timeout.explicit", 20);
    }

    public int getPageLoadTimeout() {
        return getIntProperty("timeout.page.load", 30);
    }

    public int getParallelThreads() {
        return getIntProperty("parallel.threads", 3);
    }

    public boolean isScreenshotOnFailure() {
        return getBooleanProperty("screenshot.on.failure", true);
    }

    public String getEnvironment() {
        return getProperty("environment", "dev").toLowerCase();
    }

    public String getReportsPath() {
        return getProperty("reports.path", "target/reports");
    }

    public String getScreenshotsPath() {
        return getProperty("screenshots.path", "target/screenshots");
    }

    public String getLogsPath() {
        return getProperty("logs.path", "target/logs");
    }
}