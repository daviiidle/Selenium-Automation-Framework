package com.demowebshop.automation.factories.driver;

import com.demowebshop.automation.config.ConfigManager;
import com.demowebshop.automation.enums.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.SessionNotCreatedException;
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
        boolean preferNewHeadless = ConfigManager.isHeadlessMode() && shouldUseNewHeadlessMode();
        WebDriver driver = null;
        try {
            driver = createAndRegisterDriver(browserType, preferNewHeadless);
            logger.info("Created {} driver successfully and configured for Selenide", browserType);
        } catch (SessionNotCreatedException sessionException) {
            if (shouldRetryWithLegacyHeadless(browserType, preferNewHeadless, sessionException)) {
                logger.warn("Retrying {} driver creation with legacy headless mode due to session creation failure: {}",
                        browserType, sessionException.getMessage());
                try {
                    driver = createAndRegisterDriver(browserType, false);
                } catch (SessionNotCreatedException fallbackException) {
                    logger.error("Legacy headless fallback failed for {}: {}", browserType, fallbackException.getMessage());
                    throw new RuntimeException("WebDriver creation failed after legacy headless fallback", fallbackException);
                }
                logger.info("Created {} driver successfully using legacy headless fallback", browserType);
            } else {
                logger.error("Failed to create {} driver: {}", browserType, sessionException.getMessage());
                throw new RuntimeException("WebDriver creation failed", sessionException);
            }

        } catch (Exception e) {
            logger.error("Failed to create {} driver: {}", browserType, e.getMessage());
            throw new RuntimeException("WebDriver creation failed", e);
        }

        return driver;
    }

    private static WebDriver createAndRegisterDriver(BrowserType browserType, boolean useNewHeadless) {
        WebDriver driver;
        if (ConfigManager.isRemoteExecution()) {
            driver = createRemoteDriver(browserType, useNewHeadless);
        } else {
            driver = createLocalDriver(browserType, useNewHeadless);
        }

        configureDriver(driver);
        driverThreadLocal.set(driver);

        // Set the WebDriver instance for Selenide
        WebDriverRunner.setWebDriver(driver);

        return driver;
    }

    /**
     * Creates a local WebDriver instance with extended timeout protection for CI
     * @param browserType Browser type
     * @return WebDriver instance
     */
    private static WebDriver createLocalDriver(BrowserType browserType, boolean useNewHeadless) {
        switch (browserType) {
            case CHROME:
                // Clear cache and setup ChromeDriver
                WebDriverManager.chromedriver().clearDriverCache().setup();
                ChromeOptions chromeOptions = getChromeOptions(useNewHeadless);

                try {
                    // Extended timeout to 180 seconds for CI environment stability
                    logger.debug("Creating ChromeDriver with 180s timeout for CI stability");
                    java.util.concurrent.CompletableFuture<ChromeDriver> driverFuture =
                        java.util.concurrent.CompletableFuture.supplyAsync(() -> new ChromeDriver(chromeOptions));
                    ChromeDriver driver = driverFuture.get(180, java.util.concurrent.TimeUnit.SECONDS);
                    logger.info("ChromeDriver created successfully");
                    return driver;
                } catch (java.util.concurrent.TimeoutException e) {
                    logger.error("ChromeDriver creation timed out after 180 seconds");
                    throw new RuntimeException("ChromeDriver creation timeout - browser may be hanging", e);
                } catch (Exception e) {
                    logger.error("ChromeDriver creation failed: {}", e.getMessage());
                    throw new RuntimeException("ChromeDriver creation failed", e);
                }

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
    private static WebDriver createRemoteDriver(BrowserType browserType, boolean useNewHeadless) {
        String gridUrl = ConfigManager.getGridUrl();
        URL hubUrl;
        try {
            hubUrl = new URL(gridUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Selenium Grid URL: " + gridUrl, e);
        }

        switch (browserType) {
            case CHROME:
                return new RemoteWebDriver(hubUrl, getChromeOptions(useNewHeadless));
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

        if (ConfigManager.shouldMaximizeWindow() && !ConfigManager.isHeadlessMode()) {
            try {
                driver.manage().window().maximize();
            } catch (Exception maximizeException) {
                logger.debug("Window maximization skipped: {}", maximizeException.getMessage());
            }
        } else {
            logger.debug("Skipping window maximization in headless mode");
        }

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
     * Gets Chrome options optimized for CI environment and parallel execution
     * @return ChromeOptions
     */
    private static ChromeOptions getChromeOptions(boolean useNewHeadless) {
        ChromeOptions options = new ChromeOptions();

        // Force legacy headless for maximum CI stability (--headless=new causes timeouts)
        if (ConfigManager.isHeadlessMode()) {
            options.addArguments("--headless");  // Force legacy headless for reliability
            logger.debug("Using legacy headless mode for CI stability");
        }

        // Critical stability flags for CI environment
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer");

        // Window and rendering settings
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");

        // Memory and resource limits for parallel execution stability
        options.addArguments("--max-old-space-size=512");
        options.addArguments("--js-flags=--max-old-space-size=512");
        options.addArguments("--renderer-process-limit=2");
        options.addArguments("--disable-hang-monitor");

        // Reduce resource usage for parallel execution
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        options.addArguments("--metrics-recording-only");
        options.addArguments("--no-first-run");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-backgrounding-occluded-windows");

        // Prevent hanging during session creation
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");
        options.addArguments("--disable-blink-features=AutomationControlled");

        // Logging and debugging controls
        options.addArguments("--log-level=3");
        options.addArguments("--silent");

        // Anti-automation detection (minimal)
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "enable-logging"});

        logger.debug("Created CI-optimized Chrome options with legacy headless and resource limits");
        return options;
    }
    private static boolean shouldUseNewHeadlessMode() {
        String headlessMode = ConfigManager.getProperty("browser.headless.mode", "auto");
        if ("legacy".equalsIgnoreCase(headlessMode)) {
            return false;
        }
        if ("new".equalsIgnoreCase(headlessMode)) {
            return true;
        }
        // Auto mode: prefer new headless unless explicitly disabled via system property
        return !Boolean.getBoolean("browser.headless.legacy");
    }

    private static boolean shouldRetryWithLegacyHeadless(BrowserType browserType,
                                                         boolean triedNewHeadless,
                                                         SessionNotCreatedException exception) {
        if (browserType != BrowserType.CHROME) {
            return false;
        }
        if (!ConfigManager.isHeadlessMode() || !triedNewHeadless) {
            return false;
        }
        String message = exception.getMessage();
        if (message == null) {
            return true;
        }
        String normalized = message.toLowerCase();
        return normalized.contains("chrome not reachable")
                || normalized.contains("unknown error")
                || normalized.contains("devtoolsactiveport");
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
     * Selenide-aware cleanup to prevent double cleanup issues with timeout handling
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                // Use a timeout for quit operation to prevent hanging
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        logger.debug("WebDriver quit threw exception: {}", e.getMessage());
                    }
                }).get(10, java.util.concurrent.TimeUnit.SECONDS);

                logger.info("WebDriver quit successfully");
            } catch (java.util.concurrent.TimeoutException e) {
                logger.warn("WebDriver quit timed out after 10 seconds - forcing cleanup");
                // Force cleanup if quit hangs
                try {
                    if (driver instanceof ChromeDriver) {
                        // For Chrome, try to forcefully close browser processes
                        Runtime.getRuntime().exec("taskkill /f /im chrome.exe /t");
                        Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe /t");
                    }
                } catch (Exception ignored) {
                    // Best effort process cleanup
                }
            } catch (Exception e) {
                logger.warn("WebDriver quit with error (normal for crashed sessions): {}", e.getMessage());
            } finally {
                // Always clean up ThreadLocal and Selenide references
                driverThreadLocal.remove();
                try {
                    WebDriverRunner.closeWebDriver();
                } catch (Exception e) {
                    logger.debug("Selenide WebDriver cleanup: {}", e.getMessage());
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
