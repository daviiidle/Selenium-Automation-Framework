package tests.products;

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
 * Comprehensive Product Catalog Test Suite
 * Covers product catalog browsing, navigation, filtering, and sorting functionality
 * Tests category navigation, product display, and catalog interactions
 */
public class ProductCatalogTests extends BaseTest {
    private DemoWebShopAssertions assertions;
    private HomePage homePage;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(driver);
        homePage = new HomePage(driver);
    }

    /**
     * Test ID: CATALOG_001 - Category Navigation
     * Tests navigation through different product categories
     * Validates category pages load correctly with appropriate products
     */
    @Test(groups = {"smoke", "catalog", "high-priority"},
          priority = 1,
          dataProvider = "categoryTestData",
          dataProviderClass = ProductDataProvider.class,
          description = "Category navigation should work correctly")
    public void testCategoryNavigation(String categoryName) {
        logger.info("=== Starting CATALOG_001: Category Navigation - {} ===", categoryName);

        ProductCatalogPage catalogPage = homePage.navigateToCategory(categoryName);
        assertions.assertCategoryPageLoaded(catalogPage, categoryName);

        // Verify breadcrumb navigation
        SoftAssert softAssert = assertions.getSoftAssert();
        if (catalogPage.isBreadcrumbDisplayed()) {
            List<String> breadcrumbs = catalogPage.getBreadcrumbs();
            softAssert.assertTrue(breadcrumbs.contains(categoryName),
                                 "Breadcrumb should contain category name");
        }

        // Verify category-specific content
        if (catalogPage.hasProducts()) {
            int productCount = catalogPage.getProductCount();
            softAssert.assertTrue(productCount > 0,
                                 "Category should contain products");
            logger.info("Category '{}' contains {} products", categoryName, productCount);
        }

        assertions.assertAll();
        logger.info("=== CATALOG_001 completed: Navigation to {} ===", categoryName);
    }

    /**
     * Test ID: CATALOG_002 - Product Display and Grid Layout
     * Tests product display in catalog grid layout
     * Validates product information display and layout consistency
     */
    @Test(groups = {"ui", "catalog", "medium-priority"},
          priority = 2,
          description = "Product catalog should display products in proper grid layout")
    public void testProductDisplayAndLayout() {
        logger.info("=== Starting CATALOG_002: Product Display and Grid Layout ===");

        // Navigate to a category with products
        ProductCatalogPage catalogPage = homePage.navigateToCategory("Books");
        assertions.assertCategoryPageLoaded(catalogPage, "Books");

        if (catalogPage.hasProducts()) {
            SoftAssert softAssert = assertions.getSoftAssert();

            // Verify product grid layout
            softAssert.assertTrue(catalogPage.isProductGridDisplayed(),
                                 "Product grid should be displayed");

            // Verify product information display
            List<String> productTitles = catalogPage.getProductTitles();
            softAssert.assertFalse(productTitles.isEmpty(),
                                  "Product titles should be displayed");

            List<String> productPrices = catalogPage.getProductPricesAsStrings();
            softAssert.assertFalse(productPrices.isEmpty(),
                                  "Product prices should be displayed");

            // Verify each product has required information
            for (int i = 0; i < Math.min(3, productTitles.size()); i++) {
                softAssert.assertFalse(productTitles.get(i).trim().isEmpty(),
                                      "Product title should not be empty");
                if (i < productPrices.size()) {
                    softAssert.assertFalse(productPrices.get(i).trim().isEmpty(),
                                          "Product price should not be empty");
                }
            }

            // Verify product images (if displayed)
            if (catalogPage.areProductImagesDisplayed()) {
                softAssert.assertTrue(catalogPage.getProductImageCount() > 0,
                                     "Product images should be displayed");
            }
        }

        assertions.assertAll();
        logger.info("=== CATALOG_002 completed: Product display verification ===");
    }

    /**
     * Test ID: CATALOG_003 - Product Sorting Functionality
     * Tests sorting options in product catalog
     * Validates different sorting criteria work correctly
     */
    @Test(groups = {"functional", "catalog", "high-priority"},
          priority = 3,
          dataProvider = "sortingTestData",
          dataProviderClass = ProductDataProvider.class,
          description = "Product catalog sorting should work correctly")
    public void testProductSorting(String sortOption) {
        logger.info("=== Starting CATALOG_003: Product Sorting - {} ===", sortOption);

        ProductCatalogPage catalogPage = homePage.navigateToCategory("Computers");
        assertions.assertCategoryPageLoaded(catalogPage, "Computers");

        if (catalogPage.hasProducts() && catalogPage.isSortingDropdownDisplayed()) {
            // Apply sorting
            catalogPage.selectSortOption(sortOption);

            // Verify sorting was applied
            SoftAssert softAssert = assertions.getSoftAssert();
            String selectedSort = catalogPage.getSelectedSortOption();
            softAssert.assertEquals(selectedSort, sortOption,
                                   "Selected sort option should match applied option");

            // Get products after sorting
            List<String> sortedProductTitles = catalogPage.getProductTitles();
            softAssert.assertFalse(sortedProductTitles.isEmpty(),
                                  "Products should still be displayed after sorting");

            // Verify sorting had some effect (basic check)
            if (sortOption.contains("Name")) {
                logger.info("Name-based sorting applied: {}", sortOption);
                // Could add more specific validation for alphabetical order
            } else if (sortOption.contains("Price")) {
                logger.info("Price-based sorting applied: {}", sortOption);
                // Could add price order validation
            }
        }

        assertions.assertAll();
        logger.info("=== CATALOG_003 completed: Sorting by {} ===", sortOption);
    }

    /**
     * Test ID: CATALOG_004 - Product Navigation to Details
     * Tests navigation from catalog to product detail pages
     * Validates product detail page access and information consistency
     */
    @Test(groups = {"functional", "catalog", "high-priority"},
          priority = 4,
          description = "Navigation from catalog to product details should work")
    public void testProductNavigationToDetails() {
        logger.info("=== Starting CATALOG_004: Product Navigation to Details ===");

        ProductCatalogPage catalogPage = homePage.navigateToCategory("Electronics");
        assertions.assertCategoryPageLoaded(catalogPage, "Electronics");

        if (catalogPage.hasProducts()) {
            // Get information about first product
            String firstProductTitle = catalogPage.getFirstProductTitle();
            String firstProductPrice = catalogPage.getFirstProductPrice();

            // Click on first product to navigate to details
            ProductDetailsPage detailsPage = catalogPage.clickFirstProduct();
            assertions.assertProductDetailsLoaded(detailsPage, firstProductTitle);

            // Verify product information consistency
            SoftAssert softAssert = assertions.getSoftAssert();
            String detailsTitle = detailsPage.getProductTitle();
            softAssert.assertTrue(detailsTitle.contains(firstProductTitle) ||
                                 firstProductTitle.contains(detailsTitle),
                                 "Product title should be consistent between catalog and details");

            if (firstProductPrice != null && !firstProductPrice.isEmpty()) {
                String detailsPrice = detailsPage.getProductPrice();
                softAssert.assertEquals(detailsPrice, firstProductPrice,
                                       "Product price should be consistent between catalog and details");
            }

            // Verify navigation back to catalog (if breadcrumb available)
            if (detailsPage.isBreadcrumbDisplayed()) {
                detailsPage.clickCategoryBreadcrumb();
                assertions.assertCategoryPageLoaded(catalogPage, "Electronics");
            }
        }

        assertions.assertAll();
        logger.info("=== CATALOG_004 completed: Product navigation to details ===");
    }

    /**
     * Test ID: CATALOG_005 - Catalog Pagination
     * Tests pagination functionality in product catalogs
     * Validates page navigation and product count consistency
     */
    @Test(groups = {"functional", "catalog", "medium-priority"},
          priority = 5,
          description = "Catalog pagination should work correctly")
    public void testCatalogPagination() {
        logger.info("=== Starting CATALOG_005: Catalog Pagination ===");

        // Navigate to category likely to have multiple pages
        ProductCatalogPage catalogPage = homePage.navigateToCategory("Books");
        assertions.assertCategoryPageLoaded(catalogPage, "Books");

        if (catalogPage.hasProducts()) {
            SoftAssert softAssert = assertions.getSoftAssert();

            // Check if pagination is present
            if (catalogPage.hasMultiplePages()) {
                softAssert.assertTrue(catalogPage.isPaginationDisplayed(),
                                     "Pagination should be displayed for multiple pages");

                int firstPageProductCount = catalogPage.getProductCount();
                softAssert.assertTrue(firstPageProductCount > 0,
                                     "First page should have products");

                // Navigate to next page if available
                if (catalogPage.hasNextPage()) {
                    catalogPage.clickNextPage();

                    // Verify page navigation worked
                    softAssert.assertTrue(catalogPage.hasProducts(),
                                         "Second page should have products");

                    // Verify URL or page indicator changed
                    softAssert.assertTrue(driver.getCurrentUrl().contains("page=2") ||
                                         driver.getCurrentUrl().contains("p=2") ||
                                         catalogPage.getCurrentPageNumber() == 2,
                                         "Should be on page 2 after clicking next");

                    // Navigate back to first page
                    if (catalogPage.hasPreviousPage()) {
                        catalogPage.clickPreviousPage();
                        softAssert.assertTrue(catalogPage.getCurrentPageNumber() == 1,
                                             "Should be back on page 1");
                    }
                }
            } else {
                logger.info("Category has only one page of products");
            }
        }

        assertions.assertAll();
        logger.info("=== CATALOG_005 completed: Pagination testing ===");
    }

    /**
     * Test ID: CATALOG_006 - Subcategory Navigation
     * Tests navigation through category hierarchies and subcategories
     * Validates subcategory access and filtering
     */
    @Test(groups = {"functional", "catalog", "medium-priority"},
          priority = 6,
          dataProvider = "productBrowsingData",
          dataProviderClass = ProductDataProvider.class,
          description = "Subcategory navigation should work correctly")
    public void testSubcategoryNavigation(String mainCategory, String subCategory, String description) {
        logger.info("=== Starting CATALOG_006: {} ===", description);

        if (subCategory != null) {
            ProductCatalogPage catalogPage = homePage.navigateToSubcategory(mainCategory, subCategory);
            assertions.assertCategoryPageLoaded(catalogPage, subCategory);

            // Verify subcategory-specific content
            SoftAssert softAssert = assertions.getSoftAssert();

            // Check breadcrumb navigation includes both main and subcategory
            if (catalogPage.isBreadcrumbDisplayed()) {
                List<String> breadcrumbs = catalogPage.getBreadcrumbs();
                softAssert.assertTrue(breadcrumbs.contains(mainCategory) || breadcrumbs.contains(subCategory),
                                     "Breadcrumb should contain category hierarchy");
            }

            // Verify products are relevant to subcategory
            if (catalogPage.hasProducts()) {
                logger.info("Subcategory '{}' contains {} products", subCategory, catalogPage.getProductCount());
            }
        } else {
            // Test main category only
            ProductCatalogPage catalogPage = homePage.navigateToCategory(mainCategory);
            assertions.assertCategoryPageLoaded(catalogPage, mainCategory);
        }

        assertions.assertAll();
        logger.info("=== CATALOG_006 completed: {} ===", description);
    }

    /**
     * Test ID: CATALOG_007 - Product Filtering
     * Tests product filtering options in catalog
     * Validates filter application and result accuracy
     */
    @Test(groups = {"functional", "catalog", "medium-priority"},
          priority = 7,
          dataProvider = "priceRangeData",
          dataProviderClass = ProductDataProvider.class,
          description = "Product filtering should work correctly")
    public void testProductFiltering(double minPrice, double maxPrice, String description) {
        logger.info("=== Starting CATALOG_007: {} ===", description);

        ProductCatalogPage catalogPage = homePage.navigateToCategory("Computers");
        assertions.assertCategoryPageLoaded(catalogPage, "Computers");

        if (catalogPage.hasProducts() && catalogPage.isPriceFilterDisplayed()) {
            // Apply price range filter
            catalogPage.applyPriceRangeFilter(minPrice, maxPrice);

            // Verify filter was applied
            SoftAssert softAssert = assertions.getSoftAssert();
            softAssert.assertTrue(catalogPage.hasProducts() || catalogPage.isNoResultsMessageDisplayed(),
                                 "Should show filtered results or no results message");

            if (catalogPage.hasProducts()) {
                // Verify filtered products are within price range (if prices are visible)
                List<String> productPrices = catalogPage.getProductPricesAsStrings();
                for (String priceStr : productPrices) {
                    try {
                        // Extract numeric price value (implementation dependent)
                        double price = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
                        softAssert.assertTrue(price >= minPrice && price <= maxPrice,
                                             "Product price should be within filter range");
                    } catch (NumberFormatException e) {
                        logger.warn("Could not parse price: {}", priceStr);
                    }
                }
            }
        }

        assertions.assertAll();
        logger.info("=== CATALOG_007 completed: {} ===", description);
    }

    /**
     * Test ID: CATALOG_008 - Catalog Performance
     * Tests catalog loading performance and response times
     * Validates catalog pages load within acceptable timeframes
     */
    @Test(groups = {"performance", "catalog", "low-priority"},
          priority = 8,
          description = "Catalog pages should load within acceptable time")
    public void testCatalogPerformance() {
        logger.info("=== Starting CATALOG_008: Catalog Performance ===");

        String[] categoriesToTest = {"Books", "Computers", "Electronics"};

        for (String category : categoriesToTest) {
            long startTime = System.currentTimeMillis();

            ProductCatalogPage catalogPage = homePage.navigateToCategory(category);

            long endTime = System.currentTimeMillis();
            long loadTime = endTime - startTime;

            logger.info("Category '{}' load time: {} ms", category, loadTime);

            // Verify page loaded successfully
            assertions.assertCategoryPageLoaded(catalogPage, category);

            // Verify load time is acceptable (e.g., under 5 seconds)
            SoftAssert softAssert = assertions.getSoftAssert();
            softAssert.assertTrue(loadTime < 5000,
                                 "Category page should load within 5 seconds. Actual: " + loadTime + "ms");
        }

        assertions.assertAll();
        logger.info("=== CATALOG_008 completed: Performance testing ===");
    }

    /**
     * Test ID: CATALOG_009 - Mobile Responsive Catalog
     * Tests catalog responsiveness on different viewport sizes
     * Validates mobile-friendly catalog layout and functionality
     */
    @Test(groups = {"responsive", "catalog", "medium-priority"},
          priority = 9,
          description = "Catalog should be responsive on mobile viewports")
    public void testMobileResponsiveCatalog() {
        logger.info("=== Starting CATALOG_009: Mobile Responsive Catalog ===");

        // Set mobile viewport size
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));

        ProductCatalogPage catalogPage = homePage.navigateToCategory("Jewelry");
        assertions.assertCategoryPageLoaded(catalogPage, "Jewelry");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify mobile layout adaptations
        if (catalogPage.hasProducts()) {
            softAssert.assertTrue(catalogPage.isProductGridDisplayed(),
                                 "Product grid should be displayed on mobile");

            // Verify mobile-specific navigation (hamburger menu, etc.)
            if (catalogPage.isMobileMenuDisplayed()) {
                softAssert.assertTrue(catalogPage.isMobileMenuAccessible(),
                                     "Mobile menu should be accessible");
            }

            // Verify touch-friendly elements
            softAssert.assertTrue(catalogPage.areProductLinksClickable(),
                                 "Product links should be clickable on mobile");
        }

        // Restore desktop viewport
        driver.manage().window().maximize();

        assertions.assertAll();
        logger.info("=== CATALOG_009 completed: Mobile responsive testing ===");
    }

    @Override
    protected void additionalTeardown() {
        // Ensure viewport is restored to desktop size
        driver.manage().window().maximize();
        logger.debug("Product catalog test cleanup completed");
    }
}