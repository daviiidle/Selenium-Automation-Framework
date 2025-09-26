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
// PageFactory removed - using pure Selenide approach
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.CollectionCondition;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Base page class providing common functionality for all page objects
 * Implements the Page Object Model pattern with Selenide-first approach
 * Migrated to use Selenide as primary automation library for enhanced reliability
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
        // PageFactory.initElements(driver, this); // Removed - using pure Selenide
    }

    protected BasePage() {
        // Use Selenide's WebDriverRunner to get the current driver
        this.driver = getWebDriver();
        if (this.driver != null) {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getExplicitTimeout()));
            this.elementUtils = new ElementUtils(driver);
            this.waitUtils = new WaitUtils(driver);
            this.ajaxUtils = new AjaxUtils(driver);
            // PageFactory.initElements(driver, this); // Removed - using pure Selenide
        }
    }

    // Navigation Methods
    /**
     * Navigate to a specific URL using Selenide
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        open(url);
        logger.debug("Page loaded using Selenide");
    }

    /**
     * Get current page URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    /**
     * Wait for URL to contain specified text
     * @param urlPart Expected URL part
     */
    protected void waitForUrlToContain(String urlPart) {
        try {
            WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(15));
            wait.until(ExpectedConditions.urlContains(urlPart));
            logger.debug("URL now contains: {}", urlPart);
        } catch (Exception e) {
            logger.warn("URL did not contain '{}' within timeout. Current URL: {}", urlPart, getCurrentUrl());
        }
    }

    /**
     * Get current page title
     * @return Current page title
     */
    protected String getCurrentTitle() {
        return title();
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
     * Refresh current page using Selenide
     */
    protected void refreshPage() {
        logger.info("Refreshing current page");
        refresh();
    }

    /**
     * Go back to previous page using Selenide
     */
    protected void goBack() {
        logger.info("Navigating back to previous page");
        back();
    }

    // Element Interaction Methods - Selenide First
    /**
     * Find element using Selenide (returns SelenideElement)
     * @param locator Element locator
     * @return SelenideElement
     */
    protected SelenideElement findElement(By locator) {
        return $(locator);
    }

    /**
     * Find elements using Selenide (returns ElementsCollection)
     * @param locator Element locator
     * @return ElementsCollection
     */
    protected ElementsCollection findElements(By locator) {
        return $$(locator);
    }

    /**
     * Find element using CSS selector
     * @param cssSelector CSS selector
     * @return SelenideElement
     */
    protected SelenideElement findElement(String cssSelector) {
        return $(cssSelector);
    }

    /**
     * Find elements using CSS selector
     * @param cssSelector CSS selector
     * @return ElementsCollection
     */
    protected ElementsCollection findElements(String cssSelector) {
        return $$(cssSelector);
    }

    /**
     * Click element using Selenide (with automatic waits and retries)
     * @param locator Element locator
     */
    protected void click(By locator) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                $(locator).click();
                logger.debug("Clicked element using Selenide: {}", locator);
                return;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof InterruptedException) {
                    logger.warn("Click interrupted, retrying... attempt {}/{}", i + 1, maxRetries);
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Failed to click element after " + maxRetries + " attempts due to interruption", e);
                    }
                    continue;
                } else {
                    logger.error("Failed to click element: {}", locator, e);
                    throw e;
                }
            }
        }
    }

    /**
     * Click element using CSS selector
     * @param cssSelector CSS selector
     */
    protected void click(String cssSelector) {
        try {
            $(cssSelector).click();
            logger.debug("Clicked element using Selenide: {}", cssSelector);
        } catch (Exception e) {
            logger.error("Failed to click element: {}", cssSelector, e);
            throw e;
        }
    }

    /**
     * Click SelenideElement
     * @param element SelenideElement to click
     */
    protected void click(SelenideElement element) {
        element.click();
        logger.debug("Clicked SelenideElement");
    }

    /**
     * Type text into element using Selenide (automatically clears first)
     * @param locator Element locator
     * @param text Text to type
     */
    protected void type(By locator, String text) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                $(locator).setValue(text);
                logger.debug("Typed '{}' into element using Selenide: {}", text, locator);
                return;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof InterruptedException) {
                    logger.warn("Type interrupted, retrying... attempt {}/{}", i + 1, maxRetries);
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Failed to type text after " + maxRetries + " attempts due to interruption", e);
                    }
                    continue;
                } else {
                    logger.error("Failed to type text: {}", e.getMessage());
                    throw e;
                }
            }
        }
    }

    /**
     * Type text into element using CSS selector
     * @param cssSelector CSS selector
     * @param text Text to type
     */
    protected void type(String cssSelector, String text) {
        try {
            $(cssSelector).setValue(text);
            logger.debug("Typed '{}' into element using Selenide: {}", text, cssSelector);
        } catch (Exception e) {
            logger.error("Failed to type text: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Type text into SelenideElement
     * @param element SelenideElement to type into
     * @param text Text to type
     */
    protected void type(SelenideElement element, String text) {
        element.setValue(text);
        logger.debug("Typed '{}' into SelenideElement", text);
    }

    /**
     * Get text from element using Selenide
     * @param locator Element locator
     * @return Element text
     */
    protected String getText(By locator) {
        String text = $(locator).getText();
        logger.debug("Got text '{}' from element using Selenide: {}", text, locator);
        return text;
    }

    /**
     * Get text from element using CSS selector
     * @param cssSelector CSS selector
     * @return Element text
     */
    protected String getText(String cssSelector) {
        String text = $(cssSelector).getText();
        logger.debug("Got text '{}' from element using Selenide: {}", text, cssSelector);
        return text;
    }

    /**
     * Get text from SelenideElement
     * @param element SelenideElement
     * @return Element text
     */
    protected String getText(SelenideElement element) {
        String text = element.getText();
        logger.debug("Got text '{}' from SelenideElement", text);
        return text;
    }

    /**
     * Get attribute value from element using Selenide
     * @param locator Element locator
     * @param attributeName Attribute name
     * @return Attribute value
     */
    protected String getAttribute(By locator, String attributeName) {
        String attributeValue = $(locator).getAttribute(attributeName);
        logger.debug("Got attribute '{}' value '{}' from element using Selenide: {}", attributeName, attributeValue, locator);
        return attributeValue;
    }

    /**
     * Get attribute value using CSS selector
     * @param cssSelector CSS selector
     * @param attributeName Attribute name
     * @return Attribute value
     */
    protected String getAttribute(String cssSelector, String attributeName) {
        String attributeValue = $(cssSelector).getAttribute(attributeName);
        logger.debug("Got attribute '{}' value '{}' from element using Selenide: {}", attributeName, attributeValue, cssSelector);
        return attributeValue;
    }

    /**
     * Get attribute value from SelenideElement
     * @param element SelenideElement
     * @param attributeName Attribute name
     * @return Attribute value
     */
    protected String getAttribute(SelenideElement element, String attributeName) {
        String attributeValue = element.getAttribute(attributeName);
        logger.debug("Got attribute '{}' value '{}' from SelenideElement", attributeName, attributeValue);
        return attributeValue;
    }

    /**
     * Clear element content using Selenide
     * @param locator Element locator
     */
    protected void clear(By locator) {
        $(locator).clear();
        logger.debug("Cleared element using Selenide: {}", locator);
    }

    /**
     * Clear element content using CSS selector
     * @param cssSelector CSS selector
     */
    protected void clear(String cssSelector) {
        $(cssSelector).clear();
        logger.debug("Cleared element using Selenide: {}", cssSelector);
    }

    /**
     * Clear SelenideElement content
     * @param element SelenideElement to clear
     */
    protected void clear(SelenideElement element) {
        element.clear();
        logger.debug("Cleared SelenideElement");
    }

    // Validation Methods - Selenide First
    /**
     * Check if element is displayed using Selenide
     * @param locator Element locator
     * @return true if element is displayed
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            return $(locator).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed: {}", locator);
            return false;
        }
    }

    /**
     * Check if element is displayed using CSS selector
     * @param cssSelector CSS selector
     * @return true if element is displayed
     */
    protected boolean isElementDisplayed(String cssSelector) {
        try {
            return $(cssSelector).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed: {}", cssSelector);
            return false;
        }
    }

    /**
     * Check if element is enabled using Selenide
     * @param locator Element locator
     * @return true if element is enabled
     */
    protected boolean isElementEnabled(By locator) {
        try {
            return $(locator).isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", locator);
            return false;
        }
    }

    /**
     * Check if element is enabled using CSS selector
     * @param cssSelector CSS selector
     * @return true if element is enabled
     */
    protected boolean isElementEnabled(String cssSelector) {
        try {
            return $(cssSelector).isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", cssSelector);
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

    // Selenide Enhanced Methods
    /**
     * Find element using Selenide syntax
     * @param selector CSS selector
     * @return SelenideElement
     */
    protected SelenideElement $(String selector) {
        logger.debug("Finding element with Selenide: {}", selector);
        return com.codeborne.selenide.Selenide.$(selector);
    }

    /**
     * Find element using Selenide with By locator
     * @param locator By locator
     * @return SelenideElement
     */
    protected SelenideElement $(By locator) {
        logger.debug("Finding element with Selenide: {}", locator);
        return com.codeborne.selenide.Selenide.$(locator);
    }

    /**
     * Find elements using Selenide syntax
     * @param selector CSS selector
     * @return ElementsCollection
     */
    protected ElementsCollection $$(String selector) {
        logger.debug("Finding elements with Selenide: {}", selector);
        return com.codeborne.selenide.Selenide.$$(selector);
    }

    /**
     * Find elements using Selenide with By locator
     * @param locator By locator
     * @return ElementsCollection
     */
    protected ElementsCollection $$(By locator) {
        logger.debug("Finding elements with Selenide: {}", locator);
        return com.codeborne.selenide.Selenide.$$(locator);
    }

    /**
     * Click element using Selenide (more reliable)
     * @param selector CSS selector
     */
    protected void clickSelenide(String selector) {
        logger.debug("Clicking element with Selenide: {}", selector);
        $(selector).click();
    }

    /**
     * Click element using Selenide with By locator
     * @param locator By locator
     */
    protected void clickSelenide(By locator) {
        logger.debug("Clicking element with Selenide: {}", locator);
        $(locator).click();
    }

    /**
     * Type text using Selenide (handles clear automatically)
     * @param selector CSS selector
     * @param text Text to type
     */
    protected void typeSelenide(String selector, String text) {
        logger.debug("Typing '{}' with Selenide into: {}", text, selector);
        $(selector).setValue(text);
    }

    /**
     * Type text using Selenide with By locator
     * @param locator By locator
     * @param text Text to type
     */
    protected void typeSelenide(By locator, String text) {
        logger.debug("Typing '{}' with Selenide into: {}", text, locator);
        $(locator).setValue(text);
    }

    /**
     * Get text using Selenide
     * @param selector CSS selector
     * @return Element text
     */
    protected String getTextSelenide(String selector) {
        String text = $(selector).getText();
        logger.debug("Got text '{}' with Selenide from: {}", text, selector);
        return text;
    }

    /**
     * Get text using Selenide with By locator
     * @param locator By locator
     * @return Element text
     */
    protected String getTextSelenide(By locator) {
        String text = $(locator).getText();
        logger.debug("Got text '{}' with Selenide from: {}", text, locator);
        return text;
    }

    /**
     * Check if element is visible using Selenide
     * @param selector CSS selector
     * @return true if element is visible
     */
    protected boolean isVisibleSelenide(String selector) {
        return $(selector).isDisplayed();
    }

    /**
     * Check if element is visible using Selenide with By locator
     * @param locator By locator
     * @return true if element is visible
     */
    protected boolean isVisibleSelenide(By locator) {
        return $(locator).isDisplayed();
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