package com.demowebshop.automation.pages.common;

import com.demowebshop.automation.config.ConfigManager;
import com.demowebshop.automation.factories.driver.WebDriverFactory;
import com.demowebshop.automation.utils.selenium.ElementUtils;
import com.demowebshop.automation.utils.selenium.WaitUtils;
import com.demowebshop.automation.utils.selenium.AjaxUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base page class providing common functionality for all page objects
 * Implements the Page Object Model pattern with utility methods
 */
public abstract class BasePage {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ElementUtils elementUtils;
    protected WaitUtils waitUtils;
    protected AjaxUtils ajaxUtils;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getExplicitTimeout()));
        this.elementUtils = new ElementUtils(driver);
        this.waitUtils = new WaitUtils(driver);
        this.ajaxUtils = new AjaxUtils(driver);
        PageFactory.initElements(driver, this);
    }

    protected BasePage() {
        this(WebDriverFactory.getDriver());
    }

    // Navigation Methods
    /**
     * Navigate to a specific URL
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        driver.get(url);
        waitForPageToLoad();
    }

    /**
     * Get current page URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get current page title
     * @return Current page title
     */
    protected String getCurrentTitle() {
        return driver.getTitle();
    }

    /**
     * Handle unexpected alerts that may appear during page interactions
     * @return true if alert was handled, false if no alert was present
     */
    protected boolean handleUnexpectedAlert() {
        try {
            if (driver.switchTo().alert() != null) {
                String alertText = driver.switchTo().alert().getText();
                logger.warn("Unexpected alert detected: '{}' - Dismissing", alertText);
                driver.switchTo().alert().dismiss();
                return true;
            }
        } catch (Exception e) {
            // No alert present, continue normally
            return false;
        }
        return false;
    }

    /**
     * Refresh current page
     */
    protected void refreshPage() {
        logger.info("Refreshing current page");
        driver.navigate().refresh();
        waitForPageToLoad();
    }

    /**
     * Go back to previous page
     */
    protected void goBack() {
        logger.info("Navigating back to previous page");
        driver.navigate().back();
        waitForPageToLoad();
    }

    // Element Interaction Methods
    /**
     * Find element with wait
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement findElement(By locator) {
        return waitUtils.waitForElementToBeVisible(locator);
    }

    /**
     * Find elements with wait
     * @param locator Element locator
     * @return List of WebElements
     */
    protected List<WebElement> findElements(By locator) {
        return waitUtils.waitForElementsToBeVisible(locator);
    }

    /**
     * Click element with wait and logging
     * @param locator Element locator
     */
    protected void click(By locator) {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebElement element = waitUtils.waitForElementToBeClickable(locator);
                elementUtils.clickElement(element);
                logger.debug("Clicked element: {}", locator);
                return; // Success, exit the retry loop
            } catch (StaleElementReferenceException e) {
                if (attempt == maxRetries) {
                    logger.error("Element remained stale after {} attempts: {}", maxRetries, locator);
                    throw new RuntimeException("Element is stale and could not be refound: " + locator, e);
                }
                logger.warn("Element is stale, retrying ({}/{}): {}", attempt, maxRetries, locator);
                // Wait a bit before retry to allow page to stabilize
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during stale element retry", ie);
                }
            } catch (Exception e) {
                logger.error("Failed to click element after {} attempts: {}", attempt, locator);
                throw e;
            }
        }
    }

    /**
     * Click element with wait and logging
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        waitUtils.waitForElementToBeClickable(element);
        elementUtils.clickElement(element);
        logger.debug("Clicked element: {}", element);
    }

    /**
     * Type text into element with clear and logging
     * @param locator Element locator
     * @param text Text to type
     */
    protected void type(By locator, String text) {
        try {
            WebElement element = waitUtils.waitForElementToBeVisible(locator);
            elementUtils.clearAndType(element, text);
            logger.debug("Typed '{}' into element: {}", text, locator);
        } catch (Exception e) {
            // Handle unexpected alerts that might appear during form filling
            if (handleUnexpectedAlert()) {
                // Retry the operation after handling alert
                try {
                    WebElement element = waitUtils.waitForElementToBeVisible(locator);
                    elementUtils.clearAndType(element, text);
                    logger.debug("Typed '{}' into element after handling alert: {}", text, locator);
                } catch (Exception retryException) {
                    logger.error("Failed to type after handling alert: {}", retryException.getMessage());
                    throw retryException;
                }
            } else {
                throw e;
            }
        }
    }

    /**
     * Type text into element with clear and logging
     * @param element WebElement to type into
     * @param text Text to type
     */
    protected void type(WebElement element, String text) {
        waitUtils.waitForElementToBeVisible(element);
        elementUtils.clearAndType(element, text);
        logger.debug("Typed '{}' into element: {}", text, element);
    }

    /**
     * Get text from element with wait
     * @param locator Element locator
     * @return Element text
     */
    protected String getText(By locator) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        String text = element.getText();
        logger.debug("Got text '{}' from element: {}", text, locator);
        return text;
    }

    /**
     * Get text from element with wait
     * @param element WebElement
     * @return Element text
     */
    protected String getText(WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        String text = element.getText();
        logger.debug("Got text '{}' from element: {}", text, element);
        return text;
    }

    /**
     * Get attribute value from element with wait
     * @param locator Element locator
     * @param attributeName Attribute name
     * @return Attribute value
     */
    protected String getAttribute(By locator, String attributeName) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        String attributeValue = element.getAttribute(attributeName);
        logger.debug("Got attribute '{}' value '{}' from element: {}", attributeName, attributeValue, locator);
        return attributeValue;
    }

    /**
     * Get attribute value from WebElement
     * @param element WebElement
     * @param attributeName Attribute name
     * @return Attribute value
     */
    protected String getAttribute(WebElement element, String attributeName) {
        waitUtils.waitForElementToBeVisible(element);
        String attributeValue = element.getAttribute(attributeName);
        logger.debug("Got attribute '{}' value '{}' from element: {}", attributeName, attributeValue, element);
        return attributeValue;
    }

    /**
     * Clear element content
     * @param element WebElement to clear
     */
    protected void clear(WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        element.clear();
        logger.debug("Cleared element: {}", element);
    }

    /**
     * Clear element content by locator
     * @param locator Element locator
     */
    protected void clear(By locator) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        element.clear();
        logger.debug("Cleared element: {}", locator);
    }

    // Validation Methods
    /**
     * Check if element is displayed
     * @param locator Element locator
     * @return true if element is displayed
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            WebElement element = waitUtils.softWaitForElementToBeVisible(locator, ConfigManager.getExplicitTimeout());
            return element != null && element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is enabled
     * @param locator Element locator
     * @return true if element is enabled
     */
    protected boolean isElementEnabled(By locator) {
        try {
            WebElement element = waitUtils.softWaitForElementToBeVisible(locator, ConfigManager.getExplicitTimeout());
            return element != null && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is selected
     * @param locator Element locator
     * @return true if element is selected
     */
    protected boolean isElementSelected(By locator) {
        try {
            WebElement element = waitUtils.softWaitForElementToBeVisible(locator, ConfigManager.getExplicitTimeout());
            return element != null && element.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    // Wait Methods
    /**
     * Wait for page to load completely
     */
    protected void waitForPageToLoad() {
        waitUtils.waitForPageToLoad();
    }

    /**
     * Wait for element to be visible
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement waitForElementVisible(By locator) {
        return waitUtils.waitForElementToBeVisible(locator);
    }

    /**
     * Wait for element to be clickable
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement waitForElementClickable(By locator) {
        return waitUtils.waitForElementToBeClickable(locator);
    }

    /**
     * Wait for text to be present in element
     * @param locator Element locator
     * @param text Expected text
     */
    protected void waitForTextInElement(By locator, String text) {
        waitUtils.waitForTextToBePresentInElement(locator, text);
    }

    /**
     * Wait for element to disappear
     * @param locator Element locator
     */
    protected void waitForElementToDisappear(By locator) {
        waitUtils.waitForElementToBeInvisible(locator);
    }

    // JavaScript Methods
    /**
     * Execute JavaScript
     * @param script JavaScript to execute
     * @return Script result
     */
    protected Object executeScript(String script) {
        return elementUtils.executeJavaScript(script);
    }

    /**
     * Scroll to element
     * @param element Element to scroll to
     */
    protected void scrollToElement(WebElement element) {
        elementUtils.scrollToElement(element);
    }

    /**
     * Scroll to element by locator
     * @param locator Element locator
     */
    protected void scrollToElement(By locator) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        elementUtils.scrollToElement(element);
    }

    // Abstract method that subclasses must implement
    /**
     * Verify that the page is loaded correctly
     * @return true if page is loaded correctly
     */
    public abstract boolean isPageLoaded();

    /**
     * Get the page URL pattern or identifier
     * @return Page URL pattern
     */
    public abstract String getPageUrlPattern();
}