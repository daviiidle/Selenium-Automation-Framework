package factories;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Factory class for generating product-related test data
 * Used for product search, catalog browsing, and cart operations testing
 */
public class ProductDataFactory {
    private static final Logger logger = LogManager.getLogger(ProductDataFactory.class);
    private static final Faker faker = new Faker(new Locale("en-US"));
    private static final Random random = new SecureRandom();

    // DemoWebShop specific product categories and data
    private static final String[] CATEGORIES = {
        "Books", "Computers", "Electronics", "Apparel & Shoes", "Digital downloads",
        "Jewelry", "Gift Cards"
    };

    private static final String[] COMPUTER_SUBCATEGORIES = {
        "Desktops", "Notebooks", "Accessories"
    };

    private static final String[] BOOK_GENRES = {
        "Fiction", "Science", "Computing and Internet", "Health Book"
    };

    private static final String[] SEARCH_TERMS_VALID = {
        "computer", "laptop", "book"
    };

    private static final String[] SEARCH_TERMS_PARTIAL = {
        "comp", "lap"
    };

    private static final String[] SEARCH_TERMS_INVALID = {
        "xyzabc123", "nonexistent"
    };

    private static final String[] SORT_OPTIONS = {
        "Name: A to Z", "Name: Z to A", "Price: Low to High",
        "Price: High to Low", "Created on"
    };

    /**
     * Get a random valid search term
     */
    public static String getRandomValidSearchTerm() {
        String term = SEARCH_TERMS_VALID[random.nextInt(SEARCH_TERMS_VALID.length)];
        logger.debug("Generated valid search term: {}", term);
        return term;
    }

    /**
     * Get a random partial search term (for autocomplete testing)
     */
    public static String getRandomPartialSearchTerm() {
        String term = SEARCH_TERMS_PARTIAL[random.nextInt(SEARCH_TERMS_PARTIAL.length)];
        logger.debug("Generated partial search term: {}", term);
        return term;
    }

    /**
     * Get a random invalid search term (should return no results)
     */
    public static String getRandomInvalidSearchTerm() {
        String term = SEARCH_TERMS_INVALID[random.nextInt(SEARCH_TERMS_INVALID.length)];
        logger.debug("Generated invalid search term: {}", term);
        return term;
    }

    /**
     * Get all valid search terms for data-driven testing
     */
    public static String[] getAllValidSearchTerms() {
        return SEARCH_TERMS_VALID.clone();
    }

    /**
     * Get all partial search terms for data-driven testing
     */
    public static String[] getAllPartialSearchTerms() {
        return SEARCH_TERMS_PARTIAL.clone();
    }

    /**
     * Get all invalid search terms for data-driven testing
     */
    public static String[] getAllInvalidSearchTerms() {
        return SEARCH_TERMS_INVALID.clone();
    }

    /**
     * Get a random category name
     */
    public static String getRandomCategory() {
        String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
        logger.debug("Generated random category: {}", category);
        return category;
    }

    /**
     * Get all available categories
     */
    public static String[] getAllCategories() {
        return CATEGORIES.clone();
    }

    /**
     * Get computer subcategories
     */
    public static String[] getComputerSubcategories() {
        return COMPUTER_SUBCATEGORIES.clone();
    }

    /**
     * Get book genres
     */
    public static String[] getBookGenres() {
        return BOOK_GENRES.clone();
    }

    /**
     * Get a random sort option for catalog sorting tests
     */
    public static String getRandomSortOption() {
        String sortOption = SORT_OPTIONS[random.nextInt(SORT_OPTIONS.length)];
        logger.debug("Generated sort option: {}", sortOption);
        return sortOption;
    }

    /**
     * Get all sort options
     */
    public static String[] getAllSortOptions() {
        return SORT_OPTIONS.clone();
    }

    /**
     * Generate random quantity for cart operations (1-5)
     */
    public static int getRandomQuantity() {
        int quantity = random.nextInt(5) + 1; // 1-5
        logger.debug("Generated random quantity: {}", quantity);
        return quantity;
    }

    /**
     * Generate random quantity for cart operations with specified range
     */
    public static int getRandomQuantity(int min, int max) {
        int quantity = random.nextInt(max - min + 1) + min;
        logger.debug("Generated random quantity: {} (range: {}-{})", quantity, min, max);
        return quantity;
    }

    /**
     * Generate multiple quantities for bulk cart testing
     */
    public static List<Integer> getMultipleQuantities(int count) {
        List<Integer> quantities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            quantities.add(getRandomQuantity());
        }
        logger.debug("Generated {} quantities: {}", count, quantities);
        return quantities;
    }

    /**
     * Generate search data combinations for comprehensive testing
     */
    public static Object[][] getSearchTestData() {
        List<Object[]> testData = new ArrayList<>();

        // Valid search terms
        for (String term : SEARCH_TERMS_VALID) {
            testData.add(new Object[]{term, true, "Valid search term should return results"});
        }

        // Partial search terms
        for (String term : SEARCH_TERMS_PARTIAL) {
            testData.add(new Object[]{term, true, "Partial search term should return results"});
        }

        // Invalid search terms
        for (String term : SEARCH_TERMS_INVALID) {
            testData.add(new Object[]{term, false, "Invalid search term should return no results"});
        }

        // Empty search
        testData.add(new Object[]{"", false, "Empty search should handle gracefully"});

        // Special characters
        testData.add(new Object[]{"@#$%", false, "Special characters should be handled"});

        Object[][] result = testData.toArray(new Object[0][]);
        logger.info("Generated {} search test data combinations", result.length);
        return result;
    }

    /**
     * Generate category navigation test data
     */
    public static Object[][] getCategoryTestData() {
        List<Object[]> testData = new ArrayList<>();

        for (String category : CATEGORIES) {
            testData.add(new Object[]{category});
        }

        Object[][] result = testData.toArray(new Object[0][]);
        logger.info("Generated {} category test data combinations", result.length);
        return result;
    }

    /**
     * Generate sorting test data
     */
    public static Object[][] getSortingTestData() {
        List<Object[]> testData = new ArrayList<>();

        for (String sortOption : SORT_OPTIONS) {
            testData.add(new Object[]{sortOption});
        }

        Object[][] result = testData.toArray(new Object[0][]);
        logger.info("Generated {} sorting test data combinations", result.length);
        return result;
    }

    /**
     * Generate quantity test data for cart operations
     */
    public static Object[][] getQuantityTestData() {
        Object[][] testData = {
            {1, "Single item quantity"},
            {2, "Small quantity"},
            {5, "Medium quantity"},
            {10, "Large quantity"},
            {0, "Zero quantity for removal testing"} // Edge case
        };

        logger.info("Generated {} quantity test data combinations", testData.length);
        return testData;
    }

    /**
     * Generate realistic product names for testing
     */
    public static String generateProductName() {
        String[] prefixes = {"Premium", "Professional", "Standard", "Deluxe", "Basic"};
        String[] products = {"Laptop", "Desktop", "Monitor", "Keyboard", "Mouse", "Speaker", "Headphones"};
        String[] suffixes = {"Pro", "Plus", "Elite", "Standard", "Lite"};

        String prefix = prefixes[random.nextInt(prefixes.length)];
        String product = products[random.nextInt(products.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];

        return prefix + " " + product + " " + suffix;
    }

    /**
     * Generate realistic price for testing
     */
    public static BigDecimal generatePrice() {
        double price = 10.0 + (1000.0 - 10.0) * random.nextDouble(); // $10 to $1000
        return BigDecimal.valueOf(Math.round(price * 100.0) / 100.0); // Round to 2 decimal places
    }

    /**
     * Generate multiple search terms for parallel testing
     */
    public static List<String> getMultipleSearchTerms(int count) {
        List<String> terms = new ArrayList<>();
        List<String> allTerms = new ArrayList<>(Arrays.asList(SEARCH_TERMS_VALID));
        allTerms.addAll(Arrays.asList(SEARCH_TERMS_PARTIAL));

        for (int i = 0; i < count && i < allTerms.size(); i++) {
            terms.add(allTerms.get(i));
        }

        logger.debug("Generated {} search terms for parallel testing: {}", count, terms);
        return terms;
    }
}