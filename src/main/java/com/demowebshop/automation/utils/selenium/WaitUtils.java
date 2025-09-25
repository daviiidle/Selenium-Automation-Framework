package com.demowebshop.automation.utils.selenium;

import com.demowebshop.automation.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Utility class for handling WebDriver waits and timing operations
 */
public class WaitUtils {
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final int defaultTimeout;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeout = ConfigManager.getExplicitTimeout();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    }

    /**
     * Wait for element to be visible
     * @param locator Element locator
     * @return WebElement
     */
    public WebElement waitForElementToBeVisible(By locator) {
        return waitForElementToBeVisible(locator, defaultTimeout);
    }

    /**
     * Wait for element to be visible with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout
     * @return WebElement
     */
    public WebElement waitForElementToBeVisible(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not visible within {} seconds: {}", timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Wait for element to be visible
     * @param element WebElement
     * @return WebElement
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        return waitForElementToBeVisible(element, defaultTimeout);
    }

    /**
     * Wait for element to be visible with custom timeout
     * @param element WebElement
     * @param timeoutInSeconds Custom timeout
     * @return WebElement
     */
    public WebElement waitForElementToBeVisible(WebElement element, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            logger.error("Element not visible within {} seconds: {}", timeoutInSeconds, element);
            throw e;
        }
    }

    /**
     * Wait for elements to be visible
     * @param locator Element locator
     * @return List of WebElements
     */
    public List<WebElement> waitForElementsToBeVisible(By locator) {
        return waitForElementsToBeVisible(locator, defaultTimeout);
    }

    /**
     * Wait for elements to be visible with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout
     * @return List of WebElements
     */
    public List<WebElement> waitForElementsToBeVisible(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            logger.error("Elements not visible within {} seconds: {}", timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     * @param locator Element locator
     * @return WebElement
     */
    public WebElement waitForElementToBeClickable(By locator) {
        return waitForElementToBeClickable(locator, defaultTimeout);
    }

    /**
     * Wait for element to be clickable with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout
     * @return WebElement
     */
    public WebElement waitForElementToBeClickable(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            logger.error("Element not clickable within {} seconds: {}", timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     * @param element WebElement
     * @return WebElement
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        return waitForElementToBeClickable(element, defaultTimeout);
    }

    /**
     * Wait for element to be clickable with custom timeout
     * @param element WebElement
     * @param timeoutInSeconds Custom timeout
     * @return WebElement
     */
    public WebElement waitForElementToBeClickable(WebElement element, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            logger.error("Element not clickable within {} seconds: {}", timeoutInSeconds, element);
            throw e;
        }
    }

    /**
     * Wait for element to be present in DOM
     * @param locator Element locator
     * @return WebElement
     */
    public WebElement waitForElementToBePresent(By locator) {
        return waitForElementToBePresent(locator, defaultTimeout);
    }

    /**
     * Wait for element to be present in DOM with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout
     * @return WebElement
     */
    public WebElement waitForElementToBePresent(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not present within {} seconds: {}", timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Wait for element to be invisible
     * @param locator Element locator
     */
    public void waitForElementToBeInvisible(By locator) {
        waitForElementToBeInvisible(locator, defaultTimeout);
    }

    /**
     * Wait for element to be invisible with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForElementToBeInvisible(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element still visible after {} seconds: {}", timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Wait for text to be present in element
     * @param locator Element locator
     * @param text Expected text
     */
    public void waitForTextToBePresentInElement(By locator, String text) {
        waitForTextToBePresentInElement(locator, text, defaultTimeout);
    }

    /**
     * Wait for text to be present in element with custom timeout
     * @param locator Element locator
     * @param text Expected text
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForTextToBePresentInElement(By locator, String text, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            logger.error("Text '{}' not present in element within {} seconds: {}", text, timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Wait for URL to contain specific text
     * @param urlFragment URL fragment to wait for
     */
    public void waitForUrlToContain(String urlFragment) {
        waitForUrlToContain(urlFragment, defaultTimeout);
    }

    /**
     * Wait for URL to contain specific text with custom timeout
     * @param urlFragment URL fragment to wait for
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForUrlToContain(String urlFragment, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (TimeoutException e) {
            logger.error("URL does not contain '{}' within {} seconds. Current URL: {}",
                    urlFragment, timeoutInSeconds, driver.getCurrentUrl());
            throw e;
        }
    }

    /**
     * Wait for page title to contain specific text
     * @param titleFragment Title fragment to wait for
     */
    public void waitForTitleToContain(String titleFragment) {
        waitForTitleToContain(titleFragment, defaultTimeout);
    }

    /**
     * Wait for page title to contain specific text with custom timeout
     * @param titleFragment Title fragment to wait for
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForTitleToContain(String titleFragment, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(ExpectedConditions.titleContains(titleFragment));
        } catch (TimeoutException e) {
            logger.error("Title does not contain '{}' within {} seconds. Current title: {}",
                    titleFragment, timeoutInSeconds, driver.getTitle());
            throw e;
        }
    }

    /**
     * Wait for page to load completely (document ready state)
     */
    public void waitForPageToLoad() {
        waitForPageToLoad(ConfigManager.getPageLoadTimeout());
    }

    /**
     * Wait for page to load completely with custom timeout
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForPageToLoad(int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) driver)
                            .executeScript("return document.readyState").equals("complete");
                }
            });
            logger.debug("Page loaded successfully");
        } catch (TimeoutException e) {
            logger.error("Page not loaded within {} seconds", timeoutInSeconds);
            throw e;
        }
    }

    /**
     * Wait for AJAX calls to complete (jQuery specific)
     */
    public void waitForAjaxToComplete() {
        waitForAjaxToComplete(defaultTimeout);
    }

    /**
     * Wait for AJAX calls to complete with custom timeout (jQuery specific)
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForAjaxToComplete(int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return (Boolean) ((JavascriptExecutor) driver)
                            .executeScript("return jQuery.active === 0");
                }
            });
            logger.debug("AJAX calls completed");
        } catch (Exception e) {
            logger.warn("Could not wait for AJAX completion, jQuery might not be available: {}", e.getMessage());
        }
    }

    /**
     * Generic wait method with custom condition
     * @param condition Expected condition
     * @param timeoutInSeconds Custom timeout
     * @param <T> Return type
     * @return Result of condition
     */
    public <T> T waitForCondition(ExpectedCondition<T> condition, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(condition);
        } catch (TimeoutException e) {
            logger.error("Condition not met within {} seconds", timeoutInSeconds);
            throw e;
        }
    }

    /**
     * Wait for any of the given elements to be visible (returns first found)
     * @param locators Multiple locators to check
     * @return First visible WebElement found
     */
    public WebElement waitForAnyElementToBeVisible(By... locators) {
        return waitForAnyElementToBeVisible(defaultTimeout, locators);
    }

    /**
     * Wait for any of the given elements to be visible with custom timeout
     * @param timeoutInSeconds Custom timeout
     * @param locators Multiple locators to check
     * @return First visible WebElement found
     */
    public WebElement waitForAnyElementToBeVisible(int timeoutInSeconds, By... locators) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(driver -> {
                for (By locator : locators) {
                    try {
                        WebElement element = driver.findElement(locator);
                        if (element.isDisplayed()) {
                            logger.debug("Found visible element with locator: {}", locator);
                            return element;
                        }
                    } catch (Exception ignored) {
                        // Continue to next locator
                    }
                }
                return null;
            });
        } catch (TimeoutException e) {
            logger.error("None of the elements became visible within {} seconds: {}", timeoutInSeconds, java.util.Arrays.toString(locators));
            throw e;
        }
    }

    /**
     * Wait for element to be either present or absent (flexible existence check)
     * @param locator Element locator
     * @param shouldExist true if element should exist, false if it should be absent
     * @return true if condition met
     */
    public boolean waitForElementExistenceState(By locator, boolean shouldExist) {
        return waitForElementExistenceState(locator, shouldExist, defaultTimeout);
    }

    /**
     * Wait for element to be either present or absent with custom timeout
     * @param locator Element locator
     * @param shouldExist true if element should exist, false if it should be absent
     * @param timeoutInSeconds Custom timeout
     * @return true if condition met
     */
    public boolean waitForElementExistenceState(By locator, boolean shouldExist, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(driver -> {
                try {
                    List<WebElement> elements = driver.findElements(locator);
                    boolean exists = !elements.isEmpty();
                    return exists == shouldExist;
                } catch (Exception e) {
                    return !shouldExist; // If error finding element, consider it absent
                }
            });
        } catch (TimeoutException e) {
            String existenceState = shouldExist ? "present" : "absent";
            logger.error("Element did not become {} within {} seconds: {}", existenceState, timeoutInSeconds, locator);
            throw e;
        }
    }

    /**
     * Softly wait for element to be visible (returns null if not found, doesn't throw exception)
     * @param locator Element locator
     * @return WebElement if found and visible, null otherwise
     */
    public WebElement softWaitForElementToBeVisible(By locator) {
        return softWaitForElementToBeVisible(locator, defaultTimeout);
    }

    /**
     * Softly wait for element to be visible with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout
     * @return WebElement if found and visible, null otherwise
     */
    public WebElement softWaitForElementToBeVisible(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.debug("Element not visible within {} seconds (soft wait): {}", timeoutInSeconds, locator);
            return null;
        }
    }

    /**
     * Wait for page URL to change from current URL
     * @param currentUrl The current URL to wait to change from
     */
    public void waitForUrlToChangeFrom(String currentUrl) {
        waitForUrlToChangeFrom(currentUrl, defaultTimeout);
    }

    /**
     * Wait for page URL to change from current URL with custom timeout
     * @param currentUrl The current URL to wait to change from
     * @param timeoutInSeconds Custom timeout
     */
    public void waitForUrlToChangeFrom(String currentUrl, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(driver -> !driver.getCurrentUrl().equals(currentUrl));
            logger.debug("URL changed from: {}", currentUrl);
        } catch (TimeoutException e) {
            logger.error("URL did not change from '{}' within {} seconds. Current URL: {}",
                    currentUrl, timeoutInSeconds, driver.getCurrentUrl());
            throw e;
        }
    }
}