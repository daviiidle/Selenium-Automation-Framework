package utils;

import config.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DebugAgent {
    private static final Logger logger = LogManager.getLogger(DebugAgent.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    /**
     * Analyzes test failures and attempts to find robust alternative selectors
     */
    public static class SelectorAnalyzer {
        private final WebDriver driver;
        private final WebDriverWait wait;

        public SelectorAnalyzer(WebDriver driver) {
            this.driver = driver;
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitTimeout()));
        }

        /**
         * Attempts to find an element using multiple selector strategies
         */
        public WebElement findElementWithFallbacks(By primarySelector, String elementDescription) {
            List<By> selectors = generateAlternativeSelectors(primarySelector, elementDescription);

            for (By selector : selectors) {
                try {
                    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    if (element.isDisplayed() && element.isEnabled()) {
                        logger.info("Successfully found element '{}' using selector: {}",
                                   elementDescription, selector.toString());
                        return element;
                    }
                } catch (Exception e) {
                    logger.debug("Selector failed for '{}': {} - {}",
                                elementDescription, selector.toString(), e.getMessage());
                }
            }

            throw new org.openqa.selenium.NoSuchElementException(
                String.format("Could not find element '%s' using any fallback strategies", elementDescription)
            );
        }

        /**
         * Generates alternative selectors based on the original selector
         */
        private List<By> generateAlternativeSelectors(By originalSelector, String elementDescription) {
            List<By> alternatives = new ArrayList<>();
            alternatives.add(originalSelector); // Start with original

            String selectorString = originalSelector.toString();

            // Add common alternative strategies based on element description
            if (elementDescription.toLowerCase().contains("email")) {
                alternatives.addAll(generateEmailFieldSelectors());
            } else if (elementDescription.toLowerCase().contains("password")) {
                alternatives.addAll(generatePasswordFieldSelectors());
            } else if (elementDescription.toLowerCase().contains("login")) {
                alternatives.addAll(generateLoginButtonSelectors());
            } else if (elementDescription.toLowerCase().contains("search")) {
                alternatives.addAll(generateSearchFieldSelectors());
            }

            // Add generic fallback strategies
            alternatives.addAll(generateGenericFallbacks(selectorString));

            return alternatives;
        }

        private List<By> generateEmailFieldSelectors() {
            return Arrays.asList(
                By.id("Email"),
                By.name("Email"),
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name*='email' i]"),
                By.cssSelector("input[id*='email' i]"),
                By.xpath("//input[contains(@placeholder,'email') or contains(@placeholder,'Email')]"),
                By.xpath("//input[@type='email' or contains(@name,'email') or contains(@id,'email')]")
            );
        }

        private List<By> generatePasswordFieldSelectors() {
            return Arrays.asList(
                By.id("Password"),
                By.name("Password"),
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[name*='password' i]"),
                By.cssSelector("input[id*='password' i]"),
                By.xpath("//input[contains(@placeholder,'password') or contains(@placeholder,'Password')]"),
                By.xpath("//input[@type='password' or contains(@name,'password') or contains(@id,'password')]")
            );
        }

        private List<By> generateLoginButtonSelectors() {
            return Arrays.asList(
                By.cssSelector("input[value='Log in']"),
                By.cssSelector("button[type='submit']"),
                By.cssSelector("input[type='submit']"),
                By.xpath("//input[@value='Log in' or @value='LOGIN' or @value='Login']"),
                By.xpath("//button[contains(text(),'Log in') or contains(text(),'LOGIN') or contains(text(),'Login')]"),
                By.cssSelector(".login-button"),
                By.cssSelector("#login-button")
            );
        }

        private List<By> generateSearchFieldSelectors() {
            return Arrays.asList(
                By.id("small-searchterms"),
                By.name("q"),
                By.cssSelector("input[placeholder*='search' i]"),
                By.cssSelector("input.search-box-text"),
                By.xpath("//input[contains(@placeholder,'search') or contains(@placeholder,'Search')]"),
                By.cssSelector("input[type='search']")
            );
        }

        private List<By> generateGenericFallbacks(String originalSelector) {
            List<By> fallbacks = new ArrayList<>();

            // Extract different parts of the selector for variations
            if (originalSelector.contains("css selector")) {
                String cssSelector = originalSelector.replace("By.cssSelector: ", "");
                fallbacks.addAll(generateCssFallbacks(cssSelector));
            } else if (originalSelector.contains("xpath")) {
                String xpath = originalSelector.replace("By.xpath: ", "");
                fallbacks.addAll(generateXpathFallbacks(xpath));
            }

            return fallbacks;
        }

        private List<By> generateCssFallbacks(String cssSelector) {
            List<By> fallbacks = new ArrayList<>();

            // If selector has ID, try variations
            if (cssSelector.contains("#")) {
                String id = cssSelector.substring(cssSelector.indexOf("#") + 1);
                fallbacks.add(By.id(id));
                fallbacks.add(By.cssSelector("*[id='" + id + "']"));
                fallbacks.add(By.cssSelector("*[id*='" + id + "']"));
            }

            // If selector has class, try variations
            if (cssSelector.contains(".")) {
                String className = cssSelector.substring(cssSelector.indexOf(".") + 1);
                if (!className.contains(" ")) {
                    fallbacks.add(By.className(className));
                    fallbacks.add(By.cssSelector("*[class*='" + className + "']"));
                }
            }

            return fallbacks;
        }

        private List<By> generateXpathFallbacks(String xpath) {
            List<By> fallbacks = new ArrayList<>();

            // Try more flexible XPath variations
            if (xpath.contains("@id=")) {
                String id = extractAttributeValue(xpath, "id");
                if (id != null) {
                    fallbacks.add(By.xpath("//*[contains(@id,'" + id + "')]"));
                    fallbacks.add(By.id(id));
                }
            }

            if (xpath.contains("@class=")) {
                String className = extractAttributeValue(xpath, "class");
                if (className != null) {
                    fallbacks.add(By.xpath("//*[contains(@class,'" + className + "')]"));
                }
            }

            return fallbacks;
        }

        private String extractAttributeValue(String xpath, String attribute) {
            String pattern = "@" + attribute + "='";
            int start = xpath.indexOf(pattern);
            if (start != -1) {
                start += pattern.length();
                int end = xpath.indexOf("'", start);
                if (end != -1) {
                    return xpath.substring(start, end);
                }
            }
            return null;
        }
    }

    /**
     * Analyzes DOM structure to suggest better selectors
     */
    public static class DOMAnalyzer {
        private final WebDriver driver;

        public DOMAnalyzer(WebDriver driver) {
            this.driver = driver;
        }

        /**
         * Analyzes the current page and suggests robust selectors for common elements
         */
        public Map<String, List<String>> analyzePageElements() {
            Map<String, List<String>> elementAnalysis = new HashMap<>();

            try {
                // Analyze common form elements
                elementAnalysis.put("email_fields", analyzeElements("input[type='email'], input[name*='email' i]"));
                elementAnalysis.put("password_fields", analyzeElements("input[type='password']"));
                elementAnalysis.put("submit_buttons", analyzeElements("input[type='submit'], button[type='submit']"));
                elementAnalysis.put("text_inputs", analyzeElements("input[type='text']"));
                elementAnalysis.put("links", analyzeElements("a[href]"));
                elementAnalysis.put("navigation_menus", analyzeElements(".header-menu a, nav a"));

                logger.info("DOM analysis completed. Found {} element categories", elementAnalysis.size());

            } catch (Exception e) {
                logger.error("Error during DOM analysis", e);
            }

            return elementAnalysis;
        }

        private List<String> analyzeElements(String cssSelector) {
            List<String> suggestions = new ArrayList<>();

            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));

                for (int i = 0; i < elements.size() && i < 5; i++) { // Limit to first 5 elements
                    WebElement element = elements.get(i);
                    suggestions.addAll(generateSelectorSuggestions(element));
                }

            } catch (Exception e) {
                logger.debug("Could not analyze elements with selector: {}", cssSelector);
            }

            return suggestions;
        }

        private List<String> generateSelectorSuggestions(WebElement element) {
            List<String> suggestions = new ArrayList<>();

            try {
                // Get element attributes
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String className = element.getAttribute("class");
                String tagName = element.getTagName();
                String type = element.getAttribute("type");
                String placeholder = element.getAttribute("placeholder");
                String value = element.getAttribute("value");

                // Generate ID-based selectors (highest priority)
                if (id != null && !id.trim().isEmpty()) {
                    suggestions.add("#" + id);
                    suggestions.add(tagName + "#" + id);
                }

                // Generate name-based selectors
                if (name != null && !name.trim().isEmpty()) {
                    suggestions.add("[name='" + name + "']");
                    suggestions.add(tagName + "[name='" + name + "']");
                }

                // Generate type-based selectors for inputs
                if ("input".equals(tagName) && type != null) {
                    suggestions.add("input[type='" + type + "']");
                }

                // Generate class-based selectors (be careful with multiple classes)
                if (className != null && !className.trim().isEmpty()) {
                    String[] classes = className.split("\\s+");
                    if (classes.length == 1) {
                        suggestions.add("." + classes[0]);
                    }
                }

                // Generate attribute-based selectors
                if (placeholder != null && !placeholder.trim().isEmpty()) {
                    suggestions.add("[placeholder='" + placeholder + "']");
                }

                if (value != null && !value.trim().isEmpty()) {
                    suggestions.add("[value='" + value + "']");
                }

            } catch (Exception e) {
                logger.debug("Error generating selector suggestions for element", e);
            }

            return suggestions;
        }
    }

    /**
     * Provides failure recovery mechanisms
     */
    public static class FailureRecovery {

        /**
         * Attempts to recover from element not found errors
         */
        public static WebElement recoverElement(WebDriver driver, By selector, String elementName) {
            logger.info("Attempting to recover element: {}", elementName);

            SelectorAnalyzer analyzer = new SelectorAnalyzer(driver);

            try {
                return analyzer.findElementWithFallbacks(selector, elementName);
            } catch (Exception e) {
                logger.error("Failed to recover element '{}' using fallback strategies", elementName, e);

                // Take screenshot for debugging
                ScreenshotUtils.captureScreenshot(driver, "element_recovery_failed_" + elementName);

                // Perform DOM analysis for future improvements
                DOMAnalyzer domAnalyzer = new DOMAnalyzer(driver);
                Map<String, List<String>> analysis = domAnalyzer.analyzePageElements();
                logger.info("DOM analysis results for debugging: {}", analysis);

                throw new org.openqa.selenium.NoSuchElementException("Element recovery failed for: " + elementName);
            }
        }

        /**
         * Validates selector reliability by testing multiple times
         */
        public static boolean validateSelector(WebDriver driver, By selector, int attempts) {
            int successCount = 0;

            for (int i = 0; i < attempts; i++) {
                try {
                    driver.findElement(selector);
                    successCount++;
                } catch (Exception e) {
                    logger.debug("Selector validation attempt {} failed", i + 1);
                }
            }

            double successRate = (double) successCount / attempts;
            logger.info("Selector success rate: {}/{} ({}%)", successCount, attempts, successRate * 100);

            return successRate >= 0.8; // 80% success rate threshold
        }
    }
}