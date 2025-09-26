package com.demowebshop.automation.config;

import com.codeborne.selenide.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Selenide configuration class
 * Configures Selenide settings based on framework properties
 */
public class SelenideConfig {
    private static final Logger logger = LogManager.getLogger(SelenideConfig.class);

    /**
     * Configure Selenide with framework settings
     */
    public static void configureSelenice() {
        try {
            // Set timeout for element operations (milliseconds) - increased for stability
            Configuration.timeout = Math.max(ConfigManager.getExplicitTimeout() * 1000, 15000);

            // Set polling interval (milliseconds) - reduced for faster detection
            Configuration.pollingInterval = 200;

            // Set browser size
            Configuration.browserSize = "1920x1080";

            // Disable automatic browser opening (we manage it via WebDriverFactory)
            Configuration.holdBrowserOpen = false;
            Configuration.reopenBrowserOnFail = true;

            // Enable reports
            Configuration.reportsFolder = "target/selenide-reports";
            Configuration.savePageSource = true;

            // Force headless mode for stability in parallel execution
            Configuration.headless = true;

            // Set page load timeout
            Configuration.pageLoadTimeout = ConfigManager.getPageLoadTimeout() * 1000;

            // Set browser based on configuration
            String browserName = ConfigManager.getDefaultBrowser().toLowerCase();
            Configuration.browser = browserName;

            logger.info("Selenide configured successfully with timeout: {}ms, browser: {}, headless: {}",
                       Configuration.timeout, Configuration.browser, Configuration.headless);

        } catch (Exception e) {
            logger.error("Failed to configure Selenide: {}", e.getMessage(), e);
            throw new RuntimeException("Selenide configuration failed", e);
        }
    }

    /**
     * Update Selenide timeout dynamically
     * @param timeoutSeconds Timeout in seconds
     */
    public static void setTimeout(long timeoutSeconds) {
        Configuration.timeout = timeoutSeconds * 1000;
        logger.debug("Updated Selenide timeout to: {}ms", Configuration.timeout);
    }

    /**
     * Update Selenide polling interval
     * @param intervalMillis Polling interval in milliseconds
     */
    public static void setPollingInterval(long intervalMillis) {
        Configuration.pollingInterval = intervalMillis;
        logger.debug("Updated Selenide polling interval to: {}ms", Configuration.pollingInterval);
    }

    /**
     * Enable or disable Selenide reports
     * @param enabled true to enable reports
     */
    public static void setReportsEnabled(boolean enabled) {
        Configuration.savePageSource = enabled;
        logger.debug("Selenide reports enabled: {}", enabled);
    }

    /**
     * Set browser size for Selenide
     * @param browserSize Browser size (e.g., "1920x1080")
     */
    public static void setBrowserSize(String browserSize) {
        Configuration.browserSize = browserSize;
        logger.debug("Updated Selenide browser size to: {}", browserSize);
    }

    /**
     * Get current Selenide timeout in milliseconds
     * @return Timeout in milliseconds
     */
    public static long getCurrentTimeout() {
        return Configuration.timeout;
    }

    /**
     * Get current Selenide polling interval
     * @return Polling interval in milliseconds
     */
    public static long getCurrentPollingInterval() {
        return Configuration.pollingInterval;
    }

    /**
     * Check if Selenide is in headless mode
     * @return true if headless mode is enabled
     */
    public static boolean isHeadless() {
        return Configuration.headless;
    }

    /**
     * Get current browser configuration
     * @return Browser name
     */
    public static String getCurrentBrowser() {
        return Configuration.browser;
    }

    /**
     * Get Selenide reports folder
     * @return Reports folder path
     */
    public static String getReportsFolder() {
        return Configuration.reportsFolder;
    }
}