package com.demowebshop.automation.factories.driver;

import com.demowebshop.automation.config.ConfigManager;
import com.demowebshop.automation.enums.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory class for creating and managing WebDriver instances
 * Supports Chrome, Firefox, Edge browsers with local and remote execution
 */
public class WebDriverFactory {
    private static final Logger logger = LogManager.getLogger(WebDriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private WebDriverFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates and returns a WebDriver instance based on browser type
     * @param browserType The browser type to create
     * @return WebDriver instance
     */
    public static WebDriver createDriver(BrowserType browserType) {
        WebDriver driver = null;

        try {
            if (ConfigManager.isRemoteExecution()) {
                driver = createRemoteDriver(browserType);
            } else {
                driver = createLocalDriver(browserType);
            }

            configureDriver(driver);
            driverThreadLocal.set(driver);

            logger.info("Created {} driver successfully", browserType);

        } catch (Exception e) {
            logger.error("Failed to create {} driver: {}", browserType, e.getMessage());
            throw new RuntimeException("WebDriver creation failed", e);
        }

        return driver;
    }

    /**
     * Creates a local WebDriver instance
     * @param browserType Browser type
     * @return WebDriver instance
     */
    private static WebDriver createLocalDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(getChromeOptions());

            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(getFirefoxOptions());

            case EDGE:
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver(getEdgeOptions());

            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    /**
     * Creates a remote WebDriver instance for Selenium Grid
     * @param browserType Browser type
     * @return RemoteWebDriver instance
     */
    private static WebDriver createRemoteDriver(BrowserType browserType) throws MalformedURLException {
        String gridUrl = ConfigManager.getGridUrl();
        URL hubUrl = new URL(gridUrl);

        switch (browserType) {
            case CHROME:
                return new RemoteWebDriver(hubUrl, getChromeOptions());
            case FIREFOX:
                return new RemoteWebDriver(hubUrl, getFirefoxOptions());
            case EDGE:
                return new RemoteWebDriver(hubUrl, getEdgeOptions());
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    /**
     * Configures the WebDriver with common settings
     * @param driver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver driver) {
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigManager.getImplicitTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(ConfigManager.getScriptTimeout()));

        // Maximize window if configured
        if (ConfigManager.shouldMaximizeWindow()) {
            driver.manage().window().maximize();
        }

        // Delete all cookies if configured
        if (ConfigManager.shouldDeleteCookies()) {
            driver.manage().deleteAllCookies();
        }
    }

    /**
     * Gets Chrome options based on configuration
     * @return ChromeOptions
     */
    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        if (ConfigManager.isHeadlessMode()) {
            options.addArguments("--headless=new");
        }

        // Common Chrome arguments for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");

        // Performance optimizations
        options.addArguments("--enable-automation");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        return options;
    }

    /**
     * Gets Firefox options based on configuration
     * @return FirefoxOptions
     */
    private static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();

        if (ConfigManager.isHeadlessMode()) {
            options.addArguments("--headless");
        }

        // Common Firefox preferences
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.navigator.permission.disabled", true);

        return options;
    }

    /**
     * Gets Edge options based on configuration
     * @return EdgeOptions
     */
    private static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();

        if (ConfigManager.isHeadlessMode()) {
            options.addArguments("--headless");
        }

        // Common Edge arguments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");

        return options;
    }

    /**
     * Gets the current thread's WebDriver instance
     * @return WebDriver instance or null if not set
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quits the current thread's WebDriver instance and removes it from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Closes the current browser window
     */
    public static void closeDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.close();
                logger.info("WebDriver closed successfully");
            } catch (Exception e) {
                logger.error("Error while closing WebDriver: {}", e.getMessage());
            }
        }
    }
}