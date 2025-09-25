package com.demowebshop.automation.utils.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 * Utility class for enhanced WebElement interactions
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
     * Enhanced click method with retry mechanism and JavaScript fallback
     * @param element Element to click
     */
    public void clickElement(WebElement element) {
        try {
            // Try regular click first
            element.click();
            logger.debug("Successfully clicked element using regular click");
        } catch (ElementClickInterceptedException e) {
            logger.warn("Regular click intercepted, trying JavaScript click");
            clickElementWithJavaScript(element);
        } catch (StaleElementReferenceException e) {
            logger.error("Element is stale, cannot click: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.warn("Regular click failed, trying JavaScript click: {}", e.getMessage());
            clickElementWithJavaScript(element);
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
}