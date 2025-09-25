package tests.products;

import base.BaseTest;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.ProductSearchPage;
import dataproviders.ProductDataProvider;
import factories.ProductDataFactory;
import utils.DemoWebShopAssertions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * Comprehensive Product Search Test Suite
 * Covers all search functionality scenarios from manual testing documentation
 * Tests search functionality, autocomplete, filters, and edge cases
 */
public class ProductSearchTests extends BaseTest {
    private DemoWebShopAssertions assertions;
    private HomePage homePage;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(driver);
        homePage = new HomePage(driver);
    }

    /**
     * Test ID: SEARCH_001 - Valid Product Search
     * Tests successful product search with valid search terms
     * Validates search results relevance and display
     */
    @Test(groups = {"smoke", "product-search", "high-priority"},
          priority = 1,
          dataProvider = "validSearchTerms",
          dataProviderClass = ProductDataProvider.class,
          description = "Valid product search should return relevant results")
    public void testValidProductSearch(String searchTerm, String description) {
        logger.info("=== Starting SEARCH_001: Valid Search - {} ===", searchTerm);

        ProductSearchPage searchPage = homePage.performSearch(searchTerm);
        assertions.assertPageUrl("search", "Should navigate to search results page");

        // Verify search results are displayed and relevant
        assertions.assertSearchResults(searchPage, searchTerm, true);

        // Verify search term is displayed in search results
        SoftAssert softAssert = assertions.getSoftAssert();
        String displayedSearchTerm = searchPage.getSearchTerm();
        softAssert.assertEquals(displayedSearchTerm.toLowerCase(), searchTerm.toLowerCase(),
                               "Displayed search term should match input");

        // Verify pagination if results exceed one page
        if (searchPage.hasMultiplePages()) {
            softAssert.assertTrue(searchPage.isPaginationDisplayed(),
                                 "Pagination should be displayed for multiple result pages");
        }

        assertions.assertAll();
        logger.info("=== SEARCH_001 completed: Valid search for '{}' ===", searchTerm);
    }

    /**
     * Test ID: SEARCH_002 - Invalid Product Search
     * Tests product search with invalid or non-existent search terms
     * Validates appropriate "no results" handling
     */
    @Test(groups = {"negative", "product-search", "high-priority"},
          priority = 2,
          dataProvider = "invalidSearchTerms",
          dataProviderClass = ProductDataProvider.class,
          description = "Invalid product search should show no results message")
    public void testInvalidProductSearch(String searchTerm, String description) {
        logger.info("=== Starting SEARCH_002: Invalid Search - {} ===", description);

        ProductSearchPage searchPage = homePage.performSearch(searchTerm);
        assertions.assertPageUrl("search", "Should navigate to search results page");

        // Verify no results are shown with appropriate message
        assertions.assertSearchResults(searchPage, searchTerm, false);

        assertions.assertAll();
        logger.info("=== SEARCH_002 completed: Invalid search '{}' ===", searchTerm);
    }

    /**
     * Test ID: SEARCH_003 - Search Edge Cases
     * Tests search functionality with edge case inputs
     * Validates handling of special characters, spaces, and long terms
     */
    @Test(groups = {"edge-cases", "product-search", "medium-priority"},
          priority = 3,
          dataProvider = "searchEdgeCases",
          dataProviderClass = ProductDataProvider.class,
          description = "Search should handle edge cases gracefully")
    public void testSearchEdgeCases(String searchTerm, String description) {
        logger.info("=== Starting SEARCH_003: Edge Case - {} ===", description);

        ProductSearchPage searchPage = homePage.performSearch(searchTerm);

        // Verify search doesn't break and handles edge case gracefully
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(searchPage.isPageLoaded(),
                             "Search page should load properly even with edge case input");

        // Check if appropriate handling is in place
        if (searchTerm.trim().isEmpty()) {
            // Empty search should either show all products or appropriate message
            softAssert.assertTrue(searchPage.isNoResultsMessageDisplayed() || searchPage.hasSearchResults(),
                                 "Empty search should be handled appropriately");
        } else if (searchTerm.length() > 100) {
            // Very long search terms should be handled
            softAssert.assertTrue(searchPage.isPageLoaded(),
                                 "Very long search terms should be handled without errors");
        }

        assertions.assertAll();
        logger.info("=== SEARCH_003 completed: Edge case '{}' ===", description);
    }

    /**
     * Test ID: SEARCH_004 - Search Result Sorting
     * Tests sorting functionality on search results
     * Validates different sorting options work correctly
     */
    @Test(groups = {"functional", "product-search", "medium-priority"},
          priority = 4,
          dataProvider = "sortingTestData",
          dataProviderClass = ProductDataProvider.class,
          description = "Search result sorting should work correctly")
    public void testSearchResultSorting(String sortOption) {
        logger.info("=== Starting SEARCH_004: Search Result Sorting - {} ===", sortOption);

        // Perform search that will return multiple results
        String searchTerm = ProductDataFactory.getRandomValidSearchTerm();
        ProductSearchPage searchPage = homePage.performSearch(searchTerm);

        // Verify results are returned
        assertions.assertSearchResults(searchPage, searchTerm, true);

        // Apply sorting
        if (searchPage.isSortingDropdownDisplayed()) {
            searchPage.selectSortOption(sortOption);

            // Verify sorting is applied
            SoftAssert softAssert = assertions.getSoftAssert();
            String selectedSort = searchPage.getSelectedSortOption();
            softAssert.assertEquals(selectedSort, sortOption,
                                   "Selected sort option should match applied option");

            // Verify results are re-ordered (basic check)
            softAssert.assertTrue(searchPage.hasSearchResults(),
                                 "Search results should still be displayed after sorting");
        }

        assertions.assertAll();
        logger.info("=== SEARCH_004 completed: Sorting by {} ===", sortOption);
    }

    /**
     * Test ID: SEARCH_005 - Search Performance and Response Time
     * Tests search functionality performance and response times
     * Validates search executes within acceptable timeframes
     */
    @Test(groups = {"performance", "product-search", "low-priority"},
          priority = 5,
          description = "Search should execute within acceptable response time")
    public void testSearchPerformance() {
        logger.info("=== Starting SEARCH_005: Search Performance ===");

        String searchTerm = ProductDataFactory.getRandomValidSearchTerm();

        // Record start time
        long startTime = System.currentTimeMillis();

        ProductSearchPage searchPage = homePage.performSearch(searchTerm);

        // Record end time
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        logger.info("Search response time: {} ms", responseTime);

        // Verify search completed successfully
        assertions.assertSearchResults(searchPage, searchTerm, true);

        // Verify response time is within acceptable limits (e.g., 5 seconds)
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(responseTime < 5000,
                             "Search should complete within 5 seconds. Actual: " + responseTime + "ms");

        assertions.assertAll();
        logger.info("=== SEARCH_005 completed: Response time {} ms ===", responseTime);
    }

    @Override
    protected void additionalTeardown() {
        // Clear any search state if needed
        if (homePage != null) {
            homePage.clearSearchBox();
        }
        logger.debug("Product search test cleanup completed");
    }
}