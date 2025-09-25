package com.demowebshop.automation.base;

import com.demowebshop.automation.config.ConfigManager;
import com.demowebshop.automation.enums.BrowserType;
import com.demowebshop.automation.factories.driver.WebDriverFactory;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.utils.reporting.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Base test class providing common setup and teardown functionality
 * All test classes should extend this class
 */
public abstract class BaseTest {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected HomePage homePage;

    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browserName, Method method) {
        logger.info("=== Starting test: {} ===", method.getName());
        logger.info("Test class: {}", this.getClass().getSimpleName());

        try {
            // Initialize WebDriver
            BrowserType browserType = BrowserType.fromString(browserName);
            driver = WebDriverFactory.createDriver(browserType);

            logger.info("WebDriver initialized successfully with browser: {}", browserType);

            // Initialize HomePage
            homePage = new HomePage(driver);
            logger.info("HomePage object created");

            // Navigate to base URL
            driver.get(ConfigManager.getBaseUrl());
            logger.info("Navigated to base URL: {}", ConfigManager.getBaseUrl());

        } catch (Exception e) {
            logger.error("Failed to set up test environment: {}", e.getMessage(), e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        try {
            // Take screenshot based on test result and configuration
            if (shouldTakeScreenshot(result)) {
                String screenshotPath = ScreenshotUtils.takeScreenshot(driver, testName);
                logger.info("Screenshot saved: {}", screenshotPath);
            }

            // Log test result
            logTestResult(result, testName);

        } catch (Exception e) {
            logger.error("Error during test teardown: {}", e.getMessage(), e);
        } finally {
            // Always quit the WebDriver
            if (driver != null) {
                WebDriverFactory.quitDriver();
                logger.info("WebDriver quit successfully");
            }
        }

        logger.info("=== Finished test: {} ===", testName);
    }

    /**
     * Determines if screenshot should be taken based on test result and configuration
     * @param result Test result
     * @return true if screenshot should be taken
     */
    private boolean shouldTakeScreenshot(ITestResult result) {
        return (result.getStatus() == ITestResult.FAILURE && ConfigManager.shouldTakeScreenshotOnFailure()) ||
               (result.getStatus() == ITestResult.SUCCESS && ConfigManager.shouldTakeScreenshotOnPass()) ||
               (result.getStatus() == ITestResult.SKIP);
    }

    /**
     * Log test result with appropriate level
     * @param result Test result
     * @param testName Test name
     */
    private void logTestResult(ITestResult result, String testName) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                logger.info("✅ Test PASSED: {}", testName);
                break;
            case ITestResult.FAILURE:
                logger.error("❌ Test FAILED: {} - Reason: {}", testName, result.getThrowable().getMessage());
                break;
            case ITestResult.SKIP:
                logger.warn("⏭️  Test SKIPPED: {} - Reason: {}", testName,
                        result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
                break;
            default:
                logger.warn("❓ Test result UNKNOWN: {}", testName);
        }
    }

    // Utility methods for test classes
    /**
     * Get current WebDriver instance
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Navigate to a specific path relative to base URL
     * @param path Path to navigate to
     */
    protected void navigateToPath(String path) {
        String fullUrl = ConfigManager.getBaseUrl() + path;
        logger.info("Navigating to: {}", fullUrl);
        driver.get(fullUrl);
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
     * Get base URL from configuration
     * @return Base URL
     */
    protected String getBaseUrl() {
        return ConfigManager.getBaseUrl();
    }

    /**
     * Verify current page URL contains expected path
     * @param expectedPath Expected path in URL
     * @return true if URL contains expected path
     */
    protected boolean isOnPage(String expectedPath) {
        String currentUrl = driver.getCurrentUrl();
        boolean isOnPage = currentUrl.contains(expectedPath);
        logger.debug("Current URL: {}, Expected path: {}, On page: {}", currentUrl, expectedPath, isOnPage);
        return isOnPage;
    }

    /**
     * Abstract method for test-specific setup
     * Override this method in test classes if additional setup is needed
     */
    protected void additionalSetup() {
        // Default implementation does nothing
        // Override in test classes if needed
    }

    /**
     * Abstract method for test-specific cleanup
     * Override this method in test classes if additional cleanup is needed
     */
    protected void additionalTeardown() {
        // Default implementation does nothing
        // Override in test classes if needed
    }
}