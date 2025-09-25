package com.demowebshop.automation.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for handling application properties and environment variables
 */
public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static Properties properties;
    private static Dotenv dotenv;

    static {
        loadConfiguration();
    }

    private ConfigManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Load configuration from properties file and .env file
     */
    private static void loadConfiguration() {
        properties = new Properties();

        try {
            // Load environment-specific properties
            String environment = System.getProperty("environment", "demo");
            String configFile = "/config/environments/" + environment + ".properties";

            InputStream inputStream = ConfigManager.class.getResourceAsStream(configFile);
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Loaded configuration from {}", configFile);
            } else {
                logger.warn("Configuration file not found: {}", configFile);
            }

            // Load .env file if available
            try {
                dotenv = Dotenv.configure()
                        .directory(".")
                        .ignoreIfMalformed()
                        .ignoreIfMissing()
                        .load();
                logger.info("Loaded .env configuration");
            } catch (Exception e) {
                logger.warn("Could not load .env file: {}", e.getMessage());
            }

        } catch (IOException e) {
            logger.error("Error loading configuration: {}", e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Get property value with system property override and .env fallback
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        // Priority: System Property > .env > properties file
        String value = System.getProperty(key);

        if (value == null && dotenv != null) {
            value = dotenv.get(key.toUpperCase().replace(".", "_"));
        }

        if (value == null) {
            value = properties.getProperty(key);
        }

        return value;
    }

    /**
     * Get property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get boolean property value
     * @param key Property key
     * @return Boolean value
     */
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key, "false"));
    }

    /**
     * Get integer property value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Integer value
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer property value for key '{}', using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    // Application Configuration Methods
    public static String getBaseUrl() {
        return getProperty("base.url", "https://demowebshop.tricentis.com");
    }

    public static String getApiBaseUrl() {
        return getProperty("api.base.url", "https://demowebshop.tricentis.com/api");
    }

    public static String getEnvironmentName() {
        return getProperty("environment.name", "demo");
    }

    // Browser Configuration Methods
    public static String getDefaultBrowser() {
        return getProperty("browser.default", "chrome");
    }

    public static boolean isHeadlessMode() {
        return getBooleanProperty("browser.headless");
    }

    public static boolean shouldMaximizeWindow() {
        return getBooleanProperty("browser.maximize");
    }

    public static boolean shouldDeleteCookies() {
        return getBooleanProperty("browser.delete.cookies");
    }

    // WebDriver Configuration Methods
    public static boolean isRemoteExecution() {
        return getBooleanProperty("webdriver.remote");
    }

    public static String getGridUrl() {
        return getProperty("webdriver.grid.url", "http://localhost:4444/wd/hub");
    }

    // Timeout Configuration Methods
    public static int getImplicitTimeout() {
        return getIntProperty("timeout.implicit", 10);
    }

    public static int getExplicitTimeout() {
        return getIntProperty("timeout.explicit", 15);
    }

    public static int getPageLoadTimeout() {
        return getIntProperty("timeout.page.load", 30);
    }

    public static int getScriptTimeout() {
        return getIntProperty("timeout.script", 20);
    }

    // Test Data Configuration Methods
    public static boolean shouldGenerateUniqueUsers() {
        return getBooleanProperty("test.data.generate.unique.users");
    }

    public static boolean shouldCleanupAfterTest() {
        return getBooleanProperty("test.data.cleanup.after.test");
    }

    public static String getFakerLocale() {
        return getProperty("test.data.faker.locale", "en");
    }

    // Reporting Configuration Methods
    public static boolean isExtentReportEnabled() {
        return getBooleanProperty("report.extent.enabled");
    }

    public static boolean isAllureReportEnabled() {
        return getBooleanProperty("report.allure.enabled");
    }

    public static boolean shouldTakeScreenshotOnFailure() {
        return getBooleanProperty("report.screenshots.on.failure");
    }

    public static boolean shouldTakeScreenshotOnPass() {
        return getBooleanProperty("report.screenshots.on.pass");
    }

    // Retry Configuration Methods
    public static boolean shouldRetryFailedTests() {
        return getBooleanProperty("retry.failed.tests");
    }

    public static int getRetryCount() {
        return getIntProperty("retry.count", 1);
    }

    /**
     * Reload configuration (useful for testing)
     */
    public static void reloadConfiguration() {
        loadConfiguration();
    }
}