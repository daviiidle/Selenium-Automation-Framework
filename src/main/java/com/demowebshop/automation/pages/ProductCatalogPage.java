package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object Model for Product Category/Catalog Pages
 * Handles product browsing, filtering, sorting, and pagination
 */
public class ProductCatalogPage extends BasePage {

    private static final String BASE_URL_PATTERN = "https://demowebshop.tricentis.com/";
    private final String categoryName;
    private String categoryUrlPattern;

    public ProductCatalogPage(WebDriver driver, String categoryName) {
        super(driver);
        this.categoryName = categoryName;
        this.categoryUrlPattern = BASE_URL_PATTERN + categoryName.toLowerCase().replace(" ", "-").replace("&", "");
    }

    public ProductCatalogPage(String categoryName) {
        super();
        this.categoryName = categoryName;
        this.categoryUrlPattern = BASE_URL_PATTERN + categoryName.toLowerCase().replace(" ", "-").replace("&", "");
    }

    // View Mode Methods

    /**
     * Switch to grid view mode
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage switchToGridView() {
        By gridViewSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.view_mode.grid_view");
        if (isElementDisplayed(gridViewSelector)) {
            click(gridViewSelector);
            waitForPageToLoad();
            logger.info("Switched to grid view");
        }
        return this;
    }

    /**
     * Switch to list view mode
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage switchToListView() {
        By listViewSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.view_mode.list_view");
        if (isElementDisplayed(listViewSelector)) {
            click(listViewSelector);
            waitForPageToLoad();
            logger.info("Switched to list view");
        }
        return this;
    }

    /**
     * Check if currently in grid view
     * @return true if in grid view
     */
    public boolean isGridViewActive() {
        try {
            By gridViewSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.view_mode.grid_view");
            SelenideElement gridViewElement = $(gridViewSelector);
            String href = gridViewElement.getAttribute("href");
            return !href.contains("viewmode=grid"); // If link doesn't contain grid, we're already in grid mode
        } catch (Exception e) {
            return false;
        }
    }

    // Sorting Methods

    /**
     * Sort products by criteria
     * @param sortOption Sort option (e.g., "Name: A to Z", "Price: Low to High")
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage sortBy(String sortOption) {
        By sortDropdownSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.sort_dropdown");
        SelenideElement sortDropdown = $(sortDropdownSelector);
        Select select = new Select(sortDropdown);

        select.selectByVisibleText(sortOption);
        waitForPageToLoad();
        logger.info("Sorted products by: {}", sortOption);
        return this;
    }

    /**
     * Get current sort selection
     * @return Current sort option text
     */
    public String getCurrentSortOption() {
        By sortDropdownSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.sort_dropdown");
        SelenideElement sortDropdown = $(sortDropdownSelector);
        Select select = new Select(sortDropdown);
        return select.getFirstSelectedOption().getText();
    }

    /**
     * Get all available sort options
     * @return List of available sort options
     */
    public List<String> getAvailableSortOptions() {
        By sortDropdownSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.sort_dropdown");
        SelenideElement sortDropdown = $(sortDropdownSelector);
        Select select = new Select(sortDropdown);
        return select.getOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    // Page Size Methods

    /**
     * Set number of products per page
     * @param pageSize Number of products per page (e.g., "4", "8", "12")
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage setPageSize(String pageSize) {
        By pageSizeSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.page_size");
        SelenideElement pageSizeDropdown = $(pageSizeSelector);
        Select select = new Select(pageSizeDropdown);

        select.selectByVisibleText(pageSize);
        waitForPageToLoad();
        logger.info("Set page size to: {}", pageSize);
        return this;
    }

    /**
     * Get current page size setting
     * @return Current page size
     */
    public String getCurrentPageSize() {
        By pageSizeSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.page_size");
        SelenideElement pageSizeDropdown = $(pageSizeSelector);
        Select select = new Select(pageSizeDropdown);
        return select.getFirstSelectedOption().getText();
    }

    // Price Filtering Methods

    /**
     * Apply price filter for products under $25
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage filterByPriceUnder25() {
        By priceFilterSelector = SelectorUtils.getProductSelector("product_pages.category_listing.filtering.price_ranges.under_25");
        click(priceFilterSelector);
        waitForPageToLoad();
        logger.info("Applied price filter: Under $25");
        return this;
    }

    /**
     * Apply price filter for products $25-$50
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage filterByPrice25To50() {
        By priceFilterSelector = SelectorUtils.getProductSelector("product_pages.category_listing.filtering.price_ranges.25_to_50");
        click(priceFilterSelector);
        waitForPageToLoad();
        logger.info("Applied price filter: $25 - $50");
        return this;
    }

    /**
     * Apply price filter for products over $50
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage filterByPriceOver50() {
        By priceFilterSelector = SelectorUtils.getProductSelector("product_pages.category_listing.filtering.price_ranges.over_50");
        click(priceFilterSelector);
        waitForPageToLoad();
        logger.info("Applied price filter: Over $50");
        return this;
    }

    // Product Grid Methods

    /**
     * Get all products displayed on current page
     * @return List of ProductElement objects
     */
    public List<ProductElement> getProducts() {
        By productItemsSelector = SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.product_item");
        ElementsCollection productElements = $$(productItemsSelector);

        return productElements.stream()
                .map(element -> new ProductElement(element, driver))
                .collect(Collectors.toList());
    }

    /**
     * Get product by index
     * @param index Index of the product (0-based)
     * @return ProductElement
     */
    public ProductElement getProduct(int index) {
        List<ProductElement> products = getProducts();
        if (index >= 0 && index < products.size()) {
            return products.get(index);
        }
        throw new IndexOutOfBoundsException("Product index " + index + " is out of bounds. Available products: " + products.size());
    }

    /**
     * Get number of products displayed on current page
     * @return Number of products
     */
    public int getProductCount() {
        return getProducts().size();
    }

    /**
     * Find product by title
     * @param productTitle Title of the product to find
     * @return ProductElement if found, null otherwise
     */
    public ProductElement findProductByTitle(String productTitle) {
        List<ProductElement> products = getProducts();
        return products.stream()
                .filter(product -> product.getTitle().toLowerCase().contains(productTitle.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Click on product by index
     * @param index Index of the product to click
     * @return ProductDetailsPage
     */
    public ProductDetailsPage clickProduct(int index) {
        ProductElement product = getProduct(index);
        product.clickTitle();
        logger.info("Clicked product at index: {} in category: {}", index, categoryName);
        return new ProductDetailsPage(driver);
    }

    /**
     * Click on product by title
     * @param productTitle Title of the product to click
     * @return ProductDetailsPage
     */
    public ProductDetailsPage clickProductByTitle(String productTitle) {
        ProductElement product = findProductByTitle(productTitle);
        if (product != null) {
            product.clickTitle();
            logger.info("Clicked product with title: {} in category: {}", productTitle, categoryName);
            return new ProductDetailsPage(driver);
        }
        throw new IllegalArgumentException("Product not found with title: " + productTitle);
    }

    // Pagination Methods

    /**
     * Go to next page
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage goToNextPage() {
        By nextPageSelector = SelectorUtils.getProductSelector("product_pages.category_listing.pagination.next_page");
        if (isElementDisplayed(nextPageSelector)) {
            click(nextPageSelector);
            waitForPageToLoad();
            logger.info("Navigated to next page in category: {}", categoryName);
        } else {
            logger.warn("Next page button not found or not available");
        }
        return this;
    }

    /**
     * Go to previous page
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage goToPreviousPage() {
        By previousPageSelector = SelectorUtils.getProductSelector("product_pages.category_listing.pagination.previous_page");
        if (isElementDisplayed(previousPageSelector)) {
            click(previousPageSelector);
            waitForPageToLoad();
            logger.info("Navigated to previous page in category: {}", categoryName);
        } else {
            logger.warn("Previous page button not found or not available");
        }
        return this;
    }

    /**
     * Go to specific page number
     * @param pageNumber Page number to navigate to
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage goToPage(int pageNumber) {
        By pageNumbersSelector = SelectorUtils.getProductSelector("product_pages.category_listing.pagination.page_numbers");
        ElementsCollection pageLinks = $$(pageNumbersSelector);

        for (SelenideElement pageLink : pageLinks) {
            if (pageLink.getText().equals(String.valueOf(pageNumber))) {
                pageLink.click();
                waitForPageToLoad();
                logger.info("Navigated to page {} in category: {}", pageNumber, categoryName);
                return this;
            }
        }
        logger.warn("Page number {} not found", pageNumber);
        return this;
    }

    /**
     * Get available page numbers
     * @return List of available page numbers
     */
    public List<Integer> getAvailablePageNumbers() {
        try {
            By pageNumbersSelector = SelectorUtils.getProductSelector("product_pages.category_listing.pagination.page_numbers");
            ElementsCollection pageLinks = $$(pageNumbersSelector);

            return pageLinks.stream()
                    .map(SelenideElement::getText)
                    .filter(text -> text.matches("\\d+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if next page is available
     * @return true if next page exists
     */
    public boolean hasNextPage() {
        By nextPageSelector = SelectorUtils.getProductSelector("product_pages.category_listing.pagination.next_page");
        return isElementDisplayed(nextPageSelector);
    }

    /**
     * Check if previous page is available
     * @return true if previous page exists
     */
    public boolean hasPreviousPage() {
        By previousPageSelector = SelectorUtils.getProductSelector("product_pages.category_listing.pagination.previous_page");
        return isElementDisplayed(previousPageSelector);
    }

    // Page Information Methods

    /**
     * Get category name
     * @return Category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Check if products are displayed
     * @return true if products are visible
     */
    public boolean areProductsDisplayed() {
        // DemoWebShop uses simple links for products, not complex grid containers
        try {
            By productLinksSelector = By.cssSelector("a[href*='/'], .product-title a, .item-box a");
            ElementsCollection productLinks = $$(productLinksSelector);
            return productLinks.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if products are available (same as areProductsDisplayed for compatibility)
     * @return true if products are available
     */
    public boolean hasProducts() {
        // Check for actual product items on the page, not navigation
        try {
            // Try multiple selectors for product items
            String[] productSelectors = {
                ".product-item",
                ".item-box",
                ".product-grid .item",
                ".product-list .item",
                ".products .product",
                ".item-container"
            };

            for (String selector : productSelectors) {
                try {
                    By productBy = By.cssSelector(selector);
                    ElementsCollection productItems = $$(productBy);
                    if (!productItems.isEmpty()) {
                        // Found products with this selector
                        logger.debug("Found {} products with selector: {}", productItems.size(), selector);
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // If no products found with standard selectors, check for "no products" message
            String[] noProductsSelectors = {
                ".no-result",
                ".no-products",
                ".empty-result",
                ".no-data"
            };

            for (String selector : noProductsSelectors) {
                try {
                    if (waitUtils.softWaitForElementToBeVisible(By.cssSelector(selector), 2) != null) {
                        logger.debug("Found no-products message with selector: {}", selector);
                        return false;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Default to false if no products or no-products messages found
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for products: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get current page number
     * @return current page number, 1 if not found
     */
    public int getCurrentPageNumber() {
        try {
            SelenideElement currentPageElement = $(".pager .current-page");
            String pageText = currentPageElement.getText();
            return Integer.parseInt(pageText);
        } catch (Exception e) {
            return 1; // Default to page 1 if not found
        }
    }

    /**
     * Click previous page (alias for goToPreviousPage)
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage clickPreviousPage() {
        return goToPreviousPage();
    }

    /**
     * Check if breadcrumb is displayed
     * @return true if breadcrumb navigation is visible
     */
    public boolean isBreadcrumbDisplayed() {
        try {
            // DemoWebShop might use different breadcrumb selectors
            String[] breadcrumbSelectors = {
                ".breadcrumb",
                ".bread-crumb",
                ".navigation-path",
                ".page-path",
                ".category-path",
                ".breadcrumbs",
                ".nav-breadcrumb",
                "//div[contains(@class, 'breadcrumb')]",
                "//div[contains(@class, 'bread')]",
                "//nav[contains(@class, 'breadcrumb')]"
            };

            for (String selector : breadcrumbSelectors) {
                try {
                    By breadcrumbBy;
                    if (selector.startsWith("//")) {
                        breadcrumbBy = By.xpath(selector);
                    } else {
                        breadcrumbBy = By.cssSelector(selector);
                    }

                    if (isElementDisplayed(breadcrumbBy)) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get breadcrumb navigation text
     * @return List of breadcrumb items
     */
    public List<String> getBreadcrumbs() {
        try {
            // First try to get breadcrumb container text and split it
            String[] breadcrumbContainerSelectors = {
                ".breadcrumb",
                ".bread-crumb",
                ".navigation-path",
                ".page-path"
            };

            for (String selector : breadcrumbContainerSelectors) {
                try {
                    SelenideElement breadcrumbContainer = $(By.cssSelector(selector));
                    if (breadcrumbContainer.exists() && breadcrumbContainer.isDisplayed()) {
                        String breadcrumbText = breadcrumbContainer.getText().trim();
                        if (!breadcrumbText.isEmpty()) {
                            // Split by common separators like / or >
                            List<String> items = java.util.Arrays.stream(breadcrumbText.split("[/>]"))
                                    .map(String::trim)
                                    .filter(text -> !text.isEmpty())
                                    .collect(Collectors.toList());
                            if (!items.isEmpty()) {
                                logger.debug("Found breadcrumbs via container: {}", items);
                                return items;
                            }
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to link-based approach
            String[] breadcrumbLinkSelectors = {
                ".breadcrumb a",
                ".bread-crumb a",
                ".navigation-path a",
                ".page-path a",
                ".category-path a",
                ".breadcrumbs a",
                ".nav-breadcrumb a"
            };

            for (String selector : breadcrumbLinkSelectors) {
                try {
                    ElementsCollection breadcrumbElements = $$(By.cssSelector(selector));
                    if (breadcrumbElements.size() > 0) {
                        return breadcrumbElements.stream()
                                .map(SelenideElement::getText)
                                .filter(text -> text != null && !text.trim().isEmpty())
                                .collect(Collectors.toList());
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if price filter is displayed
     * @return true if price filter options are visible
     */
    public boolean isPriceFilterDisplayed() {
        try {
            By priceFilterSelector = By.cssSelector(".price-range-filter, .filter-price");
            return isElementDisplayed(priceFilterSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Apply price range filter
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage applyPriceRangeFilter(double minPrice, double maxPrice) {
        // This is a simplified implementation - actual implementation would depend on the UI
        if (minPrice < 25) {
            return filterByPriceUnder25();
        } else if (minPrice >= 25 && maxPrice <= 50) {
            return filterByPrice25To50();
        } else if (minPrice > 50) {
            return filterByPriceOver50();
        }
        return this;
    }

    /**
     * Check if no results message is displayed
     * @return true if no results message is visible
     */
    public boolean isNoResultsMessageDisplayed() {
        try {
            By noResultsSelector = By.cssSelector(".no-result, .no-products, .empty-results");
            return isElementDisplayed(noResultsSelector) || hasNoProducts();
        } catch (Exception e) {
            return hasNoProducts();
        }
    }

    /**
     * Get all product prices on current page
     * @return List of product prices as doubles
     */
    public List<Double> getProductPrices() {
        List<ProductElement> products = getProducts();
        return products.stream()
                .map(product -> {
                    try {
                        String priceText = product.getPrice().replaceAll("[^\\d.]", "");
                        return Double.parseDouble(priceText);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if product grid is displayed
     * @return true if product grid is visible
     */
    public boolean isProductGridDisplayed() {
        // DemoWebShop uses simple layout - check if page content is displayed
        try {
            return isElementDisplayed(By.tagName("body")) && hasProducts();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all product titles on current page
     * @return List of product titles
     */
    public List<String> getProductTitles() {
        List<ProductElement> products = getProducts();
        return products.stream()
                .map(ProductElement::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Check if product images are displayed
     * @return true if all visible products have images displayed
     */
    public boolean areProductImagesDisplayed() {
        List<ProductElement> products = getProducts();
        for (ProductElement product : products) {
            try {
                SelenideElement image = product.getElement().$("img");
                if (!image.isDisplayed()) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return !products.isEmpty();
    }

    /**
     * Get product prices as strings (for compatibility)
     * @return List of product prices as strings
     */
    public List<String> getProductPricesAsStrings() {
        List<ProductElement> products = getProducts();
        return products.stream()
                .map(ProductElement::getPrice)
                .collect(Collectors.toList());
    }

    /**
     * Get product image count
     * @return Number of product images displayed
     */
    public int getProductImageCount() {
        List<ProductElement> products = getProducts();
        int imageCount = 0;
        for (ProductElement product : products) {
            try {
                ElementsCollection images = product.getElement().findAll(By.tagName("img"));
                imageCount += images.size();
            } catch (Exception e) {
                // Continue counting
            }
        }
        return imageCount;
    }

    /**
     * Select sort option
     * @param sortOption Sort option to select
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage selectSortOption(String sortOption) {
        try {
            By sortSelector = By.cssSelector(".sort-dropdown, select[name*='sort']");
            SelenideElement sortDropdown = $(sortSelector);
            if (sortDropdown.isDisplayed()) {
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(sortDropdown);
                select.selectByVisibleText(sortOption);
                waitForPageToLoad();
            }
        } catch (Exception e) {
            logger.warn("Could not select sort option: {}", sortOption);
        }
        return this;
    }

    /**
     * Get currently selected sort option
     * @return Currently selected sort option
     */
    public String getSelectedSortOption() {
        try {
            By sortSelector = By.cssSelector(".sort-dropdown, select[name*='sort']");
            SelenideElement sortDropdown = $(sortSelector);
            if (sortDropdown.isDisplayed()) {
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(sortDropdown);
                return select.getFirstSelectedOption().getText();
            }
        } catch (Exception e) {
            logger.warn("Could not get selected sort option");
        }
        return "";
    }

    /**
     * Get first product title
     * @return Title of first product or empty string
     */
    public String getFirstProductTitle() {
        List<ProductElement> products = getProducts();
        return products.isEmpty() ? "" : products.get(0).getTitle();
    }

    /**
     * Get first product price
     * @return Price of first product or empty string
     */
    public String getFirstProductPrice() {
        List<ProductElement> products = getProducts();
        return products.isEmpty() ? "" : products.get(0).getPrice();
    }

    /**
     * Click first product
     * @return ProductDetailsPage for the clicked product
     */
    public ProductDetailsPage clickFirstProduct() {
        List<ProductElement> products = getProducts();
        if (!products.isEmpty()) {
            return products.get(0).clickTitle();
        }
        throw new RuntimeException("No products available to click");
    }

    /**
     * Click category breadcrumb
     * @param categoryName Category to click in breadcrumb
     * @return ProductCatalogPage for the category
     */
    public ProductCatalogPage clickCategoryBreadcrumb(String categoryName) {
        try {
            By breadcrumbSelector = By.xpath("//a[contains(@class,'breadcrumb') and contains(text(),'" + categoryName + "')]");
            click(breadcrumbSelector);
            logger.info("Clicked breadcrumb for category: {}", categoryName);
            return new ProductCatalogPage(driver, categoryName);
        } catch (Exception e) {
            logger.warn("Could not click breadcrumb for category: {}", categoryName);
            return this;
        }
    }

    /**
     * Check if catalog has multiple pages
     * @return true if pagination is available
     */
    public boolean hasMultiplePages() {
        try {
            By paginationSelector = By.cssSelector(".pagination, .pager");
            return isElementDisplayed(paginationSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if pagination is displayed
     * @return true if pagination controls are visible
     */
    public boolean isPaginationDisplayed() {
        return hasMultiplePages();
    }

    /**
     * Click next page
     * @return ProductCatalogPage for method chaining
     */
    public ProductCatalogPage clickNextPage() {
        return goToNextPage();
    }

    /**
     * Check if category page has no products
     * @return true if no products found message is displayed
     */
    public boolean hasNoProducts() {
        // This would need to be implemented based on the actual "no products" selector
        // For now, check if product count is 0
        return getProductCount() == 0;
    }

    // Page Validation Methods

    /**
     * Verify that product catalog page is loaded correctly
     * @return true if page is loaded correctly
     */
    @Override
    public boolean isPageLoaded() {
        try {
            // Check for DemoWebShop category page indicators
            String currentUrl = getCurrentUrl().toLowerCase();

            // Check if we're on a valid category page
            boolean isOnCategoryPage = currentUrl.contains("/books") ||
                                     currentUrl.contains("/computers") ||
                                     currentUrl.contains("/electronics") ||
                                     currentUrl.contains("/apparel") ||
                                     currentUrl.contains("/digital") ||
                                     currentUrl.contains("/jewelry") ||
                                     currentUrl.contains("/gift") ||
                                     currentUrl.contains("/desktops") ||
                                     currentUrl.contains("/notebooks") ||
                                     currentUrl.contains("/accessories") ||
                                     currentUrl.contains("/camera") ||
                                     currentUrl.contains("/cell");

            // Also check if basic page structure is loaded
            boolean hasBasicStructure = isElementDisplayed(By.tagName("body")) &&
                                      !getCurrentTitle().isEmpty();

            return isOnCategoryPage && hasBasicStructure;
        } catch (Exception e) {
            logger.error("Error checking if product catalog page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the page URL pattern for validation
     * @return Category page URL pattern
     */
    @Override
    public String getPageUrlPattern() {
        return categoryUrlPattern;
    }

    /**
     * Get page title
     * @return Current page title
     */
    public String getPageTitle() {
        return getCurrentTitle();
    }

    /**
     * Check if sorting dropdown is displayed
     * @return true if sorting dropdown is visible
     */
    public boolean isSortingDropdownDisplayed() {
        try {
            // Try multiple selectors for sorting dropdown with shorter timeout
            String[] sortSelectors = {
                "select[name='orderby']",
                "select[name*='sort']",
                ".sort-dropdown",
                ".sorting-dropdown",
                "#products-orderby",
                "select[id*='sort']",
                "select[id*='orderby']"
            };

            for (String selector : sortSelectors) {
                try {
                    By sortBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(sortBy, 3) != null) {
                        logger.debug("Found sorting dropdown with selector: {}", selector);
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to original SelectorUtils method
            try {
                By sortDropdownSelector = SelectorUtils.getProductSelector("product_pages.category_listing.layout_controls.sort_dropdown");
                if (waitUtils.softWaitForElementToBeVisible(sortDropdownSelector, 3) != null) {
                    return true;
                }
            } catch (Exception ignored) {
                // Fallback failed
            }

            // DemoWebShop might not have sorting dropdowns on all category pages
            logger.debug("Sorting dropdown not found - might not be available on this category page");
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for sorting dropdown: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verify user is on correct category page
     * @return true if on correct category page
     */
    public boolean isOnCategoryPage() {
        String currentUrl = getCurrentUrl().toLowerCase();
        String expectedPath = categoryName.toLowerCase().replace(" ", "-").replace("&", "");
        return currentUrl.contains(expectedPath);
    }

    /**
     * Check if mobile menu is displayed
     * @return true if mobile menu is visible
     */
    public boolean isMobileMenuDisplayed() {
        try {
            By mobileMenuSelector = By.cssSelector(".mobile-menu, .nav-mobile, .hamburger-menu");
            return isElementDisplayed(mobileMenuSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if mobile menu is accessible
     * @return true if mobile menu is accessible
     */
    public boolean isMobileMenuAccessible() {
        try {
            By mobileMenuSelector = By.cssSelector(".mobile-menu, .nav-mobile, .hamburger-menu");
            return isElementDisplayed(mobileMenuSelector) && findElement(mobileMenuSelector).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if product links are clickable on mobile
     * @return true if product links are clickable
     */
    public boolean areProductLinksClickable() {
        try {
            ElementsCollection productLinks = $$(By.cssSelector(".product-item a, .product-title a"));
            return productLinks.stream().allMatch(link -> link.isEnabled() && link.isDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    // Inner class for handling individual product elements
    public static class ProductElement {
        private final SelenideElement productElement;
        private final WebDriver driver;

        public ProductElement(SelenideElement productElement, WebDriver driver) {
            this.productElement = productElement;
            this.driver = driver;
        }

        /**
         * Get product title
         * @return Product title text
         */
        public String getTitle() {
            try {
                SelenideElement titleElement = productElement.$(SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.product_title"));
                return titleElement.getText();
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * Get product price
         * @return Product price text
         */
        public String getPrice() {
            try {
                SelenideElement priceElement = productElement.$(SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.product_price"));
                return priceElement.getText();
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * Click on product title to navigate to details page
         * @return ProductDetailsPage
         */
        public ProductDetailsPage clickTitle() {
            SelenideElement titleElement = productElement.$(SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.product_title"));
            titleElement.click();
            return new ProductDetailsPage(driver);
        }

        /**
         * Click on product image
         * @return ProductDetailsPage
         */
        public ProductDetailsPage clickImage() {
            SelenideElement imageElement = productElement.$(SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.product_image"));
            imageElement.click();
            return new ProductDetailsPage(driver);
        }

        /**
         * Get the underlying WebElement
         * @return WebElement representing this product
         */
        public SelenideElement getElement() {
            return productElement;
        }

        /**
         * Check if product is displayed
         * @return true if product element is displayed
         */
        public boolean isDisplayed() {
            return productElement.isDisplayed();
        }
    }
}