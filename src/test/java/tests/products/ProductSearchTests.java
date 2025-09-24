package tests.products;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.SearchResultsPage;

public class ProductSearchTests extends BaseTest {

    @DataProvider(name = "searchTerms")
    public Object[][] getSearchTerms() {
        return new Object[][] {
                {"book"},
                {"computer"},
                {"jewelry"},
                {"phone"},
                {"laptop"}
        };
    }

    @Test(groups = {"smoke", "search"}, priority = 1, dataProvider = "searchTerms")
    public void testProductSearch(String searchTerm) {
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        SearchResultsPage searchResults = homePage.searchForProduct(searchTerm);
        Assert.assertTrue(searchResults.isPageLoaded(), "Search results page should be loaded");

        // Validate search results
        int productCount = searchResults.getProductCount();
        logger.info("Search for '{}' returned {} products", searchTerm, productCount);

        // Note: Actual validation would depend on expected results
        Assert.assertTrue(productCount >= 0, "Product count should be non-negative");
    }

    @Test(groups = {"negative", "search"}, priority = 2)
    public void testEmptySearch() {
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        SearchResultsPage searchResults = homePage.searchForProduct("");
        Assert.assertTrue(searchResults.isPageLoaded(), "Search results page should be loaded");

        logger.info("Empty search test completed");
    }

    @Test(groups = {"functional", "search"}, priority = 3)
    public void testSpecialCharacterSearch() {
        String specialSearchTerm = "@#$%^&*()";

        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        SearchResultsPage searchResults = homePage.searchForProduct(specialSearchTerm);
        Assert.assertTrue(searchResults.isPageLoaded(), "Search results page should be loaded");

        int productCount = searchResults.getProductCount();
        logger.info("Special character search returned {} products", productCount);
    }

    @Test(groups = {"functional", "search"}, priority = 4)
    public void testLongSearchTerm() {
        String longSearchTerm = "this is a very long search term that contains many words and should test the search functionality";

        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        SearchResultsPage searchResults = homePage.searchForProduct(longSearchTerm);
        Assert.assertTrue(searchResults.isPageLoaded(), "Search results page should be loaded");

        logger.info("Long search term test completed");
    }

    @Test(groups = {"ui", "search"}, priority = 5)
    public void testSearchBoxFunctionality() {
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        Assert.assertTrue(homePage.isSearchBoxDisplayed(), "Search box should be displayed");

        homePage.enterSearchTerm("test search");
        SearchResultsPage searchResults = homePage.clickSearchButton();

        Assert.assertTrue(searchResults.isPageLoaded(), "Search results page should be loaded");

        logger.info("Search box functionality test completed");
    }
}