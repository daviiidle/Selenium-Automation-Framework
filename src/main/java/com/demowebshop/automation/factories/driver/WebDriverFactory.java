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
import com.codeborne.selenide.WebDriverRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory class for creating and managing WebDriver instances
 * Supports Chrome, Firefox, Edge browsers with local and remote execution
 * Integrated with Selenide for enhanced test automation capabilities
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

            // Set the WebDriver instance for Selenide
            WebDriverRunner.setWebDriver(driver);

            logger.info("Created {} driver successfully and configured for Selenide", browserType);

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

        // Skip window maximization in headless mode to prevent errors
        logger.debug("Skipping window maximization in headless mode");

        // Delete all cookies if configured
        if (ConfigManager.shouldDeleteCookies()) {
            try {
                driver.manage().deleteAllCookies();
            } catch (Exception e) {
                logger.warn("Failed to delete cookies, continuing: {}", e.getMessage());
            }
        }
    }

    /**
     * Gets Chrome options based on configuration
     * @return ChromeOptions
     */
    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Force headless mode for stability in parallel execution
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");

        // Essential Chrome arguments for headless stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-debugging-port=0");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
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

        // Force headless mode for stability in parallel execution
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");

        // Common Firefox preferences for speed
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.navigator.permission.disabled", true);
        options.addPreference("dom.disable_open_during_load", false);
        options.addPreference("dom.max_script_run_time", 0);
        options.addPreference("dom.min_background_timeout_value", 4);
        options.addPreference("network.http.pipelining", true);
        options.addPreference("network.http.proxy.pipelining", true);
        options.addPreference("network.http.pipelining.maxrequests", 10);
        options.addPreference("nglayout.initialpaint.delay", 0);

        // POWERHOUSE MODE: Ultra-aggressive Firefox optimizations
        options.addPreference("content.notify.interval", 500000);
        options.addPreference("content.notify.ontimer", true);
        options.addPreference("content.switch.threshold", 500000);
        options.addPreference("browser.cache.memory.capacity", 65536);
        options.addPreference("browser.startup.homepage", "about:blank");
        options.addPreference("browser.display.show_image_placeholders", false);
        options.addPreference("browser.turbo.enabled", true);
        options.addPreference("dom.disable_window_status_change", true);
        options.addPreference("dom.disable_window_move_resize", true);
        options.addPreference("network.http.max-connections", 48);
        options.addPreference("network.http.max-connections-per-server", 16);
        options.addPreference("network.http.max-persistent-connections-per-server", 8);
        options.addPreference("content.interrupt.parsing", true);
        options.addPreference("content.max.tokenizing.time", 2250000);
        options.addPreference("content.switch.threshold", 750000);

        return options;
    }

    /**
     * Gets Edge options based on configuration
     * @return EdgeOptions
     */
    private static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();

        // Force headless mode for stability in parallel execution
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");

        // Common Edge arguments for speed
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-logging");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-renderer-backgrounding");

        // POWERHOUSE MODE: Ultra-aggressive Edge optimizations
        options.addArguments("--memory-pressure-off");
        options.addArguments("--max_old_space_size=4096");
        options.addArguments("--no-first-run");
        options.addArguments("--disable-background-mode");
        options.addArguments("--disable-translate");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-client-side-phishing-detection");
        options.addArguments("--disable-permissions-api");
        options.addArguments("--fast-start");
        options.addArguments("--aggressive");
        options.addArguments("--disable-domain-reliability");
        options.addArguments("--renderer-process-limit=1");

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
     * Selenide-aware cleanup to prevent double cleanup issues
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                // Skip window operations that may fail in headless/crashed browsers
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.warn("WebDriver quit with error (normal for crashed sessions): {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
                // Clear Selenide's WebDriver reference safely
                try {
                    WebDriverRunner.closeWebDriver();
                } catch (Exception e) {
                    logger.debug("Selenide WebDriver already cleaned up: {}", e.getMessage());
                }
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