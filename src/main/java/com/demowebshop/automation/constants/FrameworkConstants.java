package com.demowebshop.automation.constants;

/**
 * Framework-wide constants
 */
public final class FrameworkConstants {

    private FrameworkConstants() {
        // Private constructor to prevent instantiation
    }

    // Application URLs
    public static final String BASE_URL = "https://demowebshop.tricentis.com";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String REGISTER_URL = BASE_URL + "/register";
    public static final String CART_URL = BASE_URL + "/cart";
    public static final String CHECKOUT_URL = BASE_URL + "/checkout";

    // Category URLs
    public static final String BOOKS_URL = BASE_URL + "/books";
    public static final String COMPUTERS_URL = BASE_URL + "/computers";
    public static final String ELECTRONICS_URL = BASE_URL + "/electronics";
    public static final String APPAREL_SHOES_URL = BASE_URL + "/apparel-shoes";
    public static final String DIGITAL_DOWNLOADS_URL = BASE_URL + "/digital-downloads";
    public static final String JEWELRY_URL = BASE_URL + "/jewelry";
    public static final String GIFT_CARDS_URL = BASE_URL + "/gift-cards";

    // Computer subcategories
    public static final String DESKTOPS_URL = BASE_URL + "/desktops";
    public static final String NOTEBOOKS_URL = BASE_URL + "/notebooks";
    public static final String ACCESSORIES_URL = BASE_URL + "/accessories";

    // Electronics subcategories
    public static final String CAMERA_PHOTO_URL = BASE_URL + "/camera-photo";
    public static final String CELL_PHONES_URL = BASE_URL + "/cell-phones";

    // File Paths
    public static final String SCREENSHOT_PATH = "target/screenshots/";
    public static final String REPORTS_PATH = "target/reports/";
    public static final String TEST_DATA_PATH = "src/test/resources/testdata/";
    public static final String CONFIG_PATH = "src/main/resources/config/";

    // Wait Times (in seconds)
    public static final int SHORT_WAIT = 5;
    public static final int MEDIUM_WAIT = 10;
    public static final int LONG_WAIT = 30;
    public static final int DEFAULT_EXPLICIT_WAIT = 15;
    public static final int DEFAULT_IMPLICIT_WAIT = 10;
    public static final int PAGE_LOAD_TIMEOUT = 30;

    // Test Data
    public static final String TEST_EMAIL_DOMAIN = "@demotest.com";
    public static final String DEFAULT_PASSWORD = "Test123!";

    // Messages
    public static final String LOGIN_SUCCESS_MESSAGE = "Log in was successful.";
    public static final String REGISTRATION_SUCCESS_MESSAGE = "Your registration completed";
    public static final String LOGOUT_SUCCESS_MESSAGE = "Log out";
    public static final String CART_EMPTY_MESSAGE = "Your Shopping Cart is empty!";
    public static final String INVALID_LOGIN_MESSAGE = "Login was unsuccessful";
    public static final String NEWSLETTER_SUCCESS_MESSAGE = "Thank you for signing up!";

    // Error Messages
    public static final String ELEMENT_NOT_FOUND = "Element not found: ";
    public static final String PAGE_NOT_LOADED = "Page not loaded properly: ";
    public static final String TIMEOUT_EXCEPTION = "Timeout waiting for: ";

    // Browser Settings
    public static final String CHROME_BINARY_PATH = "chrome.binary.path";
    public static final String FIREFOX_BINARY_PATH = "firefox.binary.path";
    public static final String EDGE_BINARY_PATH = "edge.binary.path";

    // Reporting
    public static final String EXTENT_REPORT_NAME = "DemoWebShop_Test_Report";
    public static final String ALLURE_RESULTS_PATH = "target/allure-results/";

    // CSV Headers
    public static final String[] USER_DATA_CSV_HEADERS = {
            "FirstName", "LastName", "Email", "Password", "DateOfBirth", "Gender"
    };

    public static final String[] PRODUCT_DATA_CSV_HEADERS = {
            "ProductName", "Category", "Price", "SKU", "Description"
    };

    // Regular Expressions
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_REGEX = "^[+]?[0-9]{10,15}$";
    public static final String PRICE_REGEX = "\\$?[0-9,]+\\.?[0-9]*";

    // Product Categories for Testing
    public static final String[] MAIN_CATEGORIES = {
            "Books", "Computers", "Electronics", "Apparel & Shoes",
            "Digital downloads", "Jewelry", "Gift Cards"
    };

    public static final String[] COMPUTER_SUBCATEGORIES = {
            "Desktops", "Notebooks", "Accessories"
    };

    public static final String[] ELECTRONICS_SUBCATEGORIES = {
            "Camera, photo", "Cell phones"
    };

    // Test Environment
    public static final String DEMO_ENVIRONMENT = "demo";
    public static final String STAGING_ENVIRONMENT = "staging";
    public static final String PRODUCTION_ENVIRONMENT = "production";

    // Faker Locales
    public static final String DEFAULT_LOCALE = "en";
    public static final String[] SUPPORTED_LOCALES = {"en", "en-US", "en-GB", "de", "fr", "es"};

    // Thread Configuration
    public static final int DEFAULT_THREAD_COUNT = 3;
    public static final int MAX_THREAD_COUNT = 5;

    // Retry Configuration
    public static final int DEFAULT_RETRY_COUNT = 1;
    public static final int MAX_RETRY_COUNT = 3;
}