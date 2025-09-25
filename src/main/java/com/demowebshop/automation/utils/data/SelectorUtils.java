package com.demowebshop.automation.utils.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing and retrieving selectors from JSON configuration files
 * Implements fallback selector strategy (Primary → Secondary → XPath)
 */
public class SelectorUtils {
    private static final Logger logger = LogManager.getLogger(SelectorUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, JsonNode> selectorCache = new HashMap<>();

    // Selector files mapping
    private static final Map<String, String> SELECTOR_FILES = Map.of(
            "homepage", "/selectors/homepage-selectors.json",
            "authentication", "/selectors/authentication-selectors.json",
            "product", "/selectors/product-selectors.json",
            "cart", "/selectors/cart-checkout-selectors.json"
    );

    /**
     * Load selectors from JSON file
     * @param selectorType Type of selector file (homepage, authentication, product, cart)
     * @return JsonNode containing selectors
     */
    private static JsonNode loadSelectors(String selectorType) {
        if (selectorCache.containsKey(selectorType)) {
            return selectorCache.get(selectorType);
        }

        String filePath = SELECTOR_FILES.get(selectorType);
        if (filePath == null) {
            throw new IllegalArgumentException("Unknown selector type: " + selectorType);
        }

        try (InputStream inputStream = SelectorUtils.class.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Selector file not found: " + filePath);
            }

            JsonNode selectors = objectMapper.readTree(inputStream);
            selectorCache.put(selectorType, selectors);
            logger.info("Loaded selectors from: {}", filePath);
            return selectors;

        } catch (IOException e) {
            logger.error("Failed to load selectors from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Could not load selector file: " + filePath, e);
        }
    }

    /**
     * Get selector element configuration
     * @param selectorType Selector file type (homepage, authentication, product, cart)
     * @param elementPath Dot-separated path to element (e.g., "header.login_link")
     * @return JsonNode containing selector configuration
     */
    private static JsonNode getSelectorConfig(String selectorType, String elementPath) {
        JsonNode selectors = loadSelectors(selectorType);
        JsonNode element = selectors;

        // Navigate through the JSON path (e.g., homepage.header.login_link)
        String[] pathParts = elementPath.split("\\.");
        for (String part : pathParts) {
            element = element.get(part);
            if (element == null) {
                throw new IllegalArgumentException(
                        String.format("Selector not found: %s in %s", elementPath, selectorType));
            }
        }

        return element;
    }

    /**
     * Get primary selector as By object
     * @param selectorType Selector file type
     * @param elementPath Element path
     * @return By object for primary selector
     */
    public static By getPrimarySelector(String selectorType, String elementPath) {
        JsonNode element = getSelectorConfig(selectorType, elementPath);
        String primarySelector = element.get("primary").asText();
        return createByFromSelector(primarySelector);
    }

    /**
     * Get secondary selector as By object
     * @param selectorType Selector file type
     * @param elementPath Element path
     * @return By object for secondary selector
     */
    public static By getSecondarySelector(String selectorType, String elementPath) {
        JsonNode element = getSelectorConfig(selectorType, elementPath);
        JsonNode secondaryNode = element.get("secondary");
        if (secondaryNode == null) {
            throw new IllegalArgumentException(
                    String.format("Secondary selector not found for: %s.%s", selectorType, elementPath));
        }
        String secondarySelector = secondaryNode.asText();
        return createByFromSelector(secondarySelector);
    }

    /**
     * Get XPath selector as By object
     * @param selectorType Selector file type
     * @param elementPath Element path
     * @return By object for XPath selector
     */
    public static By getXPathSelector(String selectorType, String elementPath) {
        JsonNode element = getSelectorConfig(selectorType, elementPath);
        JsonNode xpathNode = element.get("xpath");
        if (xpathNode == null) {
            throw new IllegalArgumentException(
                    String.format("XPath selector not found for: %s.%s", selectorType, elementPath));
        }
        String xpathSelector = xpathNode.asText();
        return By.xpath(xpathSelector);
    }

    /**
     * Get selector with fallback strategy (Primary → Secondary → XPath)
     * @param selectorType Selector file type
     * @param elementPath Element path
     * @return Array of By objects in fallback order
     */
    public static By[] getFallbackSelectors(String selectorType, String elementPath) {
        JsonNode element = getSelectorConfig(selectorType, elementPath);

        String primarySelector = element.get("primary").asText();
        By primaryBy = createByFromSelector(primarySelector);

        JsonNode secondaryNode = element.get("secondary");
        JsonNode xpathNode = element.get("xpath");

        if (secondaryNode != null && xpathNode != null) {
            By secondaryBy = createByFromSelector(secondaryNode.asText());
            By xpathBy = By.xpath(xpathNode.asText());
            return new By[]{primaryBy, secondaryBy, xpathBy};
        } else if (secondaryNode != null) {
            By secondaryBy = createByFromSelector(secondaryNode.asText());
            return new By[]{primaryBy, secondaryBy};
        } else {
            return new By[]{primaryBy};
        }
    }

    /**
     * Get selector stability rating
     * @param selectorType Selector file type
     * @param elementPath Element path
     * @return Stability rating (High, Medium, Low)
     */
    public static String getSelectorStability(String selectorType, String elementPath) {
        JsonNode element = getSelectorConfig(selectorType, elementPath);
        JsonNode stabilityNode = element.get("stability");
        return stabilityNode != null ? stabilityNode.asText() : "Unknown";
    }

    /**
     * Create By object from selector string
     * @param selector Selector string
     * @return By object
     */
    private static By createByFromSelector(String selector) {
        if (selector == null || selector.trim().isEmpty()) {
            throw new IllegalArgumentException("Selector cannot be null or empty");
        }

        selector = selector.trim();

        // ID selector
        if (selector.startsWith("#")) {
            return By.id(selector.substring(1));
        }
        // Class selector
        else if (selector.startsWith(".") && !selector.contains(" ") && !selector.contains(">")) {
            return By.className(selector.substring(1));
        }
        // Name attribute
        else if (selector.matches("^\\w+\\[name='.*'\\]$")) {
            String nameValue = selector.replaceAll(".*name='([^']+)'.*", "$1");
            return By.name(nameValue);
        }
        // CSS selector (default)
        else {
            return By.cssSelector(selector);
        }
    }

    /**
     * Convenience methods for different page types
     */

    // Homepage selectors
    public static By getHomepageSelector(String elementPath) {
        return getPrimarySelector("homepage", elementPath);
    }

    public static By[] getHomepageFallbackSelectors(String elementPath) {
        return getFallbackSelectors("homepage", elementPath);
    }

    // Authentication selectors
    public static By getAuthSelector(String elementPath) {
        return getPrimarySelector("authentication", elementPath);
    }

    public static By[] getAuthFallbackSelectors(String elementPath) {
        return getFallbackSelectors("authentication", elementPath);
    }

    // Product selectors
    public static By getProductSelector(String elementPath) {
        return getPrimarySelector("product", elementPath);
    }

    public static By[] getProductFallbackSelectors(String elementPath) {
        return getFallbackSelectors("product", elementPath);
    }

    // Cart selectors
    public static By getCartSelector(String elementPath) {
        return getPrimarySelector("cart", elementPath);
    }

    public static By[] getCartFallbackSelectors(String elementPath) {
        return getFallbackSelectors("cart", elementPath);
    }

    /**
     * Clear selector cache (useful for testing or reloading)
     */
    public static void clearCache() {
        selectorCache.clear();
        logger.info("Selector cache cleared");
    }

    /**
     * Get all available selector types
     * @return Array of available selector types
     */
    public static String[] getAvailableSelectorTypes() {
        return SELECTOR_FILES.keySet().toArray(new String[0]);
    }
}