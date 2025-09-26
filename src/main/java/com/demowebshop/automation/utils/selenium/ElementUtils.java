package com.demowebshop.automation.utils.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Utility class for enhanced WebElement interactions
 * Migrated to prioritize Selenide for improved reliability and cleaner syntax
 * Legacy Selenium WebDriver methods available for backward compatibility
 */
public class ElementUtils {
    private static final Logger logger = LogManager.getLogger(ElementUtils.class);
    private final WebDriver driver;
    private final Actions actions;
    private final JavascriptExecutor jsExecutor;

    public ElementUtils(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Enhanced click method with stale element retry and JavaScript fallback
     * @param by Locator to find the element
     */
    public void clickElement(By by) {
        int maxRetries = 5;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebElement element = driver.findElement(by);
                element.click();
                logger.debug("Successfully clicked element using regular click");
                return; // Success, exit the retry loop
            } catch (StaleElementReferenceException e) {
                logger.warn("Element is stale, re-finding and retrying ({}/{})", attempt, maxRetries);
                // Wait a bit before retry to allow page to stabilize
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during stale element retry", ie);
                }
                if (attempt == maxRetries) {
                    logger.error("Element remained stale after {} attempts", maxRetries);
                    throw new RuntimeException("Element is stale and could not be clicked after multiple attempts", e);
                }
            } catch (ElementClickInterceptedException e) {
                logger.warn("Regular click intercepted, trying JavaScript click (attempt {}/{})", attempt, maxRetries);
                try {
                    WebElement element = driver.findElement(by);
                    clickElementWithJavaScript(element);
                    return; // Success with JavaScript click
                } catch (Exception jsException) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element with both regular and JavaScript methods", e);
                    }
                    logger.warn("JavaScript click also failed, retrying ({}/{})", attempt, maxRetries);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (ElementNotInteractableException e) {
                logger.warn("Element not interactable, trying JavaScript click (attempt {}/{})", attempt, maxRetries);
                try {
                    WebElement element = driver.findElement(by);
                    clickElementWithJavaScript(element);
                    return; // Success with JavaScript click
                } catch (Exception jsException) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element with both regular and JavaScript methods", e);
                    }
                    logger.warn("JavaScript click also failed, retrying ({}/{})", attempt, maxRetries);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (NoSuchElementException e) {
                if (attempt == maxRetries) {
                    logger.error("Element not found after {} attempts: {}", maxRetries, by);
                    throw new RuntimeException("Element not found: " + by, e);
                }
                logger.warn("Element not found, retrying ({}/{}): {}", attempt, maxRetries, by);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                logger.warn("Regular click failed (attempt {}/{}), trying JavaScript click: {}", attempt, maxRetries, e.getMessage());
                try {
                    WebElement element = driver.findElement(by);
                    clickElementWithJavaScript(element);
                    return; // Success with JavaScript click
                } catch (Exception jsException) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element with both regular and JavaScript methods after " + maxRetries + " attempts", e);
                    }
                    logger.warn("JavaScript click also failed, retrying ({}/{})", attempt, maxRetries);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     * Enhanced click method with retry mechanism and JavaScript fallback (for direct WebElement)
     * @param element Element to click
     */
    public void clickElement(WebElement element) {
        int maxRetries = 5;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Try regular click first
                element.click();
                logger.debug("Successfully clicked element using regular click");
                return; // Success, exit the retry loop
            } catch (StaleElementReferenceException e) {
                if (attempt == maxRetries) {
                    logger.error("Element remained stale after {} attempts", maxRetries);
                    throw new RuntimeException("Element is stale and could not be clicked after multiple attempts", e);
                }
                logger.warn("Element is stale, cannot re-find without locator ({}/{})", attempt, maxRetries);
                // Wait a bit before retry to allow page to stabilize
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during stale element retry", ie);
                }
            } catch (ElementClickInterceptedException e) {
                logger.warn("Regular click intercepted, trying JavaScript click");
                try {
                    clickElementWithJavaScript(element);
                    return; // Success with JavaScript click
                } catch (Exception jsException) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element with both regular and JavaScript methods", e);
                    }
                    logger.warn("JavaScript click also failed, retrying ({}/{})", attempt, maxRetries);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (ElementNotInteractableException e) {
                logger.warn("Element not interactable, trying JavaScript click");
                try {
                    clickElementWithJavaScript(element);
                    return; // Success with JavaScript click
                } catch (Exception jsException) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element with both regular and JavaScript methods", e);
                    }
                    logger.warn("JavaScript click also failed, retrying ({}/{})", attempt, maxRetries);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                logger.warn("Regular click failed (attempt {}/{}), trying JavaScript click: {}", attempt, maxRetries, e.getMessage());
                try {
                    clickElementWithJavaScript(element);
                    return; // Success with JavaScript click
                } catch (Exception jsException) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element with both regular and JavaScript methods after " + maxRetries + " attempts", e);
                    }
                    logger.warn("JavaScript click also failed, retrying ({}/{})", attempt, maxRetries);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     * Click element using JavaScript
     * @param element Element to click
     */
    public void clickElementWithJavaScript(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].click();", element);
            logger.debug("Successfully clicked element using JavaScript");
        } catch (Exception e) {
            logger.error("JavaScript click failed: {}", e.getMessage());
            throw new RuntimeException("Failed to click element with both regular and JavaScript methods", e);
        }
    }

    /**
     * Double click on element
     * @param element Element to double click
     */
    public void doubleClick(WebElement element) {
        try {
            actions.doubleClick(element).perform();
            logger.debug("Successfully double clicked element");
        } catch (Exception e) {
            logger.error("Double click failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Right click on element
     * @param element Element to right click
     */
    public void rightClick(WebElement element) {
        try {
            actions.contextClick(element).perform();
            logger.debug("Successfully right clicked element");
        } catch (Exception e) {
            logger.error("Right click failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Clear element and type text with enhanced error handling
     * @param element Element to type into
     * @param text Text to type
     */
    public void clearAndType(WebElement element, String text) {
        try {
            element.clear();
            element.sendKeys(text);
            logger.debug("Successfully cleared and typed text: {}", text);
        } catch (Exception e) {
            logger.warn("Regular clear and type failed, trying JavaScript: {}", e.getMessage());
            clearAndTypeWithJavaScript(element, text);
        }
    }

    /**
     * Clear element and type text using JavaScript
     * @param element Element to type into
     * @param text Text to type
     */
    public void clearAndTypeWithJavaScript(WebElement element, String text) {
        try {
            jsExecutor.executeScript("arguments[0].value = '';", element);
            element.sendKeys(text);
            logger.debug("Successfully cleared and typed text using JavaScript: {}", text);
        } catch (Exception e) {
            logger.error("JavaScript clear and type failed: {}", e.getMessage());
            throw new RuntimeException("Failed to clear and type text", e);
        }
    }

    /**
     * Type text character by character
     * @param element Element to type into
     * @param text Text to type
     */
    public void typeSlowly(WebElement element, String text) {
        element.clear();
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(50); // 50ms delay between characters
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.debug("Successfully typed text slowly: {}", text);
    }

    /**
     * Select dropdown option by visible text
     * @param element Dropdown element
     * @param visibleText Visible text to select
     */
    public void selectByVisibleText(WebElement element, String visibleText) {
        try {
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
            logger.debug("Successfully selected option by visible text: {}", visibleText);
        } catch (Exception e) {
            logger.error("Failed to select option by visible text: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Select dropdown option by value
     * @param element Dropdown element
     * @param value Value to select
     */
    public void selectByValue(WebElement element, String value) {
        try {
            Select select = new Select(element);
            select.selectByValue(value);
            logger.debug("Successfully selected option by value: {}", value);
        } catch (Exception e) {
            logger.error("Failed to select option by value: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Select dropdown option by index
     * @param element Dropdown element
     * @param index Index to select (0-based)
     */
    public void selectByIndex(WebElement element, int index) {
        try {
            Select select = new Select(element);
            select.selectByIndex(index);
            logger.debug("Successfully selected option by index: {}", index);
        } catch (Exception e) {
            logger.error("Failed to select option by index: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Hover over element
     * @param element Element to hover over
     */
    public void hoverOverElement(WebElement element) {
        try {
            actions.moveToElement(element).perform();
            logger.debug("Successfully hovered over element");
        } catch (Exception e) {
            logger.error("Hover action failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Drag and drop from source to target element
     * @param sourceElement Source element
     * @param targetElement Target element
     */
    public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
        try {
            actions.dragAndDrop(sourceElement, targetElement).perform();
            logger.debug("Successfully performed drag and drop");
        } catch (Exception e) {
            logger.error("Drag and drop failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Scroll to element
     * @param element Element to scroll to
     */
    public void scrollToElement(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
            logger.debug("Successfully scrolled to element");
        } catch (Exception e) {
            logger.error("Scroll to element failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Scroll page by pixels
     * @param pixels Pixels to scroll (positive for down, negative for up)
     */
    public void scrollByPixels(int pixels) {
        try {
            jsExecutor.executeScript("window.scrollBy(0, arguments[0]);", pixels);
            logger.debug("Successfully scrolled by {} pixels", pixels);
        } catch (Exception e) {
            logger.error("Scroll by pixels failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Scroll to top of page
     */
    public void scrollToTop() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, 0);");
            logger.debug("Successfully scrolled to top of page");
        } catch (Exception e) {
            logger.error("Scroll to top failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Scroll to bottom of page
     */
    public void scrollToBottom() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            logger.debug("Successfully scrolled to bottom of page");
        } catch (Exception e) {
            logger.error("Scroll to bottom failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Highlight element for debugging purposes
     * @param element Element to highlight
     */
    public void highlightElement(WebElement element) {
        try {
            String originalStyle = element.getAttribute("style");
            jsExecutor.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red; background-color: yellow;');", element);

            try {
                Thread.sleep(500); // Highlight for 500ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            jsExecutor.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
            logger.debug("Successfully highlighted element");
        } catch (Exception e) {
            logger.error("Element highlighting failed: {}", e.getMessage());
        }
    }

    /**
     * Execute custom JavaScript
     * @param script JavaScript code to execute
     * @param arguments Script arguments
     * @return Script execution result
     */
    public Object executeJavaScript(String script, Object... arguments) {
        try {
            Object result = jsExecutor.executeScript(script, arguments);
            logger.debug("Successfully executed JavaScript: {}", script);
            return result;
        } catch (Exception e) {
            logger.error("JavaScript execution failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get element text, handling empty/null cases
     * @param element Element to get text from
     * @return Element text or empty string
     */
    public String getElementText(WebElement element) {
        try {
            String text = element.getText();
            return text != null ? text.trim() : "";
        } catch (Exception e) {
            logger.error("Failed to get element text: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Get element attribute value, handling null cases
     * @param element Element to get attribute from
     * @param attributeName Attribute name
     * @return Attribute value or empty string
     */
    public String getElementAttribute(WebElement element, String attributeName) {
        try {
            String attributeValue = element.getAttribute(attributeName);
            return attributeValue != null ? attributeValue : "";
        } catch (Exception e) {
            logger.error("Failed to get element attribute '{}': {}", attributeName, e.getMessage());
            return "";
        }
    }

    /**
     * Check if element is displayed with exception handling
     * @param element Element to check
     * @return true if element is displayed
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed or not found: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if element is enabled with exception handling
     * @param element Element to check
     * @return true if element is enabled
     */
    public boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled or not found: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if element is selected with exception handling
     * @param element Element to check
     * @return true if element is selected
     */
    public boolean isElementSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (Exception e) {
            logger.debug("Element not selected or not found: {}", e.getMessage());
            return false;
        }
    }

    // ==================== SELENIDE ENHANCED METHODS ====================

    /**
     * Click element using Selenide (more reliable with built-in waits)
     * @param selector CSS selector
     */
    public void clickElementSelenide(String selector) {
        try {
            $(selector).click();
            logger.debug("Successfully clicked element using Selenide: {}", selector);
        } catch (Exception e) {
            logger.error("Selenide click failed for selector: {}: {}", selector, e.getMessage());
            throw e;
        }
    }

    /**
     * Click element using Selenide with By locator
     * @param locator By locator
     */
    public void clickElementSelenide(By locator) {
        try {
            $(locator).click();
            logger.debug("Successfully clicked element using Selenide: {}", locator);
        } catch (Exception e) {
            logger.error("Selenide click failed for locator: {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    /**
     * Type text using Selenide (handles clearing automatically)
     * @param selector CSS selector
     * @param text Text to type
     */
    public void typeSelenide(String selector, String text) {
        try {
            $(selector).setValue(text);
            logger.debug("Successfully typed text using Selenide: {}", text);
        } catch (Exception e) {
            logger.error("Selenide type failed for selector: {}: {}", selector, e.getMessage());
            throw e;
        }
    }

    /**
     * Type text using Selenide with By locator
     * @param locator By locator
     * @param text Text to type
     */
    public void typeSelenide(By locator, String text) {
        try {
            $(locator).setValue(text);
            logger.debug("Successfully typed text using Selenide: {}", text);
        } catch (Exception e) {
            logger.error("Selenide type failed for locator: {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    /**
     * Get text using Selenide
     * @param selector CSS selector
     * @return Element text
     */
    public String getTextSelenide(String selector) {
        try {
            String text = $(selector).getText();
            logger.debug("Successfully got text using Selenide: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Selenide getText failed for selector: {}: {}", selector, e.getMessage());
            return "";
        }
    }

    /**
     * Get text using Selenide with By locator
     * @param locator By locator
     * @return Element text
     */
    public String getTextSelenide(By locator) {
        try {
            String text = $(locator).getText();
            logger.debug("Successfully got text using Selenide: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Selenide getText failed for locator: {}: {}", locator, e.getMessage());
            return "";
        }
    }

    /**
     * Check if element is visible using Selenide
     * @param selector CSS selector
     * @return true if element is visible
     */
    public boolean isVisibleSelenide(String selector) {
        try {
            return $(selector).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not visible using Selenide: {}", selector);
            return false;
        }
    }

    /**
     * Check if element is visible using Selenide with By locator
     * @param locator By locator
     * @return true if element is visible
     */
    public boolean isVisibleSelenide(By locator) {
        try {
            return $(locator).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not visible using Selenide: {}", locator);
            return false;
        }
    }

    /**
     * Wait for element to be visible using Selenide conditions
     * @param selector CSS selector
     * @return SelenideElement
     */
    public SelenideElement waitForVisibleSelenide(String selector) {
        try {
            SelenideElement element = $(selector).shouldBe(Condition.visible);
            logger.debug("Element is now visible using Selenide: {}", selector);
            return element;
        } catch (Exception e) {
            logger.error("Element did not become visible using Selenide: {}: {}", selector, e.getMessage());
            throw e;
        }
    }

    /**
     * Wait for element to be clickable using Selenide conditions
     * @param selector CSS selector
     * @return SelenideElement
     */
    public SelenideElement waitForClickableSelenide(String selector) {
        try {
            SelenideElement element = $(selector).shouldBe(Condition.enabled).shouldBe(Condition.visible);
            logger.debug("Element is now clickable using Selenide: {}", selector);
            return element;
        } catch (Exception e) {
            logger.error("Element did not become clickable using Selenide: {}: {}", selector, e.getMessage());
            throw e;
        }
    }

    /**
     * Select dropdown option using Selenide
     * @param selector CSS selector of dropdown
     * @param optionText Option text to select
     */
    public void selectOptionSelenide(String selector, String optionText) {
        try {
            $(selector).selectOptionContainingText(optionText);
            logger.debug("Successfully selected option using Selenide: {}", optionText);
        } catch (Exception e) {
            logger.error("Selenide select option failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Hover over element using Selenide
     * @param selector CSS selector
     */
    public void hoverSelenide(String selector) {
        try {
            $(selector).hover();
            logger.debug("Successfully hovered using Selenide: {}", selector);
        } catch (Exception e) {
            logger.error("Selenide hover failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Clear element using Selenide
     * @param selector CSS selector
     */
    public void clearSelenide(String selector) {
        try {
            $(selector).clear();
            logger.debug("Successfully cleared element using Selenide: {}", selector);
        } catch (Exception e) {
            logger.error("Selenide clear failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get all elements matching selector using Selenide
     * @param selector CSS selector
     * @return ElementsCollection
     */
    public ElementsCollection getAllElementsSelenide(String selector) {
        try {
            ElementsCollection elements = $$(selector);
            logger.debug("Found {} elements using Selenide: {}", elements.size(), selector);
            return elements;
        } catch (Exception e) {
            logger.error("Failed to get elements using Selenide: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if element has specific text using Selenide
     * @param selector CSS selector
     * @param expectedText Expected text
     * @return true if element contains expected text
     */
    public boolean hasTextSelenide(String selector, String expectedText) {
        try {
            $(selector).shouldHave(Condition.text(expectedText));
            return true;
        } catch (Exception e) {
            logger.debug("Element does not have expected text using Selenide: {}", expectedText);
            return false;
        }
    }

    /**
     * Get element attribute using Selenide
     * @param selector CSS selector
     * @param attributeName Attribute name
     * @return Attribute value
     */
    public String getAttributeSelenide(String selector, String attributeName) {
        try {
            String value = $(selector).getAttribute(attributeName);
            logger.debug("Got attribute '{}' value '{}' using Selenide", attributeName, value);
            return value != null ? value : "";
        } catch (Exception e) {
            logger.error("Failed to get attribute using Selenide: {}", e.getMessage());
            return "";
        }
    }
}