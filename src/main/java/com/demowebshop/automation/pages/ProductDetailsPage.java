package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for Product Details Page
 * Handles product information display, add to cart, wishlist, and reviews
 */
public class ProductDetailsPage extends BasePage {

    private static final String PAGE_URL_PATTERN_REGEX = ".*/[\\w-]+$";

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage() {
        super();
    }

    // Product Information Methods

    /**
     * Get product title
     * @return Product title text
     */
    public String getProductTitle() {
        try {
            // Try multiple selectors for product title with shorter timeout
            String[] titleSelectors = {
                ".product-name h1",
                "h1[class*='product-name']",
                ".product-title h1",
                ".page-title h1",
                "h1"
            };

            for (String selector : titleSelectors) {
                try {
                    By titleBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(titleBy, 3) != null) {
                        String titleText = getText(titleBy);
                        if (!titleText.trim().isEmpty()) {
                            logger.debug("Found product title with selector '{}': {}", selector, titleText);
                            return titleText;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to original method
            try {
                By titleSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.title");
                if (waitUtils.softWaitForElementToBeVisible(titleSelector, 3) != null) {
                    return getText(titleSelector);
                }
            } catch (Exception ignored) {
                // Final fallback failed
            }

            logger.warn("Product title not found with any selector");
            return "";
        } catch (Exception e) {
            logger.debug("Error getting product title: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Get product price
     * @return Product price text
     */
    public String getProductPrice() {
        By priceSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.price");
        return getText(priceSelector);
    }

    /**
     * Get product old price (if on sale)
     * @return Old price text, or empty string if not on sale
     */
    public String getProductOldPrice() {
        try {
            By oldPriceSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.old_price");
            if (isElementDisplayed(oldPriceSelector)) {
                return getText(oldPriceSelector);
            }
        } catch (Exception e) {
            logger.debug("No old price found for product");
        }
        return "";
    }

    /**
     * Get product availability status
     * @return Availability status text (e.g., "In stock", "Out of stock")
     */
    public String getAvailabilityStatus() {
        try {
            By availabilitySelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.availability");
            return getText(availabilitySelector);
        } catch (Exception e) {
            logger.debug("Availability status not found");
            return "Unknown";
        }
    }

    /**
     * Get product short description
     * @return Short description text
     */
    public String getShortDescription() {
        try {
            By descriptionSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.short_description");
            return getText(descriptionSelector);
        } catch (Exception e) {
            logger.debug("Short description not found");
            return "";
        }
    }

    /**
     * Check if product is in stock
     * @return true if product is available for purchase
     */
    public boolean isProductInStock() {
        String availability = getAvailabilityStatus().toLowerCase();
        return availability.contains("in stock") || availability.contains("available");
    }

    /**
     * Get product price as double value for calculations
     * @return Product price as double
     */
    public double getProductPriceAsDouble() {
        try {
            String priceText = getProductPrice();
            // Remove currency symbols and extract numeric value
            String numericPrice = priceText.replaceAll("[^0-9.]", "");
            return Double.parseDouble(numericPrice);
        } catch (Exception e) {
            logger.warn("Could not parse product price as double: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Select quantity for product
     * @param quantity Number of items to select
     */
    public void selectQuantity(int quantity) {
        try {
            By quantitySelector = SelectorUtils.getProductSelector("product_pages.product_detail.add_to_cart.quantity");
            SelenideElement quantityField = $(quantitySelector);
            clear(quantityField);
            type(quantityField, String.valueOf(quantity));
            logger.info("Selected quantity: {}", quantity);
        } catch (Exception e) {
            logger.warn("Could not set quantity: {}", e.getMessage());
        }
    }

    /**
     * Click add to cart button
     */
    public void clickAddToCart() {
        try {
            By addToCartSelector = SelectorUtils.getProductSelector("product_pages.product_detail.add_to_cart.button");
            click(addToCartSelector);
            logger.info("Clicked add to cart button");
        } catch (Exception e) {
            logger.warn("Could not click add to cart: {}", e.getMessage());
        }
    }

    /**
     * Click home link to return to homepage
     * @return HomePage instance
     */
    public HomePage clickHomeLink() {
        try {
            By homeSelector = SelectorUtils.getHomepageSelector("homepage.header.home_link");
            click(homeSelector);
            logger.info("Clicked home link");
            return new HomePage(driver);
        } catch (Exception e) {
            logger.warn("Could not click home link: {}", e.getMessage());
            return new HomePage(driver);
        }
    }

    /**
     * Check if page has error message
     * @return true if error message is displayed
     */
    public boolean hasErrorMessage() {
        try {
            By errorSelector = By.cssSelector(".message-error, .validation-summary-errors, .error");
            return isElementDisplayed(errorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if product is on sale (has old price)
     * @return true if product has discounted price
     */
    public boolean isProductOnSale() {
        return !getProductOldPrice().isEmpty();
    }

    // Purchase Actions Methods

    /**
     * Set quantity for purchase
     * @param quantity Quantity to purchase
     * @return ProductDetailsPage for method chaining
     */
    public ProductDetailsPage setQuantity(int quantity) {
        By quantitySelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.quantity_input");
        SelenideElement quantityInput = $(quantitySelector);

        quantityInput.clear();
        type(quantityInput, String.valueOf(quantity));
        logger.info("Set quantity to: {}", quantity);
        return this;
    }

    /**
     * Get current quantity value
     * @return Current quantity as integer
     */
    public int getCurrentQuantity() {
        By quantitySelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.quantity_input");
        String quantityValue = getAttribute(quantitySelector, "value");
        try {
            return Integer.parseInt(quantityValue);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse quantity value: {}", quantityValue);
            return 1; // Default quantity
        }
    }

    /**
     * Add product to cart with current quantity
     * @return ProductDetailsPage for method chaining
     */
    public ProductDetailsPage addToCart() {
        By addToCartSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_cart_button");
        click(addToCartSelector);

        // Wait for any AJAX cart updates
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("Added product to cart");
        return this;
    }

    /**
     * Add product to cart with specified quantity
     * @param quantity Number of items to add
     * @return ProductDetailsPage for method chaining
     */
    public ProductDetailsPage addToCart(int quantity) {
        setQuantity(quantity);
        return addToCart();
    }

    /**
     * Add product to wishlist
     * @return ProductDetailsPage for method chaining
     */
    public ProductDetailsPage addToWishlist() {
        try {
            By wishlistSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_wishlist");
            if (isElementDisplayed(wishlistSelector)) {
                click(wishlistSelector);
                logger.info("Added product to wishlist");
            } else {
                logger.warn("Add to wishlist button not found or not available");
            }
        } catch (Exception e) {
            logger.error("Error adding product to wishlist: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Add product to compare list
     * @return ProductDetailsPage for method chaining
     */
    public ProductDetailsPage addToCompareList() {
        try {
            By compareSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_compare");
            if (isElementDisplayed(compareSelector)) {
                click(compareSelector);
                logger.info("Added product to compare list");
            } else {
                logger.warn("Add to compare button not found or not available");
            }
        } catch (Exception e) {
            logger.error("Error adding product to compare list: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if add to cart button is available
     * @return true if add to cart button is displayed and enabled
     */
    public boolean canAddToCart() {
        try {
            By addToCartSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_cart_button");
            return isElementDisplayed(addToCartSelector) && isElementEnabled(addToCartSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if add to wishlist button is available
     * @return true if add to wishlist button is displayed
     */
    public boolean canAddToWishlist() {
        try {
            By wishlistSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_wishlist");
            return isElementDisplayed(wishlistSelector);
        } catch (Exception e) {
            return false;
        }
    }

    // Reviews and Rating Methods

    /**
     * Get product rating display
     * @return Rating text or empty string if no rating
     */
    public String getProductRating() {
        try {
            By ratingSelector = SelectorUtils.getProductSelector("product_pages.product_detail.reviews.rating_display");
            if (isElementDisplayed(ratingSelector)) {
                return getText(ratingSelector);
            }
        } catch (Exception e) {
            logger.debug("Product rating not found");
        }
        return "";
    }

    /**
     * Get review count
     * @return Number of reviews text
     */
    public String getReviewCount() {
        try {
            By reviewCountSelector = SelectorUtils.getProductSelector("product_pages.product_detail.reviews.review_count");
            if (isElementDisplayed(reviewCountSelector)) {
                return getText(reviewCountSelector);
            }
        } catch (Exception e) {
            logger.debug("Review count not found");
        }
        return "0 reviews";
    }

    /**
     * Click write review button
     * @return Review page or stays on same page
     */
    public ProductDetailsPage clickWriteReview() {
        try {
            By writeReviewSelector = SelectorUtils.getProductSelector("product_pages.product_detail.reviews.write_review");
            if (isElementDisplayed(writeReviewSelector)) {
                click(writeReviewSelector);
                logger.info("Clicked write review button");

                // Wait for potential page change or modal
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                logger.warn("Write review button not found");
            }
        } catch (Exception e) {
            logger.error("Error clicking write review: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if product has reviews
     * @return true if product has one or more reviews
     */
    public boolean hasReviews() {
        String reviewCount = getReviewCount();
        return !reviewCount.equals("0 reviews") && !reviewCount.isEmpty();
    }

    /**
     * Check if write review option is available
     * @return true if write review button is displayed
     */
    public boolean canWriteReview() {
        try {
            By writeReviewSelector = SelectorUtils.getProductSelector("product_pages.product_detail.reviews.write_review");
            return isElementDisplayed(writeReviewSelector);
        } catch (Exception e) {
            return false;
        }
    }

    // Product Tags Methods

    /**
     * Get all product tags
     * @return List of tag texts
     */
    public List<String> getProductTags() {
        try {
            By tagLinksSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_tags.tag_link");
            ElementsCollection tagElements = $$(tagLinksSelector);
            return tagElements.stream()
                    .map(SelenideElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.debug("Product tags not found");
            return List.of();
        }
    }

    /**
     * Click on a product tag
     * @param tagName Name of the tag to click
     * @return ProductSearchPage with tag results
     */
    public ProductSearchPage clickProductTag(String tagName) {
        try {
            By tagLinksSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_tags.tag_link");
            ElementsCollection tagElements = $$(tagLinksSelector);

            for (SelenideElement tag : tagElements) {
                if (tag.getText().trim().equalsIgnoreCase(tagName.trim())) {
                    tag.click();
                    logger.info("Clicked product tag: {}", tagName);
                    return new ProductSearchPage(driver);
                }
            }
            throw new IllegalArgumentException("Tag not found: " + tagName);
        } catch (Exception e) {
            logger.error("Error clicking product tag: {}", e.getMessage());
            throw new RuntimeException("Could not click product tag: " + tagName, e);
        }
    }

    /**
     * Check if product has tags
     * @return true if product has one or more tags
     */
    public boolean hasTags() {
        return !getProductTags().isEmpty();
    }

    // Related Products Methods

    /**
     * Get all related products
     * @return List of RelatedProductElement objects
     */
    public List<RelatedProductElement> getRelatedProducts() {
        try {
            By relatedItemsSelector = SelectorUtils.getProductSelector("product_pages.product_detail.related_products.related_item");
            ElementsCollection relatedElements = $$(relatedItemsSelector);

            return relatedElements.stream()
                    .map(element -> new RelatedProductElement(element, driver))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.debug("Related products not found");
            return List.of();
        }
    }

    /**
     * Get related product by index
     * @param index Index of related product (0-based)
     * @return RelatedProductElement
     */
    public RelatedProductElement getRelatedProduct(int index) {
        List<RelatedProductElement> relatedProducts = getRelatedProducts();
        if (index >= 0 && index < relatedProducts.size()) {
            return relatedProducts.get(index);
        }
        throw new IndexOutOfBoundsException("Related product index " + index + " is out of bounds. Available: " + relatedProducts.size());
    }

    /**
     * Click on related product by index
     * @param index Index of related product to click
     * @return ProductDetailsPage for the related product
     */
    public ProductDetailsPage clickRelatedProduct(int index) {
        RelatedProductElement relatedProduct = getRelatedProduct(index);
        relatedProduct.click();
        logger.info("Clicked related product at index: {}", index);
        return new ProductDetailsPage(driver);
    }

    /**
     * Check if product has related products
     * @return true if related products are available
     */
    public boolean hasRelatedProducts() {
        return !getRelatedProducts().isEmpty();
    }

    // Navigation Methods

    /**
     * Navigate back to previous page
     * @return BasePage (could be catalog or search page)
     */
    public BasePage navigateBack() {
        super.goBack();
        return new BasePage(driver) {
            @Override
            public boolean isPageLoaded() {
                return true;
            }

            @Override
            public String getPageUrlPattern() {
                return "";
            }
        };
    }

    // Display Check Methods

    /**
     * Check if product title is displayed
     * @return true if product title is visible
     */
    public boolean isProductTitleDisplayed() {
        try {
            By titleSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.title");
            return isElementDisplayed(titleSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if product price is displayed
     * @return true if product price is visible
     */
    public boolean isProductPriceDisplayed() {
        try {
            // Try multiple selectors for product price with shorter timeout
            String[] priceSelectors = {
                ".price-value-1",
                "[class*='price-value']",
                ".price",
                "[class*='price']",
                ".product-price",
                ".current-price"
            };

            for (String selector : priceSelectors) {
                try {
                    By priceBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(priceBy, 3) != null) {
                        String priceText = getText(priceBy);
                        if (!priceText.trim().isEmpty()) {
                            logger.debug("Found product price with selector '{}': {}", selector, priceText);
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to original method
            try {
                By priceSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.price");
                if (waitUtils.softWaitForElementToBeVisible(priceSelector, 3) != null) {
                    return true;
                }
            } catch (Exception ignored) {
                // Fallback failed
            }

            logger.debug("Product price not found with any selector");
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for product price: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if add to cart button is displayed
     * @return true if add to cart button is visible
     */
    public boolean isAddToCartButtonDisplayed() {
        try {
            // Try multiple selectors for add to cart button with shorter timeout
            String[] addToCartSelectors = {
                "input[value='Add to cart']",
                "button[type='submit']",
                ".add-to-cart-button",
                "#add-to-cart-button",
                "input[type='submit'][value*='cart']",
                "button:contains('Add to cart')",
                ".product-box-add-to-cart-button"
            };

            for (String selector : addToCartSelectors) {
                try {
                    By cartBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(cartBy, 3) != null) {
                        logger.debug("Found add to cart button with selector: {}", selector);
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to original method
            try {
                By addToCartSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_cart_button");
                if (waitUtils.softWaitForElementToBeVisible(addToCartSelector, 3) != null) {
                    return true;
                }
            } catch (Exception ignored) {
                // Try another fallback path
                try {
                    By addToCartSelector = SelectorUtils.getProductSelector("product_pages.product_detail.purchase_options.add_to_cart");
                    if (waitUtils.softWaitForElementToBeVisible(addToCartSelector, 3) != null) {
                        return true;
                    }
                } catch (Exception ignored2) {
                    // All fallbacks failed
                }
            }

            logger.debug("Add to cart button not found with any selector");
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for add to cart button: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if product links are clickable
     */
    public boolean areProductLinksClickable() {
        try {
            ElementsCollection links = $$(By.tagName("a"));
            return links.stream().allMatch(link -> link.isEnabled() && link.isDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if mobile menu is displayed
     */
    public boolean isMobileMenuDisplayed() {
        try {
            By mobileMenuSelector = By.cssSelector(".mobile-menu, .hamburger-menu");
            return isElementDisplayed(mobileMenuSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if mobile menu is accessible
     */
    public boolean isMobileMenuAccessible() {
        return isMobileMenuDisplayed() && isElementEnabled(By.cssSelector(".mobile-menu, .hamburger-menu"));
    }

    // Page Validation Methods

    /**
     * Verify that product details page is loaded correctly
     * @return true if page is loaded correctly
     */
    @Override
    public boolean isPageLoaded() {
        try {
            // Try multiple combinations of product page indicators with shorter timeout
            String[] titleSelectors = {".product-name h1", "h1[class*='product-name']", ".product-title h1", "h1"};
            String[] priceSelectors = {".price-value-1", "[class*='price-value']", ".price", "[class*='price']"};

            // Check for title elements with short timeout
            boolean titleFound = false;
            for (String titleSelector : titleSelectors) {
                try {
                    if (waitUtils.softWaitForElementToBeVisible(By.cssSelector(titleSelector), 2) != null) {
                        titleFound = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check for price elements with short timeout
            boolean priceFound = false;
            for (String priceSelector : priceSelectors) {
                try {
                    if (waitUtils.softWaitForElementToBeVisible(By.cssSelector(priceSelector), 2) != null) {
                        priceFound = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // If basic checks fail, try original method as fallback
            if (!titleFound || !priceFound) {
                try {
                    By titleSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.title");
                    By priceSelector = SelectorUtils.getProductSelector("product_pages.product_detail.product_info.price");

                    titleFound = waitUtils.softWaitForElementToBeVisible(titleSelector, 3) != null;
                    priceFound = waitUtils.softWaitForElementToBeVisible(priceSelector, 3) != null;
                } catch (Exception ignored) {
                    // Fallback failed
                }
            }

            boolean pageLoaded = titleFound || priceFound; // At least one should be present
            logger.debug("Product details page loaded check: title={}, price={}, result={}",
                        titleFound, priceFound, pageLoaded);

            return pageLoaded;

        } catch (Exception e) {
            logger.error("Error checking if product details page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the page URL pattern for validation
     * @return Product details page URL pattern regex
     */
    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN_REGEX;
    }

    /**
     * Check if on product details page by URL pattern
     * @return true if on product details page
     */
    public boolean isOnProductDetailsPage() {
        return getCurrentUrl().matches(".*demowebshop.tricentis.com/[\\w-]+$");
    }

    /**
     * Check if breadcrumb is displayed
     * @return true if breadcrumb navigation is visible
     */
    public boolean isBreadcrumbDisplayed() {
        try {
            By breadcrumbSelector = By.cssSelector(".breadcrumb, .bread-crumb, .navigation-path");
            return isElementDisplayed(breadcrumbSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click category breadcrumb to navigate back to category
     * @return ProductCatalogPage
     */
    public ProductCatalogPage clickCategoryBreadcrumb() {
        try {
            By categoryBreadcrumbSelector = By.cssSelector(".breadcrumb a:last-child, .bread-crumb a:last-child");
            if (isElementDisplayed(categoryBreadcrumbSelector)) {
                click(categoryBreadcrumbSelector);
                logger.info("Clicked category breadcrumb");
                return new ProductCatalogPage(driver, "Category");
            } else {
                logger.warn("Category breadcrumb not found");
                goBack();
                return new ProductCatalogPage(driver, "Category");
            }
        } catch (Exception e) {
            logger.error("Error clicking category breadcrumb: {}", e.getMessage());
            goBack();
            return new ProductCatalogPage(driver, "Category");
        }
    }

    // Inner class for related products
    public static class RelatedProductElement {
        private final SelenideElement productElement;
        private final WebDriver driver;

        public RelatedProductElement(SelenideElement productElement, WebDriver driver) {
            this.productElement = productElement;
            this.driver = driver;
        }

        /**
         * Get related product title
         * @return Product title
         */
        public String getTitle() {
            try {
                SelenideElement titleElement = productElement.$(".product-title a");
                return titleElement.getText();
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * Get related product price
         * @return Product price
         */
        public String getPrice() {
            try {
                SelenideElement priceElement = productElement.$(".price");
                return priceElement.getText();
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * Click on related product
         * @return ProductDetailsPage for related product
         */
        public ProductDetailsPage click() {
            try {
                SelenideElement titleElement = productElement.$(".product-title a");
                titleElement.click();
                return new ProductDetailsPage(driver);
            } catch (Exception e) {
                productElement.click();
                return new ProductDetailsPage(driver);
            }
        }

        /**
         * Get underlying WebElement
         * @return WebElement for this related product
         */
        public SelenideElement getElement() {
            return productElement;
        }
    }
}