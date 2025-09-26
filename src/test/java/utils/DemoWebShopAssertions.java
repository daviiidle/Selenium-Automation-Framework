package utils;

import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.LoginPage;
import com.demowebshop.automation.pages.PasswordRecoveryPage;
import com.demowebshop.automation.pages.ProductCatalogPage;
import com.demowebshop.automation.pages.ProductDetailsPage;
import com.demowebshop.automation.pages.ProductSearchPage;
import com.demowebshop.automation.pages.RegisterPage;
import com.demowebshop.automation.pages.ShoppingCartPage;
import com.demowebshop.automation.pages.CheckoutPage;
import com.demowebshop.automation.pages.OrderCompletePage;
import models.User;
import models.Address;
import models.PaymentInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.util.List;

/**
 * Custom assertions class specifically designed for DemoWebShop validation scenarios
 * Provides domain-specific assertion methods for enhanced test readability and maintenance
 */
public class DemoWebShopAssertions {
    private static final Logger logger = LogManager.getLogger(DemoWebShopAssertions.class);
    private final WebDriver driver;
    private final SoftAssert softAssert;

    public DemoWebShopAssertions(WebDriver driver) {
        this.driver = driver;
        this.softAssert = new SoftAssert();
    }

    public DemoWebShopAssertions(WebDriver driver, SoftAssert softAssert) {
        this.driver = driver;
        this.softAssert = softAssert;
    }

    // ========== Authentication Assertions ==========

    /**
     * Assert that user is successfully logged in
     */
    public void assertUserLoggedIn(HomePage homePage, String expectedUserIdentifier) {
        logger.info("Asserting user is logged in with identifier: {}", expectedUserIdentifier);

        softAssert.assertTrue(homePage.isUserLoggedIn(),
                "User should be logged in - login indicator should be visible");

        if (expectedUserIdentifier != null && !expectedUserIdentifier.isEmpty()) {
            softAssert.assertTrue(homePage.getLoggedInUserInfo().contains(expectedUserIdentifier),
                    "Logged in user info should contain: " + expectedUserIdentifier);
        }

        softAssert.assertFalse(homePage.isLoginLinkDisplayed(),
                "Login link should not be displayed when user is logged in");

        softAssert.assertTrue(homePage.isLogoutLinkDisplayed(),
                "Logout link should be displayed when user is logged in");

        logger.info("User login assertion completed");
    }

    /**
     * Assert that user is successfully logged out
     */
    public void assertUserLoggedOut(HomePage homePage) {
        logger.info("Asserting user is logged out");

        softAssert.assertFalse(homePage.isUserLoggedIn(),
                "User should be logged out - login indicator should not be visible");

        softAssert.assertTrue(homePage.isLoginLinkDisplayed(),
                "Login link should be displayed when user is logged out");

        softAssert.assertFalse(homePage.isLogoutLinkDisplayed(),
                "Logout link should not be displayed when user is logged out");

        logger.info("User logout assertion completed");
    }

    /**
     * Assert registration success
     */
    public void assertRegistrationSuccess(RegisterPage registerPage, User expectedUser) {
        logger.info("Asserting registration success for user: {}", expectedUser.getEmail());

        softAssert.assertTrue(registerPage.isRegistrationSuccessful(),
                "Registration should be successful - success message should be displayed");

        String successMessage = registerPage.getRegistrationSuccessMessage();
        softAssert.assertNotNull(successMessage,
                "Registration success message should not be null");

        softAssert.assertTrue(successMessage.toLowerCase().contains("success") ||
                        successMessage.toLowerCase().contains("complete") ||
                        successMessage.toLowerCase().contains("register"),
                "Registration success message should indicate successful registration");

        logger.info("Registration success assertion completed");
    }

    /**
     * Assert registration validation errors
     */
    public void assertRegistrationValidationErrors(RegisterPage registerPage, String expectedErrorField) {
        logger.info("Asserting registration validation errors for field: {}", expectedErrorField);

        softAssert.assertTrue(registerPage.hasValidationErrors(),
                "Registration validation errors should be displayed");

        List<String> errorMessages = registerPage.getValidationErrorMessages();
        softAssert.assertFalse(errorMessages.isEmpty(),
                "Registration should have validation error messages");

        if (expectedErrorField != null && !expectedErrorField.isEmpty()) {
            String lowerExpectedField = expectedErrorField.toLowerCase();
            boolean hasFieldError = errorMessages.stream()
                    .anyMatch(msg -> {
                        String lowerMsg = msg.toLowerCase();
                        return lowerMsg.contains(lowerExpectedField) ||
                               (lowerExpectedField.contains("email") && (lowerMsg.contains("email") || lowerMsg.contains("e-mail"))) ||
                               (lowerExpectedField.contains("password") && lowerMsg.contains("password")) ||
                               (lowerExpectedField.contains("required") && (lowerMsg.contains("required") || lowerMsg.contains("field") || lowerMsg.contains("enter"))) ||
                               (lowerExpectedField.contains("format") && (lowerMsg.contains("format") || lowerMsg.contains("invalid") || lowerMsg.contains("valid")));
                    });
            softAssert.assertTrue(hasFieldError,
                    String.format("Should have validation error for field '%s'. Actual errors: %s", expectedErrorField, errorMessages));
        }

        logger.info("Registration validation errors assertion completed");
    }

    /**
     * Assert login validation errors
     */
    public void assertLoginValidationErrors(LoginPage loginPage, String expectedErrorType) {
        logger.info("Asserting login validation errors of type: {}", expectedErrorType);

        softAssert.assertTrue(loginPage.hasValidationErrors(),
                "Login validation errors should be displayed");

        String errorMessage = loginPage.getValidationErrorMessage();
        softAssert.assertNotNull(errorMessage,
                "Login error message should not be null");

        softAssert.assertFalse(errorMessage.trim().isEmpty(),
                "Login error message should not be empty");

        // Add more specific error type checking
        if (expectedErrorType != null && !expectedErrorType.isEmpty()) {
            String lowerErrorMessage = errorMessage.toLowerCase();
            String lowerExpectedType = expectedErrorType.toLowerCase();

            boolean hasExpectedError = lowerErrorMessage.contains(lowerExpectedType) ||
                                     (lowerExpectedType.contains("required") && (lowerErrorMessage.contains("required") || lowerErrorMessage.contains("field") || lowerErrorMessage.contains("enter"))) ||
                                     (lowerExpectedType.contains("invalid") && (lowerErrorMessage.contains("invalid") || lowerErrorMessage.contains("incorrect") || lowerErrorMessage.contains("wrong")));

            softAssert.assertTrue(hasExpectedError,
                    String.format("Error message should contain or relate to '%s'. Actual message: '%s'", expectedErrorType, errorMessage));
        }

        logger.info("Login validation errors assertion completed");
    }

    /**
     * Assert password recovery validation errors
     */
    public void assertPasswordRecoveryValidationErrors(PasswordRecoveryPage recoveryPage, String expectedErrorType) {
        logger.info("Asserting password recovery validation errors of type: {}", expectedErrorType);

        softAssert.assertTrue(recoveryPage.hasValidationErrors(),
                "Password recovery validation errors should be displayed");

        String errorMessage = recoveryPage.getValidationErrorMessage();
        softAssert.assertNotNull(errorMessage,
                "Password recovery error message should not be null");

        softAssert.assertFalse(errorMessage.trim().isEmpty(),
                "Password recovery error message should not be empty");

        // Add more specific error type checking for password recovery
        if (expectedErrorType != null && !expectedErrorType.isEmpty()) {
            String lowerErrorMessage = errorMessage.toLowerCase();
            String lowerExpectedType = expectedErrorType.toLowerCase();

            boolean hasExpectedError = lowerErrorMessage.contains(lowerExpectedType) ||
                                     (lowerExpectedType.contains("email") && (lowerErrorMessage.contains("email") || lowerErrorMessage.contains("e-mail") || lowerErrorMessage.contains("address"))) ||
                                     (lowerExpectedType.contains("required") && (lowerErrorMessage.contains("required") || lowerErrorMessage.contains("field") || lowerErrorMessage.contains("enter") || lowerErrorMessage.contains("empty"))) ||
                                     (lowerExpectedType.contains("format") && (lowerErrorMessage.contains("format") || lowerErrorMessage.contains("invalid") || lowerErrorMessage.contains("valid") || lowerErrorMessage.contains("correct"))) ||
                                     (lowerExpectedType.contains("invalid") && (lowerErrorMessage.contains("invalid") || lowerErrorMessage.contains("incorrect") || lowerErrorMessage.contains("wrong") || lowerErrorMessage.contains("not valid")));

            softAssert.assertTrue(hasExpectedError,
                    String.format("Password recovery error message should contain or relate to '%s'. Actual message: '%s'", expectedErrorType, errorMessage));
        }

        logger.info("Password recovery validation errors assertion completed");
    }

    // ========== Product Search and Browse Assertions ==========

    /**
     * Assert search results are displayed and relevant
     */
    public void assertSearchResults(ProductSearchPage searchPage, String searchTerm, boolean shouldHaveResults) {
        logger.info("Asserting search results for term: {} (should have results: {})", searchTerm, shouldHaveResults);

        softAssert.assertTrue(searchPage.isSearchResultsDisplayed(),
                "Search results section should be displayed");

        if (shouldHaveResults) {
            softAssert.assertTrue(searchPage.hasSearchResults(),
                    "Search should return results for term: " + searchTerm);

            int resultCount = searchPage.getSearchResultCount();
            softAssert.assertTrue(resultCount > 0,
                    "Search result count should be greater than 0");

            // Verify search term appears in results (if possible)
            List<String> productTitles = searchPage.getSearchResultTitles();
            boolean termFoundInResults = productTitles.stream()
                    .anyMatch(title -> title.toLowerCase().contains(searchTerm.toLowerCase()));

            if (!termFoundInResults && !searchTerm.isEmpty()) {
                logger.warn("Search term '{}' not found in product titles, but results were returned", searchTerm);
            }
        } else {
            softAssert.assertFalse(searchPage.hasSearchResults(),
                    "Search should not return results for invalid term: " + searchTerm);

            softAssert.assertTrue(searchPage.isNoResultsMessageDisplayed(),
                    "No results message should be displayed for invalid search");
        }

        logger.info("Search results assertion completed");
    }

    /**
     * Assert category page is loaded correctly
     */
    public void assertCategoryPageLoaded(ProductCatalogPage catalogPage, String expectedCategory) {
        logger.info("Asserting category page loaded for: {}", expectedCategory);

        softAssert.assertTrue(catalogPage.isPageLoaded(),
                "Category page should be loaded");

        if (expectedCategory != null) {
            String pageTitle = catalogPage.getPageTitle();
            softAssert.assertTrue(pageTitle.toLowerCase().contains(expectedCategory.toLowerCase()),
                    "Page title should contain category name: " + expectedCategory);
        }

        softAssert.assertTrue(catalogPage.hasProducts(),
                "Category page should display products");

        softAssert.assertTrue(catalogPage.isSortingDropdownDisplayed(),
                "Sorting dropdown should be displayed on category page");

        logger.info("Category page assertion completed");
    }

    /**
     * Assert product details page is loaded correctly
     */
    public void assertProductDetailsLoaded(ProductDetailsPage productPage, String expectedProductName) {
        logger.info("Asserting product details page loaded for: {}", expectedProductName);

        softAssert.assertTrue(productPage.isPageLoaded(),
                "Product details page should be loaded");

        softAssert.assertTrue(productPage.isProductTitleDisplayed(),
                "Product title should be displayed");

        softAssert.assertTrue(productPage.isProductPriceDisplayed(),
                "Product price should be displayed");

        softAssert.assertTrue(productPage.isAddToCartButtonDisplayed(),
                "Add to cart button should be displayed");

        if (expectedProductName != null) {
            String actualProductName = productPage.getProductTitle();
            softAssert.assertTrue(actualProductName.toLowerCase().contains(expectedProductName.toLowerCase()),
                    "Product title should contain expected name: " + expectedProductName);
        }

        logger.info("Product details assertion completed");
    }

    // ========== Shopping Cart Assertions ==========

    /**
     * Assert item was added to cart successfully
     */
    public void assertItemAddedToCart(HomePage homePage, int expectedCartCount) {
        logger.info("Asserting item added to cart, expected count: {}", expectedCartCount);

        softAssert.assertTrue(homePage.isCartIconDisplayed(),
                "Shopping cart icon should be displayed");

        int actualCartCount = homePage.getCartItemCount();
        softAssert.assertEquals(actualCartCount, expectedCartCount,
                "Cart item count should match expected count");

        // Check if cart count is visible (usually shown when > 0)
        if (expectedCartCount > 0) {
            softAssert.assertTrue(homePage.isCartCountDisplayed(),
                    "Cart count should be displayed when items are in cart");
        }

        logger.info("Item added to cart assertion completed");
    }

    /**
     * Assert shopping cart page content
     */
    public void assertShoppingCartContent(ShoppingCartPage cartPage, int expectedItemCount) {
        logger.info("Asserting shopping cart content, expected items: {}", expectedItemCount);

        softAssert.assertTrue(cartPage.isPageLoaded(),
                "Shopping cart page should be loaded");

        if (expectedItemCount > 0) {
            softAssert.assertTrue(cartPage.hasItems(),
                    "Shopping cart should contain items");

            int actualItemCount = cartPage.getItemCount();
            softAssert.assertEquals(actualItemCount, expectedItemCount,
                    "Cart should contain expected number of items");

            softAssert.assertTrue(cartPage.isTotalDisplayed(),
                    "Cart total should be displayed");

            softAssert.assertTrue(cartPage.isCheckoutButtonDisplayed(),
                    "Checkout button should be displayed when cart has items");
        } else {
            softAssert.assertFalse(cartPage.hasItems(),
                    "Shopping cart should be empty");

            softAssert.assertTrue(cartPage.isEmptyCartMessageDisplayed(),
                    "Empty cart message should be displayed");
        }

        logger.info("Shopping cart content assertion completed");
    }

    /**
     * Assert cart total calculation is correct
     */
    public void assertCartTotalCalculation(ShoppingCartPage cartPage, BigDecimal expectedTotal) {
        logger.info("Asserting cart total calculation, expected: {}", expectedTotal);

        softAssert.assertTrue(cartPage.isTotalDisplayed(),
                "Cart total should be displayed");

        BigDecimal actualTotal = cartPage.getCartTotalAmount();
        softAssert.assertEquals(actualTotal, expectedTotal,
                "Cart total should match expected amount");

        // Verify subtotal + tax + shipping = total (if applicable)
        if (cartPage.isSubtotalDisplayed() && cartPage.isTaxDisplayed()) {
            BigDecimal subtotal = cartPage.getSubtotal();
            BigDecimal tax = cartPage.getTax();
            BigDecimal shipping = cartPage.getShipping();
            BigDecimal calculatedTotal = subtotal.add(tax).add(shipping);

            softAssert.assertEquals(actualTotal, calculatedTotal,
                    "Total should equal subtotal + tax + shipping");
        }

        logger.info("Cart total calculation assertion completed");
    }

    // ========== Checkout Assertions ==========

    /**
     * Assert checkout page is loaded and ready
     */
    public void assertCheckoutPageLoaded(CheckoutPage checkoutPage) {
        logger.info("Asserting checkout page is loaded");

        softAssert.assertTrue(checkoutPage.isPageLoaded(),
                "Checkout page should be loaded");

        softAssert.assertTrue(checkoutPage.isBillingAddressFormDisplayed(),
                "Billing address form should be displayed");

        softAssert.assertTrue(checkoutPage.isShippingMethodSectionDisplayed(),
                "Shipping method section should be displayed");

        softAssert.assertTrue(checkoutPage.isPaymentMethodSectionDisplayed(),
                "Payment method section should be displayed");

        logger.info("Checkout page assertion completed");
    }

    /**
     * Assert address information is correctly populated
     */
    public void assertAddressInformation(CheckoutPage checkoutPage, Address expectedAddress, boolean isBilling) {
        String addressType = isBilling ? "billing" : "shipping";
        logger.info("Asserting {} address information", addressType);

        Address actualAddress = isBilling ? checkoutPage.getBillingAddress() : checkoutPage.getShippingAddress();

        softAssert.assertEquals(actualAddress.getFirstName(), expectedAddress.getFirstName(),
                String.format("%s address first name should match", addressType));

        softAssert.assertEquals(actualAddress.getLastName(), expectedAddress.getLastName(),
                String.format("%s address last name should match", addressType));

        softAssert.assertEquals(actualAddress.getAddress1(), expectedAddress.getAddress1(),
                String.format("%s address street should match", addressType));

        softAssert.assertEquals(actualAddress.getCity(), expectedAddress.getCity(),
                String.format("%s address city should match", addressType));

        softAssert.assertEquals(actualAddress.getZipPostalCode(), expectedAddress.getZipPostalCode(),
                String.format("%s address zip code should match", addressType));

        logger.info("{} address assertion completed", addressType);
    }

    /**
     * Assert order completion
     */
    public void assertOrderCompleted(OrderCompletePage orderPage, String expectedOrderNumber) {
        logger.info("Asserting order completion");

        softAssert.assertTrue(orderPage.isPageLoaded(),
                "Order complete page should be loaded");

        softAssert.assertTrue(orderPage.isOrderConfirmationDisplayed(),
                "Order confirmation message should be displayed");

        String actualOrderNumber = orderPage.getOrderNumber();
        softAssert.assertNotNull(actualOrderNumber,
                "Order number should be provided");

        softAssert.assertFalse(actualOrderNumber.trim().isEmpty(),
                "Order number should not be empty");

        if (expectedOrderNumber != null) {
            softAssert.assertEquals(actualOrderNumber, expectedOrderNumber,
                    "Order number should match expected value");
        }

        softAssert.assertTrue(orderPage.isOrderDetailsDisplayed(),
                "Order details should be displayed");

        logger.info("Order completion assertion completed");
    }

    // ========== General Page Assertions ==========

    /**
     * Assert page URL contains expected path
     */
    public void assertPageUrl(String expectedPath, String description) {
        logger.info("Asserting page URL contains: {}", expectedPath);

        String currentUrl = driver.getCurrentUrl();
        softAssert.assertTrue(currentUrl.contains(expectedPath),
                String.format("Page URL should contain '%s' - %s. Current URL: %s",
                        expectedPath, description, currentUrl));

        logger.info("Page URL assertion completed");
    }

    /**
     * Assert page title matches expected title
     */
    public void assertPageTitle(String expectedTitle, String description) {
        logger.info("Asserting page title contains: {}", expectedTitle);

        String actualTitle = driver.getTitle();
        softAssert.assertTrue(actualTitle.toLowerCase().contains(expectedTitle.toLowerCase()),
                String.format("Page title should contain '%s' - %s. Actual title: %s",
                        expectedTitle, description, actualTitle));

        logger.info("Page title assertion completed");
    }

    /**
     * Assert element is visible and enabled
     */
    public void assertElementVisibleAndEnabled(WebElement element, String elementDescription) {
        logger.info("Asserting element is visible and enabled: {}", elementDescription);

        softAssert.assertTrue(element.isDisplayed(),
                elementDescription + " should be visible");

        softAssert.assertTrue(element.isEnabled(),
                elementDescription + " should be enabled");

        logger.info("Element visibility/enabled assertion completed");
    }

    /**
     * Assert validation error messages contain expected text
     */
    public void assertValidationErrorContains(List<String> errorMessages, String expectedText, String fieldName) {
        logger.info("Asserting validation error contains '{}' for field: {}", expectedText, fieldName);

        softAssert.assertFalse(errorMessages.isEmpty(),
                "Validation errors should be present for field: " + fieldName);

        boolean errorFound = errorMessages.stream()
                .anyMatch(msg -> msg.toLowerCase().contains(expectedText.toLowerCase()));

        softAssert.assertTrue(errorFound,
                String.format("Validation error should contain '%s' for field '%s'. Actual errors: %s",
                        expectedText, fieldName, errorMessages));

        logger.info("Validation error assertion completed");
    }

    // ========== Assertion Finalization ==========

    /**
     * Assert all soft assertions and log results
     */
    public void assertAll() {
        logger.info("Finalizing all assertions");
        try {
            softAssert.assertAll();
            logger.info("All assertions passed successfully");
        } catch (AssertionError e) {
            logger.error("Some assertions failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get the SoftAssert instance for additional custom assertions
     */
    public SoftAssert getSoftAssert() {
        return softAssert;
    }

    // ========== Utility Methods ==========

    /**
     * Assert that a list contains expected number of items
     */
    public void assertListSize(List<?> list, int expectedSize, String listDescription) {
        logger.info("Asserting {} list size is {}", listDescription, expectedSize);

        softAssert.assertNotNull(list, listDescription + " should not be null");
        softAssert.assertEquals(list.size(), expectedSize,
                listDescription + " should contain " + expectedSize + " items");

        logger.info("List size assertion completed");
    }

    /**
     * Assert that a string is not null or empty
     */
    public void assertStringNotNullOrEmpty(String value, String fieldDescription) {
        logger.info("Asserting {} is not null or empty", fieldDescription);

        softAssert.assertNotNull(value, fieldDescription + " should not be null");
        softAssert.assertFalse(value.trim().isEmpty(), fieldDescription + " should not be empty");

        logger.info("String not null/empty assertion completed");
    }

    /**
     * Assert numeric value is within expected range
     */
    public void assertNumericRange(double value, double min, double max, String fieldDescription) {
        logger.info("Asserting {} ({}) is within range {} - {}", fieldDescription, value, min, max);

        softAssert.assertTrue(value >= min && value <= max,
                String.format("%s should be within range %.2f - %.2f, but was %.2f",
                        fieldDescription, min, max, value));

        logger.info("Numeric range assertion completed");
    }
}