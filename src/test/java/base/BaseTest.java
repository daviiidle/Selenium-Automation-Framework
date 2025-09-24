package base;

import config.ConfigurationManager;
import drivers.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.HomePage;
import utils.ScreenshotUtils;

import java.lang.reflect.Method;

public abstract class BaseTest {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected ConfigurationManager config;
    protected HomePage homePage;

    @BeforeMethod
    public void setUp(Method method) {
        logger.info("Starting test: {}.{}", this.getClass().getSimpleName(), method.getName());

        try {
            config = ConfigurationManager.getInstance();
            String browserName = config.getBrowser();
            logger.info("Using browser: {} in headless mode: {}", browserName, config.isHeadless());

            WebDriverFactory.setDriver(browserName);
            driver = WebDriverFactory.getDriver();

            if (driver == null) {
                throw new RuntimeException("WebDriver initialization failed - driver is null");
            }
            logger.info("WebDriver initialized successfully");

            homePage = new HomePage(driver);
            logger.info("HomePage object created");

            homePage.navigateToHomePage();
            logger.info("Navigated to homepage");

            logger.info("Test setup completed for: {}", method.getName());
        } catch (Exception e) {
            logger.error("Error during test setup for: {}", method.getName(), e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod
    public void tearDown(Method method) {
        logger.info("Cleaning up test: {}.{}", this.getClass().getSimpleName(), method.getName());

        try {
            if (driver != null) {
                // Take screenshot on failure
                if (!isTestPassed()) {
                    String screenshotName = this.getClass().getSimpleName() + "_" + method.getName() + "_failed";
                    ScreenshotUtils.captureScreenshot(driver, screenshotName);
                }
            }
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage());
        } finally {
            WebDriverFactory.quitDriver();
            logger.info("Test cleanup completed for: {}", method.getName());
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
        ScreenshotUtils.captureScreenshot(driver, testName);
    }

    // Method to check if test passed (to be overridden by test listeners)
    private boolean isTestPassed() {
        // This will be handled by TestNG listeners
        return true;
    }
}