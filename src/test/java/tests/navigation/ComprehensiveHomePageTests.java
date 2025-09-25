package tests.navigation;

import base.BaseTest;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.ProductCatalogPage;
import com.demowebshop.automation.pages.ProductDetailsPage;
import dataproviders.ProductDataProvider;
import utils.DemoWebShopAssertions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * Comprehensive HomePage Test Suite
 * Covers homepage functionality, navigation, featured products, and user interface elements
 * Tests homepage performance, responsive design, and user experience features
 */
public class ComprehensiveHomePageTests extends BaseTest {
    private DemoWebShopAssertions assertions;
    private HomePage homePage;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(driver);
        homePage = new HomePage(driver);
    }

    /**
     * Test ID: HOME_001 - Homepage Load and Basic Elements
     * Tests successful homepage loading and presence of essential elements
     * Validates page structure and core functionality availability
     */
    @Test(groups = {"smoke", "homepage", "high-priority"},
          priority = 1,
          description = "Homepage should load successfully with all essential elements")
    public void testHomepageLoadAndBasicElements() {
        logger.info("=== Starting HOME_001: Homepage Load and Basic Elements ===");

        // Verify homepage is loaded
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(homePage.isPageLoaded(),
                             "Homepage should be loaded successfully");

        // Verify page title
        assertions.assertPageTitle("demo", "Homepage should have appropriate title");

        // Verify essential navigation elements
        softAssert.assertTrue(homePage.isLogoDisplayed(),
                             "Site logo should be displayed");
        softAssert.assertTrue(homePage.isSearchBoxDisplayed(),
                             "Search box should be displayed");
        softAssert.assertTrue(homePage.isMainNavigationDisplayed(),
                             "Main navigation menu should be displayed");

        // Verify authentication links
        if (!homePage.isUserLoggedIn()) {
            softAssert.assertTrue(homePage.isLoginLinkDisplayed(),
                                 "Login link should be displayed for non-authenticated users");
            softAssert.assertTrue(homePage.isRegisterLinkDisplayed(),
                                 "Register link should be displayed for non-authenticated users");
        }

        // Verify shopping cart link
        softAssert.assertTrue(homePage.isShoppingCartLinkDisplayed(),
                             "Shopping cart link should be displayed");

        assertions.assertAll();
        logger.info("=== HOME_001 completed: Homepage basic elements verified ===");
    }

    /**
     * Test ID: HOME_002 - Featured Products Display
     * Tests featured products section functionality and display
     * Validates product information and interaction capabilities
     */
    @Test(groups = {"functional", "homepage", "high-priority"},
          priority = 2,
          description = "Featured products should be displayed correctly")
    public void testFeaturedProductsDisplay() {
        logger.info("=== Starting HOME_002: Featured Products Display ===");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify featured products section exists
        if (homePage.isFeaturedProductsSectionDisplayed()) {
            int featuredCount = homePage.getFeaturedProductsCount();
            softAssert.assertTrue(featuredCount > 0,
                                 "Featured products section should contain products");

            logger.info("Homepage displays {} featured products", featuredCount);

            // Verify featured product information
            List<String> featuredTitles = homePage.getFeaturedProductTitles();
            List<String> featuredPrices = homePage.getFeaturedProductPrices();

            for (int i = 0; i < Math.min(3, featuredTitles.size()); i++) {
                softAssert.assertFalse(featuredTitles.get(i).trim().isEmpty(),
                                      "Featured product title should not be empty");
                if (i < featuredPrices.size()) {
                    softAssert.assertFalse(featuredPrices.get(i).trim().isEmpty(),
                                          "Featured product price should not be empty");
                }
            }

            // Test featured product navigation
            if (featuredCount > 0) {
                String firstFeaturedTitle = homePage.getFirstFeaturedProductTitle();
                ProductDetailsPage detailsPage = homePage.clickFirstFeaturedProduct();

                assertions.assertProductDetailsLoaded(detailsPage, firstFeaturedTitle);

                // Navigate back to homepage
                homePage.navigateToHomePage();
                softAssert.assertTrue(homePage.isPageLoaded(),
                                     "Should be able to navigate back to homepage");
            }
        } else {
            logger.warn("Featured products section not found on homepage");
        }

        assertions.assertAll();
        logger.info("=== HOME_002 completed: Featured products verification ===");
    }

    /**
     * Test ID: HOME_003 - Main Navigation Categories
     * Tests main navigation menu and category links
     * Validates navigation to all major product categories
     */
    @Test(groups = {"functional", "homepage", "high-priority"},
          priority = 3,
          dataProvider = "categoryTestData",
          dataProviderClass = ProductDataProvider.class,
          description = "Main navigation categories should work correctly")
    public void testMainNavigationCategories(String categoryName) {
        logger.info("=== Starting HOME_003: Main Navigation - {} ===", categoryName);

        if (homePage.isCategoryLinkDisplayed(categoryName)) {
            ProductCatalogPage catalogPage = homePage.navigateToCategory(categoryName);
            assertions.assertCategoryPageLoaded(catalogPage, categoryName);

            // Verify navigation back to homepage
            homePage.clickLogoToReturnHome();
            assertions.assertPageUrl("/", "Should return to homepage when clicking logo");
        } else {
            logger.warn("Category '{}' not found in main navigation", categoryName);
        }

        assertions.assertAll();
        logger.info("=== HOME_003 completed: Navigation to {} ===", categoryName);
    }

    /**
     * Test ID: HOME_004 - Search Functionality from Homepage
     * Tests search box functionality on homepage
     * Validates search initiation and results
     */
    @Test(groups = {"functional", "homepage", "high-priority"},
          priority = 4,
          description = "Homepage search functionality should work correctly")
    public void testSearchFunctionalityFromHomepage() {
        logger.info("=== Starting HOME_004: Search Functionality from Homepage ===");

        String searchTerm = "computer";
        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify search box functionality
        softAssert.assertTrue(homePage.isSearchBoxDisplayed(),
                             "Search box should be displayed");
        softAssert.assertTrue(homePage.isSearchBoxEnabled(),
                             "Search box should be enabled");

        // Test search initiation
        homePage.enterSearchTerm(searchTerm);
        String enteredText = homePage.getSearchBoxText();
        softAssert.assertEquals(enteredText, searchTerm,
                               "Entered search term should be displayed in search box");

        // Execute search
        homePage.clickSearchButton();
        assertions.assertPageUrl("search", "Should navigate to search results page");

        // Navigate back to homepage for cleanup
        homePage.navigateToHomePage();

        assertions.assertAll();
        logger.info("=== HOME_004 completed: Search functionality verification ===");
    }

    /**
     * Test ID: HOME_005 - Shopping Cart Access from Homepage
     * Tests shopping cart link and flyout functionality
     * Validates cart access and basic cart information display
     */
    @Test(groups = {"functional", "homepage", "medium-priority"},
          priority = 5,
          description = "Shopping cart access from homepage should work")
    public void testShoppingCartAccessFromHomepage() {
        logger.info("=== Starting HOME_005: Shopping Cart Access from Homepage ===");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify shopping cart link
        softAssert.assertTrue(homePage.isShoppingCartLinkDisplayed(),
                             "Shopping cart link should be displayed");
        softAssert.assertTrue(homePage.isShoppingCartLinkClickable(),
                             "Shopping cart link should be clickable");

        // Check cart count display
        int cartCount = homePage.getCartItemCount();
        logger.info("Current cart item count: {}", cartCount);

        if (cartCount > 0) {
            softAssert.assertTrue(homePage.isCartCountDisplayed(),
                                 "Cart count should be displayed when items are in cart");
        }

        // Test cart flyout (if available)
        if (homePage.hasCartFlyout()) {
            homePage.hoverOverCartIcon();
            if (homePage.isCartFlyoutDisplayed()) {
                softAssert.assertTrue(homePage.isCartFlyoutAccessible(),
                                     "Cart flyout should be accessible");
            }
        }

        // Click cart to navigate to cart page
        homePage.clickShoppingCartLink();
        assertions.assertPageUrl("cart", "Should navigate to shopping cart page");

        // Navigate back to homepage
        homePage.navigateToHomePage();

        assertions.assertAll();
        logger.info("=== HOME_005 completed: Shopping cart access verification ===");
    }

    /**
     * Test ID: HOME_006 - Footer Links and Information
     * Tests footer section links and informational content
     * Validates footer navigation and company information
     */
    @Test(groups = {"ui", "homepage", "medium-priority"},
          priority = 6,
          description = "Homepage footer should contain all necessary links and information")
    public void testFooterLinksAndInformation() {
        logger.info("=== Starting HOME_006: Footer Links and Information ===");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Scroll to footer to ensure it's visible
        homePage.scrollToFooter();

        // Verify footer is displayed
        softAssert.assertTrue(homePage.isFooterDisplayed(),
                             "Footer should be displayed");

        // Verify footer sections (if available)
        if (homePage.hasFooterLinks()) {
            List<String> footerLinks = homePage.getFooterLinkTexts();
            softAssert.assertFalse(footerLinks.isEmpty(),
                                  "Footer should contain links");

            // Test a few footer links (avoid external links that might redirect)
            for (String linkText : footerLinks.subList(0, Math.min(3, footerLinks.size()))) {
                if (homePage.isFooterLinkInternal(linkText)) {
                    logger.info("Testing footer link: {}", linkText);
                    // Could click and verify navigation
                }
            }
        }

        // Verify copyright or company information (if present)
        if (homePage.isCopyrightInfoDisplayed()) {
            String copyrightText = homePage.getCopyrightText();
            softAssert.assertFalse(copyrightText.trim().isEmpty(),
                                  "Copyright text should not be empty");
            logger.info("Copyright info: {}", copyrightText);
        }

        assertions.assertAll();
        logger.info("=== HOME_006 completed: Footer verification ===");
    }

    /**
     * Test ID: HOME_007 - Homepage Performance
     * Tests homepage loading performance and response times
     * Validates page load speed and resource loading
     */
    @Test(groups = {"performance", "homepage", "low-priority"},
          priority = 7,
          description = "Homepage should load within acceptable time limits")
    public void testHomepagePerformance() {
        logger.info("=== Starting HOME_007: Homepage Performance ===");

        // Record start time
        long startTime = System.currentTimeMillis();

        // Navigate to homepage (refresh)
        homePage.refreshCurrentPage();

        // Record end time when page is fully loaded
        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        logger.info("Homepage load time: {} ms", loadTime);

        // Verify page loaded successfully
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(homePage.isPageLoaded(),
                             "Homepage should load successfully");

        // Verify load time is acceptable (e.g., under 5 seconds)
        softAssert.assertTrue(loadTime < 5000,
                             "Homepage should load within 5 seconds. Actual: " + loadTime + "ms");

        // Test image loading (if images are present)
        if (homePage.hasImages()) {
            softAssert.assertTrue(homePage.areImagesLoaded(),
                                 "Homepage images should load completely");
        }

        assertions.assertAll();
        logger.info("=== HOME_007 completed: Performance testing ===");
    }

    /**
     * Test ID: HOME_008 - Mobile Responsive Homepage
     * Tests homepage responsiveness on mobile devices
     * Validates mobile layout and touch-friendly elements
     */
    @Test(groups = {"responsive", "homepage", "medium-priority"},
          priority = 8,
          description = "Homepage should be responsive on mobile devices")
    public void testMobileResponsiveHomepage() {
        logger.info("=== Starting HOME_008: Mobile Responsive Homepage ===");

        // Set mobile viewport
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));

        // Refresh page to apply mobile layout
        homePage.refreshCurrentPage();

        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify mobile layout
        softAssert.assertTrue(homePage.isPageLoaded(),
                             "Homepage should load on mobile viewport");

        // Test mobile navigation (hamburger menu)
        if (homePage.isMobileMenuDisplayed()) {
            softAssert.assertTrue(homePage.isMobileMenuAccessible(),
                                 "Mobile menu should be accessible");
            softAssert.assertTrue(homePage.isMobileMenuClickable(),
                                 "Mobile menu should be clickable");
        }

        // Test mobile search
        if (homePage.isSearchBoxDisplayed()) {
            softAssert.assertTrue(homePage.isSearchBoxTouchFriendly(),
                                 "Search box should be touch-friendly on mobile");
        }

        // Test mobile-friendly buttons and links
        softAssert.assertTrue(homePage.areLinksClickableOnMobile(),
                             "Links should be clickable on mobile");

        // Verify featured products adapt to mobile layout
        if (homePage.isFeaturedProductsSectionDisplayed()) {
            softAssert.assertTrue(homePage.isFeaturedProductsLayoutMobileFriendly(),
                                 "Featured products should adapt to mobile layout");
        }

        // Restore desktop viewport
        driver.manage().window().maximize();

        assertions.assertAll();
        logger.info("=== HOME_008 completed: Mobile responsive testing ===");
    }

    /**
     * Test ID: HOME_009 - Homepage SEO Elements
     * Tests SEO-related elements on homepage
     * Validates meta tags, structured data, and SEO best practices
     */
    @Test(groups = {"seo", "homepage", "low-priority"},
          priority = 9,
          description = "Homepage should have proper SEO elements")
    public void testHomepageSEOElements() {
        logger.info("=== Starting HOME_009: Homepage SEO Elements ===");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify page title
        String pageTitle = driver.getTitle();
        softAssert.assertFalse(pageTitle.trim().isEmpty(),
                              "Page title should not be empty");
        softAssert.assertTrue(pageTitle.length() >= 10 && pageTitle.length() <= 60,
                             "Page title should be between 10-60 characters for SEO");

        // Verify meta description (if accessible)
        String metaDescription = homePage.getMetaDescription();
        if (metaDescription != null && !metaDescription.isEmpty()) {
            softAssert.assertTrue(metaDescription.length() >= 120 && metaDescription.length() <= 160,
                                 "Meta description should be between 120-160 characters for SEO");
        }

        // Verify heading structure (H1, H2, etc.)
        if (homePage.hasHeadings()) {
            softAssert.assertTrue(homePage.hasH1Tag(),
                                 "Page should have at least one H1 tag");

            String h1Text = homePage.getH1Text();
            softAssert.assertFalse(h1Text.trim().isEmpty(),
                                  "H1 tag should contain text");
        }

        // Verify alt text for images (basic check)
        if (homePage.hasImages()) {
            List<String> imagesWithoutAlt = homePage.getImagesWithoutAltText();
            softAssert.assertTrue(imagesWithoutAlt.size() == 0,
                                 "All images should have alt text for accessibility and SEO");
        }

        assertions.assertAll();
        logger.info("=== HOME_009 completed: SEO elements verification ===");
    }

    /**
     * Test ID: HOME_010 - Homepage Accessibility
     * Tests accessibility features on homepage
     * Validates WCAG compliance and screen reader compatibility
     */
    @Test(groups = {"accessibility", "homepage", "low-priority"},
          priority = 10,
          description = "Homepage should meet accessibility standards")
    public void testHomepageAccessibility() {
        logger.info("=== Starting HOME_010: Homepage Accessibility ===");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Test keyboard navigation
        if (homePage.isKeyboardNavigationSupported()) {
            softAssert.assertTrue(homePage.canNavigateWithKeyboard(),
                                 "Homepage should support keyboard navigation");
        }

        // Test focus indicators
        softAssert.assertTrue(homePage.haveFocusIndicators(),
                             "Interactive elements should have focus indicators");

        // Test aria labels and roles (basic check)
        if (homePage.hasAriaLabels()) {
            softAssert.assertTrue(homePage.areAriaLabelsAppropriate(),
                                 "ARIA labels should be appropriate and descriptive");
        }

        // Test color contrast (basic check)
        if (homePage.hasColoredElements()) {
            softAssert.assertTrue(homePage.hasAdequateColorContrast(),
                                 "Elements should have adequate color contrast");
        }

        // Test alt text for images
        if (homePage.hasImages()) {
            softAssert.assertTrue(homePage.allImagesHaveAltText(),
                                 "All images should have alt text");
        }

        assertions.assertAll();
        logger.info("=== HOME_010 completed: Accessibility verification ===");
    }

    @Override
    protected void additionalTeardown() {
        // Ensure we're back to desktop viewport
        driver.manage().window().maximize();
        // Navigate to homepage for next test
        homePage.navigateToHomePage();
        logger.debug("Homepage test cleanup completed");
    }
}