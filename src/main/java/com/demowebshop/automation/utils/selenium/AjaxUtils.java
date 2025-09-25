package com.demowebshop.automation.utils.selenium;

import com.demowebshop.automation.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Utility class for handling AJAX operations and dynamic content
 * Specifically designed for DemoWebShop's asynchronous operations
 */
public class AjaxUtils {
    private static final Logger logger = LogManager.getLogger(AjaxUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;

    public AjaxUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getExplicitTimeout()));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Wait for jQuery AJAX calls to complete
     * Useful for pages that use jQuery for AJAX operations
     */
    public void waitForJQueryToComplete() {
        waitForJQueryToComplete(ConfigManager.getExplicitTimeout());
    }

    /**
     * Wait for jQuery AJAX calls to complete with custom timeout
     * @param timeoutSeconds Timeout in seconds
     */
    public void waitForJQueryToComplete(int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        try {
            customWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        Boolean jQueryDefined = (Boolean) jsExecutor.executeScript("return typeof jQuery !== 'undefined'");
                        if (!jQueryDefined) {
                            return true; // jQuery not used, assume AJAX complete
                        }

                        Long ajaxCallsCount = (Long) jsExecutor.executeScript("return jQuery.active");
                        return ajaxCallsCount == 0;
                    } catch (Exception e) {
                        logger.debug("Error checking jQuery status: {}", e.getMessage());
                        return true; // Assume complete if unable to check
                    }
                }

                @Override
                public String toString() {
                    return "jQuery AJAX calls to complete";
                }
            });
            logger.debug("jQuery AJAX calls completed");
        } catch (Exception e) {
            logger.warn("Timeout waiting for jQuery AJAX completion: {}", e.getMessage());
        }
    }

    /**
     * Wait for all AJAX requests to complete using a generic approach
     * Checks for common AJAX indicators across different frameworks
     */
    public void waitForAjaxToComplete() {
        waitForAjaxToComplete(ConfigManager.getExplicitTimeout());
    }

    /**
     * Wait for all AJAX requests to complete with custom timeout
     * @param timeoutSeconds Timeout in seconds
     */
    public void waitForAjaxToComplete(int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        try {
            customWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    // Check jQuery
                    boolean jQueryComplete = isJQueryComplete();

                    // Check for common loading indicators
                    boolean noLoadingIndicators = !hasLoadingIndicators();

                    // Check document ready state
                    boolean documentReady = isDocumentReady();

                    return jQueryComplete && noLoadingIndicators && documentReady;
                }

                @Override
                public String toString() {
                    return "all AJAX requests to complete";
                }
            });
            logger.debug("All AJAX requests completed");
        } catch (Exception e) {
            logger.warn("Timeout waiting for AJAX completion: {}", e.getMessage());
        }
    }

    /**
     * Check if jQuery is complete or not present
     * @return true if jQuery is complete or not used
     */
    private boolean isJQueryComplete() {
        try {
            Boolean jQueryDefined = (Boolean) jsExecutor.executeScript("return typeof jQuery !== 'undefined'");
            if (!jQueryDefined) {
                return true; // jQuery not used
            }

            Long ajaxCallsCount = (Long) jsExecutor.executeScript("return jQuery.active");
            return ajaxCallsCount == 0;
        } catch (Exception e) {
            logger.debug("Error checking jQuery status: {}", e.getMessage());
            return true; // Assume complete if unable to check
        }
    }

    /**
     * Check if document is ready
     * @return true if document ready state is complete
     */
    private boolean isDocumentReady() {
        try {
            String readyState = (String) jsExecutor.executeScript("return document.readyState");
            return "complete".equals(readyState);
        } catch (Exception e) {
            logger.debug("Error checking document ready state: {}", e.getMessage());
            return true; // Assume ready if unable to check
        }
    }

    /**
     * Check for common loading indicators on the page
     * @return true if loading indicators are present
     */
    private boolean hasLoadingIndicators() {
        try {
            // Common loading indicator selectors
            String[] loadingSelectors = {
                ".loading",
                ".spinner",
                ".ajax-loader",
                "[class*='loading']",
                "[class*='spinner']",
                "[id*='loading']",
                ".overlay",
                ".progress"
            };

            for (String selector : loadingSelectors) {
                try {
                    WebElement loadingElement = driver.findElement(By.cssSelector(selector));
                    if (loadingElement.isDisplayed()) {
                        return true;
                    }
                } catch (Exception e) {
                    // Element not found, continue checking other selectors
                }
            }
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for loading indicators: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Wait for a specific element to be updated after AJAX call
     * Useful when waiting for cart quantity, prices, or other dynamic content
     *
     * @param element Element to monitor
     * @param originalValue Original value/text of the element
     * @return true if element value changed
     */
    public boolean waitForElementValueChange(WebElement element, String originalValue) {
        return waitForElementValueChange(element, originalValue, ConfigManager.getExplicitTimeout());
    }

    /**
     * Wait for element value change with custom timeout
     * @param element Element to monitor
     * @param originalValue Original value
     * @param timeoutSeconds Timeout in seconds
     * @return true if value changed
     */
    public boolean waitForElementValueChange(WebElement element, String originalValue, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        try {
            return customWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        String currentValue = element.getText();
                        if (currentValue == null) {
                            currentValue = element.getAttribute("value");
                        }
                        if (currentValue == null) {
                            currentValue = element.getAttribute("innerHTML");
                        }

                        boolean changed = !originalValue.equals(currentValue);
                        if (changed) {
                            logger.debug("Element value changed from '{}' to '{}'", originalValue, currentValue);
                        }
                        return changed;
                    } catch (Exception e) {
                        logger.debug("Error checking element value change: {}", e.getMessage());
                        return false;
                    }
                }

                @Override
                public String toString() {
                    return "element value to change from: " + originalValue;
                }
            });
        } catch (Exception e) {
            logger.warn("Timeout waiting for element value change: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Wait for cart quantity to update (specific to DemoWebShop)
     * @param expectedQuantity Expected cart quantity
     * @return true if cart quantity matches expected value
     */
    public boolean waitForCartQuantityUpdate(int expectedQuantity) {
        return waitForCartQuantityUpdate(expectedQuantity, ConfigManager.getExplicitTimeout());
    }

    /**
     * Wait for cart quantity update with custom timeout
     * @param expectedQuantity Expected quantity
     * @param timeoutSeconds Timeout in seconds
     * @return true if quantity matches
     */
    public boolean waitForCartQuantityUpdate(int expectedQuantity, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        try {
            return customWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        // Common cart quantity selectors for DemoWebShop
                        String[] cartSelectors = {
                            ".header-links .cart-qty",
                            "[class*='cart-qty']",
                            ".shopping-cart-quantity",
                            "#topcartlink .qty"
                        };

                        for (String selector : cartSelectors) {
                            try {
                                WebElement qtyElement = driver.findElement(By.cssSelector(selector));
                                String qtyText = qtyElement.getText();
                                // Extract number from text like "(2)" or "2 items"
                                String numberOnly = qtyText.replaceAll("[^0-9]", "");
                                if (!numberOnly.isEmpty()) {
                                    int currentQty = Integer.parseInt(numberOnly);
                                    if (currentQty == expectedQuantity) {
                                        logger.debug("Cart quantity updated to: {}", expectedQuantity);
                                        return true;
                                    }
                                }
                            } catch (Exception e) {
                                // Continue checking other selectors
                            }
                        }
                        return false;
                    } catch (Exception e) {
                        logger.debug("Error checking cart quantity: {}", e.getMessage());
                        return false;
                    }
                }

                @Override
                public String toString() {
                    return "cart quantity to update to: " + expectedQuantity;
                }
            });
        } catch (Exception e) {
            logger.warn("Timeout waiting for cart quantity update: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Wait for element to be present and stable (not changing)
     * Useful for elements that appear after AJAX calls and may update multiple times
     *
     * @param locator Locator for the element
     * @param stabilityTimeMs Time in milliseconds to wait for stability
     * @return WebElement if found and stable
     */
    public WebElement waitForElementToBeStable(By locator, long stabilityTimeMs) {
        return waitForElementToBeStable(locator, stabilityTimeMs, ConfigManager.getExplicitTimeout());
    }

    /**
     * Wait for element to be stable with custom timeout
     * @param locator Element locator
     * @param stabilityTimeMs Stability time in milliseconds
     * @param timeoutSeconds Total timeout in seconds
     * @return WebElement if found and stable
     */
    public WebElement waitForElementToBeStable(By locator, long stabilityTimeMs, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        return customWait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    if (element.isDisplayed()) {
                        String initialText = element.getText();
                        String initialValue = element.getAttribute("value");

                        // Wait for stability period
                        try {
                            Thread.sleep(stabilityTimeMs);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        // Check if element is still the same
                        String currentText = element.getText();
                        String currentValue = element.getAttribute("value");

                        if (initialText.equals(currentText) &&
                            (initialValue == null ? currentValue == null : initialValue.equals(currentValue))) {
                            logger.debug("Element is stable");
                            return element;
                        }
                    }
                } catch (Exception e) {
                    // Element not found or not stable yet
                }
                return null;
            }

            @Override
            public String toString() {
                return "element to be stable for " + stabilityTimeMs + "ms: " + locator;
            }
        });
    }

    /**
     * Execute JavaScript and wait for completion
     * @param script JavaScript to execute
     * @param args Script arguments
     * @return Script result
     */
    public Object executeScriptWithWait(String script, Object... args) {
        Object result = jsExecutor.executeScript(script, args);
        waitForAjaxToComplete();
        return result;
    }

    /**
     * Click element and wait for AJAX completion
     * @param element Element to click
     */
    public void clickAndWaitForAjax(WebElement element) {
        element.click();
        waitForAjaxToComplete();
        logger.debug("Clicked element and waited for AJAX completion");
    }

    /**
     * Click element and wait for specific condition
     * @param element Element to click
     * @param condition Condition to wait for
     * @param <T> Return type of condition
     * @return Condition result
     */
    public <T> T clickAndWaitFor(WebElement element, ExpectedCondition<T> condition) {
        element.click();
        return wait.until(condition);
    }

    /**
     * Type text and wait for AJAX completion
     * @param element Element to type into
     * @param text Text to type
     */
    public void typeAndWaitForAjax(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
        waitForAjaxToComplete();
        logger.debug("Typed text and waited for AJAX completion");
    }

    /**
     * Wait with a custom function condition
     * @param condition Custom condition function
     * @param timeoutSeconds Timeout in seconds
     * @param <T> Return type
     * @return Result of condition
     */
    public <T> T waitFor(Function<WebDriver, T> condition, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(condition::apply);
    }
}