package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for Product Search Results Page
 * Handles search functionality, advanced search, and results display
 */
public class ProductSearchPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/search";

    public ProductSearchPage(WebDriver driver) {
        super(driver);
    }

    public ProductSearchPage() {
        super();
    }

    // Search Methods

    /**
     * Perform new search with term
     * @param searchTerm Search term
     * @return ProductSearchPage with new results
     */
    public ProductSearchPage searchFor(String searchTerm) {
        By searchInputSelector = SelectorUtils.getProductSelector("product_pages.search_results.search_elements.search_input");
        By searchButtonSelector = SelectorUtils.getProductSelector("product_pages.search_results.search_elements.search_button");

        type(searchInputSelector, searchTerm);
        click(searchButtonSelector);
        waitForPageToLoad();
        logger.info("Performed search for: {}", searchTerm);
        return this;
    }

    /**
     * Perform advanced search
     * @param searchTerm Search term
     * @param category Category to search in
     * @param manufacturer Manufacturer to filter by
     * @return ProductSearchPage with filtered results
     */
    public ProductSearchPage advancedSearch(String searchTerm, String category, String manufacturer) {
        // Fill advanced search term
        By advancedSearchSelector = SelectorUtils.getProductSelector("product_pages.search_results.search_elements.advanced_search");
        type(advancedSearchSelector, searchTerm);

        // Select category if provided
        if (category != null && !category.isEmpty()) {
            By categorySelector = SelectorUtils.getProductSelector("product_pages.search_results.search_elements.category_dropdown");
            Select categorySelect = new Select(findElement(categorySelector));
            categorySelect.selectByVisibleText(category);
        }

        // Select manufacturer if provided
        if (manufacturer != null && !manufacturer.isEmpty()) {
            By manufacturerSelector = SelectorUtils.getProductSelector("product_pages.search_results.search_elements.manufacturer_dropdown");
            Select manufacturerSelect = new Select(findElement(manufacturerSelector));
            manufacturerSelect.selectByVisibleText(manufacturer);
        }

        // Execute search
        By searchButtonSelector = SelectorUtils.getProductSelector("product_pages.search_results.search_elements.search_button");
        click(searchButtonSelector);
        waitForPageToLoad();
        logger.info("Performed advanced search");
        return this;
    }

    // Results Methods

    /**
     * Get search results count
     * @return Number of results found
     */
    public int getResultsCount() {
        try {
            By resultsSelector = SelectorUtils.getProductSelector("product_pages.search_results.results.results_count");
            String resultsText = getText(resultsSelector);
            // Extract number from text like "16 item(s)"
            return Integer.parseInt(resultsText.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if search returned no results
     * @return true if no results found
     */
    public boolean hasNoResults() {
        try {
            By noResultsSelector = SelectorUtils.getProductSelector("product_pages.search_results.results.no_results");
            return isElementDisplayed(noResultsSelector);
        } catch (Exception e) {
            return getResultsCount() == 0;
        }
    }

    /**
     * Get all search result products
     * @return List of ProductElement objects
     */
    public List<ProductCatalogPage.ProductElement> getSearchResults() {
        // Reuse ProductElement from ProductCatalogPage as they have similar structure
        By productItemsSelector = SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.product_item");
        List<WebElement> productElements = findElements(productItemsSelector);

        return productElements.stream()
                .map(element -> new ProductCatalogPage.ProductElement(element, driver))
                .collect(Collectors.toList());
    }

    /**
     * Check if search results are displayed
     * @return true if search results are visible
     */
    public boolean isSearchResultsDisplayed() {
        try {
            By resultsContainerSelector = SelectorUtils.getProductSelector("product_pages.category_listing.product_grid.container");
            return isElementDisplayed(resultsContainerSelector) && !hasNoResults();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if search has results
     * @return true if search returned results
     */
    public boolean hasSearchResults() {
        return getResultsCount() > 0 && !hasNoResults();
    }

    /**
     * Get search result count (alias for getResultsCount)
     * @return Number of search results
     */
    public int getSearchResultCount() {
        return getResultsCount();
    }

    /**
     * Get all search result titles
     * @return List of product titles from search results
     */
    public List<String> getSearchResultTitles() {
        List<ProductCatalogPage.ProductElement> results = getSearchResults();
        return results.stream()
                .map(ProductCatalogPage.ProductElement::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Check if no results message is displayed (alias for hasNoResults)
     * @return true if no results message is shown
     */
    public boolean isNoResultsMessageDisplayed() {
        return hasNoResults();
    }

    /**
     * Get the search term that was used
     * @return Search term from URL or page
     */
    public String getSearchTerm() {
        try {
            // Try to get from URL parameter
            String currentUrl = getCurrentUrl();
            if (currentUrl.contains("q=")) {
                String[] parts = currentUrl.split("q=");
                if (parts.length > 1) {
                    return parts[1].split("&")[0];
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if search results have multiple pages
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
     * Check if sorting dropdown is displayed
     * @return true if sort options are available
     */
    public boolean isSortingDropdownDisplayed() {
        try {
            By sortSelector = By.cssSelector(".sort-dropdown, select[name*='sort']");
            return isElementDisplayed(sortSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Select a sort option
     * @param sortOption Sort option to select
     * @return ProductSearchPage for method chaining
     */
    public ProductSearchPage selectSortOption(String sortOption) {
        try {
            By sortSelector = By.cssSelector(".sort-dropdown, select[name*='sort']");
            if (isElementDisplayed(sortSelector)) {
                WebElement sortDropdown = findElement(sortSelector);
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
            if (isElementDisplayed(sortSelector)) {
                WebElement sortDropdown = findElement(sortSelector);
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(sortDropdown);
                return select.getFirstSelectedOption().getText();
            }
        } catch (Exception e) {
            logger.warn("Could not get selected sort option");
        }
        return "";
    }

    // Page Validation

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN);
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }
}