package tests.navigation;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.demowebshop.automation.pages.ProductCatalogPage;
import com.demowebshop.automation.pages.HomePage;

public class HomePageTests extends BaseTest {

    @Test(groups = {"smoke", "navigation"}, priority = 1)
    public void testHomePageLoad() {
        HomePage homePage = getHomePage();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded successfully");
        Assert.assertFalse(homePage.getPageTitle().isEmpty(), "Page title should not be empty");

        logger.info("Home page load test completed");
    }

    @Test(groups = {"ui", "navigation"}, priority = 2)
    public void testNavigationMenuElements() {
        HomePage homePage = getHomePage();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        Assert.assertTrue(homePage.isLoginLinkDisplayed(), "Login link should be displayed");
        Assert.assertTrue(homePage.isRegisterLinkDisplayed(), "Register link should be displayed");
        Assert.assertTrue(homePage.isShoppingCartLinkDisplayed(), "Shopping cart link should be displayed");

        logger.info("Navigation menu elements test completed");
    }

    @Test(groups = {"functional", "navigation"}, priority = 3)
    public void testCategoryNavigation() {
        HomePage homePage = getHomePage();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        ProductCatalogPage booksPage = homePage.navigateToBooks();
        Assert.assertTrue(booksPage.isPageLoaded(), "Books catalog page should be loaded");

        // Navigate back to home and test another category
        homePage.navigateToHomePage();
        ProductCatalogPage computersPage = homePage.navigateToComputers();
        Assert.assertTrue(computersPage.isPageLoaded(), "Computers catalog page should be loaded");

        logger.info("Category navigation test completed");
    }

    @Test(groups = {"functional", "navigation"}, priority = 4)
    public void testFeaturedProducts() {
        HomePage homePage = getHomePage();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        int featuredProductsCount = homePage.getFeaturedProductsCount();
        Assert.assertTrue(featuredProductsCount > 0, "Featured products should be displayed");

        logger.info("Featured products test completed. Count: {}", featuredProductsCount);
    }

    @Test(groups = {"ui", "navigation"}, priority = 5)
    public void testPageElements() {
        HomePage homePage = getHomePage();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        // Test various page elements
        int productItemsCount = homePage.getProductItemsCount();
        Assert.assertTrue(productItemsCount >= 0, "Product items count should be non-negative");

        logger.info("Page elements test completed. Product items: {}", productItemsCount);
    }
}
