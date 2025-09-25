package dataproviders;

import factories.ProductDataFactory;
import org.testng.annotations.DataProvider;

/**
 * Data provider class for product-related test scenarios
 * Provides test data for product search, browsing, catalog, and cart operations
 */
public class ProductDataProvider {

    /**
     * Provides search test data for comprehensive search functionality testing
     */
    @DataProvider(name = "searchTestData")
    public static Object[][] getSearchTestData() {
        return ProductDataFactory.getSearchTestData();
    }

    /**
     * Provides valid search terms for positive search testing
     */
    @DataProvider(name = "validSearchTerms")
    public static Object[][] getValidSearchTerms() {
        String[] searchTerms = ProductDataFactory.getAllValidSearchTerms();
        Object[][] data = new Object[searchTerms.length][];

        for (int i = 0; i < searchTerms.length; i++) {
            data[i] = new Object[]{searchTerms[i], "Valid search term should return results"};
        }

        return data;
    }

    /**
     * Provides partial search terms for autocomplete testing
     */
    @DataProvider(name = "partialSearchTerms")
    public static Object[][] getPartialSearchTerms() {
        String[] searchTerms = ProductDataFactory.getAllPartialSearchTerms();
        Object[][] data = new Object[searchTerms.length][];

        for (int i = 0; i < searchTerms.length; i++) {
            data[i] = new Object[]{searchTerms[i], "Partial search term should show suggestions"};
        }

        return data;
    }

    /**
     * Provides invalid search terms for negative testing
     */
    @DataProvider(name = "invalidSearchTerms")
    public static Object[][] getInvalidSearchTerms() {
        String[] searchTerms = ProductDataFactory.getAllInvalidSearchTerms();
        Object[][] data = new Object[searchTerms.length][];

        for (int i = 0; i < searchTerms.length; i++) {
            data[i] = new Object[]{searchTerms[i], "Invalid search term should show no results message"};
        }

        return data;
    }

    /**
     * Provides category navigation test data
     */
    @DataProvider(name = "categoryTestData")
    public static Object[][] getCategoryTestData() {
        return ProductDataFactory.getCategoryTestData();
    }

    /**
     * Provides sorting test data for catalog sorting functionality
     */
    @DataProvider(name = "sortingTestData")
    public static Object[][] getSortingTestData() {
        return ProductDataFactory.getSortingTestData();
    }

    /**
     * Provides quantity test data for cart operations
     */
    @DataProvider(name = "quantityTestData")
    public static Object[][] getQuantityTestData() {
        return ProductDataFactory.getQuantityTestData();
    }

    /**
     * Provides comprehensive product browsing scenarios
     */
    @DataProvider(name = "productBrowsingData")
    public static Object[][] getProductBrowsingData() {
        return new Object[][]{
            {"Books", "Fiction", "Browse fiction books category"},
            {"Books", "Science", "Browse science books category"},
            {"Books", "Computing and Internet", "Browse computing books category"},
            {"Computers", "Desktops", "Browse desktop computers category"},
            {"Computers", "Notebooks", "Browse notebook computers category"},
            {"Computers", "Accessories", "Browse computer accessories category"},
            {"Electronics", null, "Browse electronics main category"},
            {"Apparel & Shoes", null, "Browse apparel and shoes category"},
            {"Digital downloads", null, "Browse digital downloads category"},
            {"Jewelry", null, "Browse jewelry category"},
            {"Gift Cards", null, "Browse gift cards category"}
        };
    }

    /**
     * Provides search filter combinations
     */
    @DataProvider(name = "searchFilterData")
    public static Object[][] getSearchFilterData() {
        return new Object[][]{
            {"computer", "Books", "Search computer in Books category"},
            {"computer", "Computers", "Search computer in Computers category"},
            {"health", "Books", "Search health in Books category"},
            {"digital", "Digital downloads", "Search digital in Digital downloads category"},
            {"jewelry", "Jewelry", "Search jewelry in Jewelry category"},
            {"gift", "Gift Cards", "Search gift in Gift Cards category"}
        };
    }

    /**
     * Provides price range test data
     */
    @DataProvider(name = "priceRangeData")
    public static Object[][] getPriceRangeData() {
        return new Object[][]{
            {0, 25, "Low price range products"},
            {25, 100, "Medium price range products"},
            {100, 500, "High price range products"},
            {500, 2000, "Premium price range products"},
            {0, 2000, "All price ranges"}
        };
    }

    /**
     * Provides parallel search data for performance testing
     */
    @DataProvider(name = "parallelSearchData", parallel = true)
    public static Object[][] getParallelSearchData() {
        return new Object[][]{
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 1"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 2"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 3"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 4"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 5"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 6"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 7"},
            {ProductDataFactory.getRandomValidSearchTerm(), "Parallel search test 8"}
        };
    }

    /**
     * Provides cart operation test data with different quantities
     */
    @DataProvider(name = "cartOperationData")
    public static Object[][] getCartOperationData() {
        return new Object[][]{
            {1, "Add single item to cart"},
            {2, "Add two items to cart"},
            {5, "Add five items to cart"},
            {10, "Add ten items to cart"},
            {ProductDataFactory.getRandomQuantity(), "Add random quantity to cart"}
        };
    }

    /**
     * Provides multiple product addition scenarios
     */
    @DataProvider(name = "multipleProductData")
    public static Object[][] getMultipleProductData() {
        return new Object[][]{
            {"Books", "Fiction", 2, "Add fiction book with quantity 2"},
            {"Computers", "Desktops", 1, "Add desktop computer"},
            {"Electronics", null, 3, "Add electronics item with quantity 3"},
            {"Apparel & Shoes", null, 1, "Add apparel item"},
            {"Digital downloads", null, 1, "Add digital download"},
            {"Jewelry", null, 2, "Add jewelry with quantity 2"},
            {"Gift Cards", null, 1, "Add gift card"}
        };
    }

    /**
     * Provides product comparison data
     */
    @DataProvider(name = "productComparisonData")
    public static Object[][] getProductComparisonData() {
        return new Object[][]{
            {"computer", new String[]{"desktop", "laptop", "tablet"}, "Compare computer products"},
            {"book", new String[]{"fiction", "science", "computing"}, "Compare book categories"},
            {"phone", new String[]{"smartphone", "mobile", "cell"}, "Compare phone products"}
        };
    }

    /**
     * Provides wishlist operation test data
     */
    @DataProvider(name = "wishlistOperationData")
    public static Object[][] getWishlistOperationData() {
        return new Object[][]{
            {"Books", "Add book to wishlist"},
            {"Computers", "Add computer to wishlist"},
            {"Electronics", "Add electronics to wishlist"},
            {"Jewelry", "Add jewelry to wishlist"},
            {"Digital downloads", "Add digital download to wishlist"}
        };
    }

    /**
     * Provides edge case search scenarios
     */
    @DataProvider(name = "searchEdgeCases")
    public static Object[][] getSearchEdgeCases() {
        return new Object[][]{
            {"", "Empty search should handle gracefully"},
            {" ", "Space-only search should handle gracefully"},
            {"   multiple   spaces   ", "Multiple spaces should be handled"},
            {"UPPERCASE", "Uppercase search should work"},
            {"lowercase", "Lowercase search should work"},
            {"MiXeD cAsE", "Mixed case search should work"},
            {"123456", "Numeric search should work or show appropriate message"},
            {"@#$%^&*()", "Special characters should be handled"},
            {"very long search term that might cause issues with the search functionality", "Very long search term"},
            {"ñoñ-éñglísh", "Non-English characters should be handled"}
        };
    }

    /**
     * Provides product detail page test scenarios
     */
    @DataProvider(name = "productDetailData")
    public static Object[][] getProductDetailData() {
        return new Object[][]{
            {"Books", "Check book product details page"},
            {"Computers", "Check computer product details page"},
            {"Electronics", "Check electronics product details page"},
            {"Apparel & Shoes", "Check apparel product details page"},
            {"Digital downloads", "Check digital download details page"},
            {"Jewelry", "Check jewelry product details page"},
            {"Gift Cards", "Check gift card details page"}
        };
    }
}