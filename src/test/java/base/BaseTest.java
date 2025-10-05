package base;

import config.ConfigurationManager;
import com.demowebshop.automation.factories.driver.WebDriverFactory;
import com.demowebshop.automation.enums.BrowserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.utils.reporting.ScreenshotUtils;
import com.demowebshop.automation.config.SelenideConfig;
import com.codeborne.selenide.Configuration;
import listeners.RetryAnalyzer;

import java.lang.reflect.Method;

public abstract class BaseTest {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected ConfigurationManager config;
    protected HomePage homePage;

    @BeforeMethod
    public void setUp(Method method) {
        logger.info("Starting test: {}.{}", this.getClass().getSimpleName(), method.getName());

        int retryCount = 0;
        int maxRetries = 3;
        Exception lastException = null;

        while (retryCount < maxRetries) {
            try {
                config = ConfigurationManager.getInstance();
                String browserName = config.getBrowser();
                logger.info("Using browser: {} in headless mode: {} (attempt {})", browserName, config.isHeadless(), retryCount + 1);

                BrowserType browserType = BrowserType.fromString(browserName);
                driver = WebDriverFactory.createDriver(browserType);

                if (driver == null) {
                    throw new RuntimeException("WebDriver initialization failed - driver is null");
                }
                logger.info("WebDriver initialized successfully");

                homePage = new HomePage(driver);
                logger.info("HomePage object created");

                // Add retry for navigation as well
                try {
                    homePage.navigateToHomePage();
                    logger.info("Navigated to homepage successfully");
                } catch (Exception navException) {
                    logger.warn("Navigation failed on attempt {}: {}", retryCount + 1, navException.getMessage());
                    if (retryCount == maxRetries - 1) {
                        throw navException;
                    }
                    // Clean up and retry
                    if (driver != null) {
                        try { driver.quit(); } catch (Exception ignored) {}
                    }
                    Thread.sleep(2000); // Wait before retry
                    retryCount++;
                    continue;
                }

                // Call additional setup hook for test classes
                additionalSetup();

                logger.info("Test setup completed for: {}", method.getName());
                return; // Success, exit retry loop

            } catch (Exception e) {
                lastException = e;
                logger.warn("Test setup failed on attempt {} for: {} - {}", retryCount + 1, method.getName(), e.getMessage());

                // Clean up driver before retry
                if (driver != null) {
                    try { WebDriverFactory.quitDriver(); } catch (Exception ignored) {}
                    driver = null;
                }

                retryCount++;

                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(3000); // Wait between retries
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        // If all retries failed, throw the last exception
        logger.error("Test setup failed after {} attempts for: {}", maxRetries, method.getName(), lastException);
        throw new RuntimeException("Test setup failed after " + maxRetries + " attempts", lastException);
    }

    @AfterMethod
    public void tearDown(Method method) {
        logger.info("Cleaning up test: {}.{}", this.getClass().getSimpleName(), method.getName());

        try {
            // Call additional teardown hook for test classes
            additionalTeardown();

            if (driver != null) {
                try {
                    // Take screenshot on failure - check if driver is still valid
                    if (!isTestPassed() && isDriverValid()) {
                        String screenshotName = this.getClass().getSimpleName() + "_" + method.getName() + "_failed";
                        ScreenshotUtils.takeScreenshot(driver, screenshotName);
                    }
                } catch (Exception screenshotException) {
                    logger.warn("Could not take screenshot during teardown: {}", screenshotException.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage());
        } finally {
            // Safe driver cleanup
            try {
                if (driver != null) {
                    WebDriverFactory.quitDriver();
                }
            } catch (Exception driverException) {
                logger.warn("Error closing driver: {}", driverException.getMessage());
            }
            driver = null; // Ensure driver reference is cleared
            logger.info("Test cleanup completed for: {}", method.getName());
        }
    }

    /**
     * Check if driver is still valid and can be used
     * @return true if driver is valid
     */
    private boolean isDriverValid() {
        try {
            if (driver == null) {
                return false;
            }
            // Try to get current URL to verify driver is still active
            driver.getCurrentUrl();
            return true;
        } catch (Exception e) {
            logger.debug("Driver is no longer valid: {}", e.getMessage());
            return false;
        }
    }

    @BeforeClass
    public void beforeClass() {
        logger.info("Starting test class: {}", this.getClass().getSimpleName());
    }

    @AfterClass
    public void afterClass() {
        logger.info("Completed test class: {}", this.getClass().getSimpleName());
    }

    @BeforeSuite
    public void beforeSuite() {
        logger.info("Starting test suite execution");

        // Force headless mode at system level
        System.setProperty("selenide.headless", "true");
        System.setProperty("headless", "true");
        System.setProperty("browser.headless", "true");
        System.setProperty("chrome.switches", "--headless=new --no-sandbox --disable-dev-shm-usage");

        // Initialize Selenide with headless configuration
        Configuration.headless = true;
        Configuration.browser = "chrome";
        SelenideConfig.configureSelenice();

        logger.info("Forced headless mode via system properties and Selenide configuration");
    }

    @AfterSuite
    public void afterSuite() {
        logger.info("Completed test suite execution");
    }

    // Utility methods for derived test classes
    protected void navigateToHomePage() {
        homePage.navigateToHomePage();
    }

    protected void takeScreenshot(String testName) {
        ScreenshotUtils.takeScreenshot(driver, testName);
    }

    // Method to check if test passed (to be overridden by test listeners)
    private boolean isTestPassed() {
        // This will be handled by TestNG listeners
        return true;
    }

    /**
     * Get the base URL from configuration
     * @return Base URL
     */
    protected String getBaseUrl() {
        return config.getBaseUrl();
    }

    /**
     * Wait for a specified number of seconds
     * @param seconds Seconds to wait
     */
    protected void waitInSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
            logger.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            logger.warn("Wait interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Navigate to a specific path relative to base URL
     * @param path Relative path to navigate to
     */
    protected void navigateToPath(String path) {
        String fullUrl = getBaseUrl() + path;
        logger.info("Navigating to: {}", fullUrl);
        driver.get(fullUrl);
    }

    /**
     * Verify current page URL contains expected path
     * @param expectedPath Expected path in URL
     * @return true if URL contains expected path
     */
    protected boolean isOnPage(String expectedPath) {
        String currentUrl = driver.getCurrentUrl();
        boolean isOnPage = currentUrl.contains(expectedPath);
        logger.debug("Current URL: {}, Expected path: {}, IsOnPage: {}", currentUrl, expectedPath, isOnPage);
        return isOnPage;
    }

    /**
     * Hook method for test classes to perform additional setup
     * Override this method in test classes to add custom setup logic
     */
    protected void additionalSetup() {
        // Override in test classes if additional setup is needed
        logger.debug("Running additionalSetup hook");
    }

    /**
     * Hook method for test classes to perform additional teardown
     * Override this method in test classes to add custom teardown logic
     */
    protected void additionalTeardown() {
        // Override in test classes if additional teardown is needed
        logger.debug("Running additionalTeardown hook");
    }

}