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
import java.time.Duration;

public abstract class BaseTest {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected ConfigurationManager config;
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<HomePage> HOME_PAGE = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SETUP_COMPLETED = new ThreadLocal<>();

    @BeforeMethod(timeOut = 600000) // 10 minute timeout to match page load timeout
    public void setUp(Method method) {
        logger.info("Starting test: {}.{}", this.getClass().getSimpleName(), method.getName());

        try {
            // Check if setup already completed for this thread (handles data-driven test iterations)
            if (Boolean.TRUE.equals(SETUP_COMPLETED.get()) && DRIVER.get() != null) {
                logger.debug("WebDriver already initialized for this thread, reusing for data-driven test iteration");
                
                // Verify driver is still valid
                if (isDriverValid()) {
                    // Navigate back to home page for data-driven test iteration
                    try {
                        HomePage homePage = HOME_PAGE.get();
                        if (homePage != null) {
                            homePage.navigateToHomePage();
                            logger.info("Navigated to homepage for new test iteration");
                        }
                    } catch (Exception navEx) {
                        logger.warn("Could not navigate to home page for iteration, will continue: {}", navEx.getMessage());
                    }
                    
                    // Call additional setup hook for test classes
                    additionalSetup();
                    return;
                } else {
                    logger.warn("Driver was initialized but is no longer valid, recreating");
                    tearDownThreadState();
                }
            }

            tearDownThreadState(); // ensure no stale state on reused threads

            config = ConfigurationManager.getInstance();
            String browserName = config.getBrowser();
            logger.info("Using browser: {} in headless mode: {}", browserName, config.isHeadless());

            BrowserType browserType = BrowserType.fromString(browserName);
            
            // Create WebDriver with extended CI timeout handling and retries
            WebDriver driver = null;
            int retryCount = 0;
            int maxRetries = 3; // Increased to 3 attempts
            
            while (driver == null && retryCount < maxRetries) {
                try {
                    logger.info("Creating WebDriver (attempt {}/{})", retryCount + 1, maxRetries);
                    driver = WebDriverFactory.createDriver(browserType);
                    
                    if (driver == null) {
                        throw new RuntimeException("WebDriver initialization failed - driver is null");
                    }
                    
                    logger.info("WebDriver initialized successfully on attempt {}", retryCount + 1);
                    
                    // CRITICAL FIX: Set page load timeout IMMEDIATELY after driver creation
                    // This prevents renderer timeouts during navigation
                    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(600));
                    driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(120));
                    logger.info("Extended timeouts configured: pageLoad=600s, script=120s");
                    
                } catch (Exception driverException) {
                    retryCount++;
                    logger.warn("WebDriver creation attempt {} failed: {}", retryCount, driverException.getMessage());
                    
                    if (retryCount >= maxRetries) {
                        throw new RuntimeException("WebDriver creation failed after " + maxRetries + " attempts", driverException);
                    }
                    
                    // Exponential backoff: 2s, 4s, 8s
                    int waitTime = (int) Math.pow(2, retryCount) * 1000;
                    logger.info("Waiting {}ms before retry...", waitTime);
                    Thread.sleep(waitTime);
                }
            }
            
            DRIVER.set(driver);

            // Ensure Selenide WebDriver is bound for this thread
            com.codeborne.selenide.WebDriverRunner.setWebDriver(driver);

            HomePage homePage = new HomePage(driver);
            HOME_PAGE.set(homePage);
            logger.info("HomePage object created");

            // Navigate with enhanced retry logic for renderer timeouts
            boolean navigationSuccess = false;
            int navAttempts = 0;
            int maxNavAttempts = 3;
            Exception lastNavException = null;
            
            while (!navigationSuccess && navAttempts < maxNavAttempts) {
                navAttempts++;
                try {
                    logger.info("Attempting homepage navigation (attempt {}/{})", navAttempts, maxNavAttempts);
                    
                    // Refresh page load timeout before each navigation attempt
                    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(600));
                    
                    homePage.navigateToHomePage();

                    // Wait for page to be fully loaded with extended timeout for CI
                    int maxWait = 60; // Extended to 60s for CI renderer delays
                    int waited = 0;
                    while (!homePage.isPageLoaded() && waited < maxWait) {
                        Thread.sleep(1000);
                        waited++;
                        
                        if (waited % 10 == 0) {
                            logger.debug("Still waiting for page load... {}s elapsed", waited);
                        }
                    }

                    if (!homePage.isPageLoaded()) {
                        logger.warn("Homepage may not be fully loaded after {}s, but continuing", maxWait);
                    } else {
                        logger.info("Homepage fully loaded and verified");
                    }
                    
                    navigationSuccess = true;
                    logger.info("Navigated to homepage successfully on attempt {}", navAttempts);
                    
                } catch (org.openqa.selenium.TimeoutException timeoutEx) {
                    lastNavException = timeoutEx;
                    logger.warn("Navigation attempt {} timed out: {}", navAttempts, timeoutEx.getMessage());
                    
                    // Enhanced handling for renderer timeouts
                    if (timeoutEx.getMessage() != null && 
                        timeoutEx.getMessage().contains("Timed out receiving message from renderer")) {
                        logger.warn("Detected renderer communication timeout - extending wait and retrying");
                        
                        // Progressive backoff for renderer issues: 5s, 10s, 15s
                        int rendererWait = navAttempts * 5000;
                        logger.info("Waiting {}ms to allow renderer to stabilize", rendererWait);
                        Thread.sleep(rendererWait);
                        
                        // Try to refresh the driver's page load timeout
                        try {
                            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(600));
                            logger.debug("Reset page load timeout to 600s");
                        } catch (Exception e) {
                            logger.warn("Could not reset timeout: {}", e.getMessage());
                        }
                    } else {
                        // Standard timeout - shorter wait
                        Thread.sleep(2000);
                    }
                    
                    if (navAttempts >= maxNavAttempts) {
                        logger.error("Navigation failed after {} attempts with timeout", maxNavAttempts);
                        throw new RuntimeException("Failed to navigate to homepage: renderer timeout after " + 
                                                  maxNavAttempts + " attempts", timeoutEx);
                    }
                    
                } catch (Exception navException) {
                    lastNavException = navException;
                    logger.warn("Navigation attempt {} failed: {}", navAttempts, navException.getMessage());
                    
                    Thread.sleep(2000);
                    
                    if (navAttempts >= maxNavAttempts) {
                        logger.error("Navigation failed after {} attempts", maxNavAttempts);
                        throw new RuntimeException("Failed to navigate to homepage after " + 
                                                  maxNavAttempts + " attempts", navException);
                    }
                }
            }

            // Mark setup as completed for this thread
            SETUP_COMPLETED.set(true);

            // Call additional setup hook for test classes
            additionalSetup();

            logger.info("Test setup completed for: {}", method.getName());

        } catch (Exception e) {
            logger.error("Test setup failed for: {} - {}", method.getName(), e.getMessage());

            // Clean up driver on failure
            safeQuitDriver();
            tearDownThreadState();

            throw new RuntimeException("Test setup failed: " + e.getMessage(), e);
        }
    }

    @AfterMethod
    public void tearDown(Method method) {
        logger.info("Cleaning up test: {}.{}\n", this.getClass().getSimpleName(), method.getName());

        try {
            // Call additional teardown hook for test classes
            additionalTeardown();

            WebDriver driver = DRIVER.get();
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
                safeQuitDriver();
            } catch (Exception driverException) {
                logger.warn("Error closing driver: {}", driverException.getMessage());
            }
            tearDownThreadState();
            logger.info("Test cleanup completed for: {}", method.getName());
        }
    }

    /**
     * Check if driver is still valid and can be used
     * @return true if driver is valid
     */
    private boolean isDriverValid() {
        try {
            WebDriver driver = DRIVER.get();
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
        System.setProperty("chrome.switches", "--headless --no-sandbox --disable-dev-shm-usage");

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
        getHomePage().navigateToHomePage();
    }

    protected void takeScreenshot(String testName) {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            ScreenshotUtils.takeScreenshot(driver, testName);
        }
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
        getDriver().get(fullUrl);
    }

    /**
     * Verify current page URL contains expected path
     * @param expectedPath Expected path in URL
     * @return true if URL contains expected path
     */
    protected boolean isOnPage(String expectedPath) {
        String currentUrl = getDriver().getCurrentUrl();
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

    protected WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized for current thread");
        }
        return driver;
    }

    protected HomePage getHomePage() {
        HomePage page = HOME_PAGE.get();
        if (page == null) {
            // Lazy initialization if HomePage is null but Driver exists
            WebDriver driver = DRIVER.get();
            if (driver != null) {
                logger.debug("HomePage was null, creating new instance from existing driver");
                page = new HomePage(driver);
                HOME_PAGE.set(page);
            } else {
                throw new IllegalStateException("Cannot create HomePage - WebDriver not initialized for current thread");
            }
        }
        return page;
    }

    protected void setHomePage(HomePage homePage) {
        HOME_PAGE.set(homePage);
    }

    protected HomePage peekHomePage() {
        return HOME_PAGE.get();
    }

    private void safeQuitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                WebDriverFactory.quitDriver();
            } catch (Exception e) {
                logger.warn("Error quitting driver: {}", e.getMessage());
            }
        }
    }

    private void tearDownThreadState() {
        DRIVER.remove();
        HOME_PAGE.remove();
        SETUP_COMPLETED.remove();
    }

}
