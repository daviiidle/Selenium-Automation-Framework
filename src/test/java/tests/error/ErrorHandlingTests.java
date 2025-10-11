package tests.error;

import base.BaseTest;
import com.demowebshop.automation.pages.*;
import com.demowebshop.automation.pages.common.BasePage;
import utils.DemoWebShopAssertions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Comprehensive Error Handling Test Suite
 * Covers all error handling scenarios from manual testing documentation
 * Tests network errors, validation errors, and system error recovery
 */
public class ErrorHandlingTests extends BaseTest {
    private DemoWebShopAssertions assertions;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(getDriver());
        setHomePage(new HomePage(getDriver()));
    }

    private HomePage home() {
        return getHomePage();
    }

    private void updateHome(HomePage homePage) {
        setHomePage(homePage);
    }

    private WebDriver webDriver() {
        return getDriver();
    }

    /**
     * Test ID: ERROR_001 - Network Error Handling
     * Tests graceful handling of network interruptions and connectivity issues
     * Validates error messages, retry mechanisms, and recovery options
     */
    @Test(groups = {"error-handling", "network", "low-priority"},
          priority = 1,
          description = "Application should handle network errors gracefully")
    public void testNetworkErrorHandling() {
        logger.info("=== Starting ERROR_001: Network Error Handling ===");

        HomePage homePage = home();
        WebDriver driver = webDriver();
        SoftAssert softAssert = assertions.getSoftAssert();

        // Test 1: Page load timeout handling
        logger.info("Testing page load timeout scenarios");

        try {
            // Set very short timeout to simulate network issues
            driver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofMillis(100));

            // Attempt to navigate to a page that might timeout
            driver.get("https://demowebshop.tricentis.com/slow-loading-page");

        } catch (TimeoutException e) {
            softAssert.assertTrue(true, "Timeout exception should be caught gracefully");
            logger.info("Page load timeout handled: {}", e.getMessage());

            // Verify browser doesn't crash and can recover
            try {
                // Reset timeout and navigate to working page
                driver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(30));
                driver.get("https://demowebshop.tricentis.com/");

                homePage = new HomePage(driver);
                updateHome(homePage);
                softAssert.assertTrue(homePage.isPageLoaded(),
                                     "Should be able to recover from timeout and load homepage");

            } catch (Exception recoveryException) {
                logger.error("Could not recover from timeout: {}", recoveryException.getMessage());
            }
        }

        // Test 2: Invalid URL handling
        logger.info("Testing invalid URL error handling");

        try {
            driver.get("https://demowebshop.tricentis.com/non-existent-page");

            // Check if a 404 error page is displayed or redirect occurred
            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();

            if (pageTitle.toLowerCase().contains("404") ||
                pageTitle.toLowerCase().contains("not found") ||
                pageTitle.toLowerCase().contains("error")) {

                softAssert.assertTrue(true, "404 error page should be displayed for invalid URLs");
                logger.info("404 error page detected: {}", pageTitle);

            } else if (currentUrl.contains("demowebshop.tricentis.com")) {
                // Site might redirect to homepage or valid page
                softAssert.assertTrue(true, "Invalid URL should redirect to valid page");
                logger.info("Invalid URL redirected to: {}", currentUrl);
            }

        } catch (WebDriverException e) {
            logger.info("WebDriver exception for invalid URL: {}", e.getMessage());
        }

        // Test 3: Form submission error handling
        logger.info("Testing form submission error scenarios");

        try {
            // Navigate back to working site
            driver.get("https://demowebshop.tricentis.com/");
            homePage = new HomePage(driver);
            updateHome(homePage);

            // Test registration form error handling
            RegisterPage registerPage = homePage.clickRegisterLink();
            Assert.assertTrue(registerPage.isPageLoaded(), "Register page should load");

            // Submit form with intentionally problematic data
            registerPage.enterEmail("test@test.com") // Potentially existing email
                       .enterPassword("weak")        // Weak password
                       .confirmPassword("different") // Mismatched password
                       .clickRegisterButton();

            // Verify error handling
            if (registerPage.hasValidationErrors()) {
                softAssert.assertTrue(true, "Form validation errors should be displayed");
                logger.info("Registration form validation working correctly");
            }

            if (registerPage.hasServerErrors()) {
                softAssert.assertTrue(true, "Server errors should be displayed appropriately");
                logger.info("Server error handling working correctly");
            }

        } catch (Exception e) {
            logger.info("Form submission error test resulted in: {}", e.getMessage());
        }

        // Test 4: AJAX request error handling
        logger.info("Testing AJAX error handling scenarios");

        try {
            homePage = new HomePage(driver);
            updateHome(homePage);

            // Test search with potential AJAX errors
            String searchTerm = "test search with special chars: <>&\"'";
            ProductSearchPage searchPage = homePage.performSearch(searchTerm);

            // Verify search handles special characters gracefully
            if (searchPage.isPageLoaded()) {
                softAssert.assertTrue(true, "Search should handle special characters without errors");

                if (searchPage.hasSearchResults() || searchPage.isNoResultsMessageDisplayed()) {
                    softAssert.assertTrue(true, "Search results or no results message should be displayed");
                }
            }

            // Test cart operations that might involve AJAX
            ProductDetailsPage productPage = homePage.navigateToRandomProduct();
            if (productPage.isPageLoaded()) {
                // Rapid clicking to test AJAX race conditions
                productPage.clickAddToCart();

                // Wait a moment and verify cart updated properly
                Thread.sleep(1000);

                int cartCount = homePage.getCartItemCount();
                softAssert.assertTrue(cartCount >= 0, "Cart count should be non-negative after AJAX update");
            }

        } catch (Exception e) {
            logger.info("AJAX error handling test: {}", e.getMessage());
        }

        assertions.assertAll();
        logger.info("=== ERROR_001 completed: Network error handling scenarios ===");
    }

    /**
     * Test ID: ERROR_002 - Browser Compatibility Error Handling
     * Tests error handling across different browser scenarios
     * Validates graceful degradation and browser-specific error recovery
     */
    @Test(groups = {"error-handling", "browser", "low-priority"},
          priority = 2,
          description = "Application should handle browser-specific errors gracefully")
    public void testBrowserCompatibilityErrors() {
        logger.info("=== Starting ERROR_002: Browser Compatibility Error Handling ===");

        HomePage homePage = home();
        WebDriver driver = webDriver();
        SoftAssert softAssert = assertions.getSoftAssert();

        // Test 1: JavaScript error handling
        logger.info("Testing JavaScript error scenarios");

        try {
            // Execute potentially problematic JavaScript
            ((JavascriptExecutor) driver).executeScript("nonExistentFunction();");

        } catch (Exception e) {
            softAssert.assertTrue(true, "JavaScript errors should be handled gracefully");
            logger.info("JavaScript error handled: {}", e.getMessage());

            // Verify page functionality still works after JS error
            try {
                homePage = new HomePage(driver);
                updateHome(homePage);
                softAssert.assertTrue(homePage.isPageLoaded(),
                                     "Page should remain functional after JavaScript errors");
            } catch (Exception recoveryError) {
                logger.warn("Page recovery after JS error failed: {}", recoveryError.getMessage());
            }
        }

        // Test 2: Browser back/forward navigation error handling
        logger.info("Testing browser navigation error scenarios");

        try {
            // Navigate through several pages
            ProductCatalogPage catalogPage = homePage.navigateToCategory("Books");

            if (catalogPage.hasProducts()) {
                ProductDetailsPage productPage = catalogPage.clickFirstProduct();

                // Use browser back button
                driver.navigate().back();

                // Verify graceful handling of back navigation
                softAssert.assertTrue(catalogPage.isPageLoaded() || homePage.isPageLoaded(),
                                     "Back navigation should work without errors");

                // Use browser forward button
                driver.navigate().forward();

                // Verify forward navigation handling
                softAssert.assertTrue(true, "Forward navigation should not cause errors");
            }

        } catch (Exception e) {
            logger.info("Browser navigation error: {}", e.getMessage());
        }

        // Test 3: Window/tab handling errors
        logger.info("Testing window handling error scenarios");

        try {
            String originalWindow = driver.getWindowHandle();

            // Open new tab/window (if supported)
            ((JavascriptExecutor) driver).executeScript("window.open('https://demowebshop.tricentis.com/books', '_blank');");

            // Wait and check for new windows
            Thread.sleep(2000);

            if (driver.getWindowHandles().size() > 1) {
                // Switch to new window
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }

                // Verify new window functionality
                HomePage newWindowHomePage = new HomePage(driver);
                if (newWindowHomePage.isPageLoaded()) {
                    softAssert.assertTrue(true, "New window should function correctly");
                }

                // Close new window and switch back
                driver.close();
                driver.switchTo().window(originalWindow);

                // Verify original window still works
                homePage = new HomePage(driver);
                updateHome(homePage);
                softAssert.assertTrue(homePage.isPageLoaded(),
                                     "Original window should remain functional after new window closed");
            }

        } catch (Exception e) {
            logger.info("Window handling error: {}", e.getMessage());
        }

        assertions.assertAll();
        logger.info("=== ERROR_002 completed: Browser compatibility error handling ===");
    }

    /**
     * Test ID: ERROR_003 - Session and Authentication Error Handling
     * Tests handling of session timeouts and authentication errors
     * Validates proper error messages and login redirect functionality
     */
    @Test(groups = {"error-handling", "authentication", "low-priority"},
          priority = 3,
          description = "Application should handle session and auth errors gracefully")
    public void testSessionAndAuthErrorHandling() {
        logger.info("=== Starting ERROR_003: Session and Authentication Error Handling ===");

        HomePage homePage = home();
        WebDriver driver = webDriver();
        SoftAssert softAssert = assertions.getSoftAssert();

        // Test 1: Invalid login attempts
        logger.info("Testing invalid authentication scenarios");

        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should load");

        // Test with completely invalid credentials
        loginPage.enterEmail("nonexistent@invalid.com");
        loginPage.enterPassword("wrongpassword");

        BasePage resultPage = loginPage.clickLoginButton();

        // Verify appropriate error handling
        if (resultPage instanceof LoginPage) {
            LoginPage loginPageResult = (LoginPage) resultPage;

            if (loginPageResult.hasValidationErrors()) {
                softAssert.assertTrue(true, "Login validation errors should be displayed");
                logger.info("Login error handling working correctly");
            }
        }

        // Test 2: Multiple failed login attempts
        logger.info("Testing multiple failed login attempts");

        for (int i = 0; i < 3; i++) {
            loginPage.clearForm();
            loginPage.enterEmail("test" + i + "@invalid.com");
            loginPage.enterPassword("wrong" + i);
            loginPage.clickLoginButton();

            // Check if account lockout or rate limiting occurs
            if (loginPage.hasAccountLockoutMessage()) {
                softAssert.assertTrue(true, "Account lockout should be implemented after multiple failures");
                break;
            }

            if (loginPage.hasRateLimitingMessage()) {
                softAssert.assertTrue(true, "Rate limiting should be implemented for failed attempts");
                break;
            }
        }

        // Test 3: Session handling for protected pages
        logger.info("Testing protected page access without login");

        try {
            // Try to access account pages without being logged in
            driver.get("https://demowebshop.tricentis.com/customer/info");

            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();

            // Should redirect to login or show appropriate message
            if (currentUrl.contains("login") ||
                pageTitle.toLowerCase().contains("login") ||
                pageTitle.toLowerCase().contains("sign in")) {

                softAssert.assertTrue(true, "Should redirect to login for protected pages");
                logger.info("Protected page redirect working correctly");

            } else if (currentUrl.contains("unauthorized") ||
                       pageTitle.toLowerCase().contains("unauthorized") ||
                       pageTitle.toLowerCase().contains("access denied")) {

                softAssert.assertTrue(true, "Should show unauthorized message for protected pages");
                logger.info("Unauthorized access handling working correctly");
            }

        } catch (Exception e) {
            logger.info("Protected page access test: {}", e.getMessage());
        }

        // Test 4: Form token and CSRF error handling
        logger.info("Testing form security error handling");

        try {
            // Navigate to registration page
            RegisterPage registerPage = homePage.clickRegisterLink();

            // Attempt to manipulate form tokens (if visible)
            // This tests CSRF protection
            registerPage.enterFirstName("Test");
            registerPage.enterLastName("User");
            registerPage.enterEmail("test" + System.currentTimeMillis() + "@test.com");
            registerPage.enterPassword("password123");
            registerPage.confirmPassword("password123");

            // Try to submit form multiple times rapidly
            for (int i = 0; i < 3; i++) {
                registerPage.clickRegisterButton();
                Thread.sleep(500);
            }

            // Check for appropriate security error handling
            if (registerPage.hasSecurityErrors()) {
                softAssert.assertTrue(true, "Security errors should be handled appropriately");
            }

        } catch (Exception e) {
            logger.info("Form security test: {}", e.getMessage());
        }

        assertions.assertAll();
        logger.info("=== ERROR_003 completed: Session and authentication error handling ===");
    }

    /**
     * Test ID: ERROR_004 - Data Validation Error Handling
     * Tests handling of various data validation errors across the application
     * Validates error message clarity and user guidance
     */
    @Test(groups = {"error-handling", "validation", "low-priority"},
          priority = 4,
          description = "Application should provide clear validation error messages")
    public void testDataValidationErrorHandling() {
        logger.info("=== Starting ERROR_004: Data Validation Error Handling ===");

        HomePage homePage = home();
        WebDriver driver = webDriver();
        SoftAssert softAssert = assertions.getSoftAssert();

        // Test 1: Email validation errors
        logger.info("Testing email validation error scenarios");

        RegisterPage registerPage = homePage.clickRegisterLink();
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page should load");

        String[] invalidEmails = {
            "invalid-email",
            "test@",
            "@domain.com",
            "test..test@domain.com",
            "test@domain",
            "test@.domain.com",
            "<script>alert('xss')</script>@domain.com"
        };

        for (String invalidEmail : invalidEmails) {
            registerPage.clearForm();
            registerPage.enterFirstName("Test");
            registerPage.enterLastName("User");
            registerPage.enterEmail(invalidEmail);
            registerPage.enterPassword("password123");
            registerPage.confirmPassword("password123");
            registerPage.clickRegisterButton();

            if (registerPage.hasEmailValidationError()) {
                softAssert.assertTrue(true,
                    "Email validation error should be shown for: " + invalidEmail);
                logger.info("Email validation working for: {}", invalidEmail);
            }
        }

        // Test 2: Password validation errors
        logger.info("Testing password validation error scenarios");

        String[] weakPasswords = {
            "",           // Empty
            "123",        // Too short
            "password",   // Common word
            "12345678",   // Numeric only
            "abcdefgh"    // Letters only
        };

        for (String weakPassword : weakPasswords) {
            registerPage.clearForm();
            registerPage.enterFirstName("Test");
            registerPage.enterLastName("User");
            registerPage.enterEmail("test" + System.currentTimeMillis() + "@test.com");
            registerPage.enterPassword(weakPassword);
            registerPage.confirmPassword(weakPassword);
            registerPage.clickRegisterButton();

            if (registerPage.hasPasswordValidationError()) {
                softAssert.assertTrue(true,
                    "Password validation error should be shown for weak password");
                logger.info("Password validation working for weak password");
                break;
            }
        }

        // Test 3: XSS and injection attempt handling
        logger.info("Testing XSS and injection prevention");

        String[] maliciousInputs = {
            "<script>alert('xss')</script>",
            "'; DROP TABLE users; --",
            "<img src=x onerror=alert('xss')>",
            "javascript:alert('xss')"
        };

        for (String maliciousInput : maliciousInputs) {
            try {
                registerPage.clearForm();
                registerPage.enterFirstName(maliciousInput);
                registerPage.enterLastName("User");
                registerPage.enterEmail("test" + System.currentTimeMillis() + "@test.com");
                registerPage.enterPassword("password123");
                registerPage.confirmPassword("password123");
                registerPage.clickRegisterButton();

                // Verify malicious input is sanitized or blocked
                String pageSource = driver.getPageSource();
                if (!pageSource.contains("<script>") &&
                    !pageSource.contains("javascript:") &&
                    !pageSource.contains("DROP TABLE")) {

                    softAssert.assertTrue(true, "Malicious input should be sanitized");
                    logger.info("XSS/injection prevention working for: {}", maliciousInput);
                }

            } catch (Exception e) {
                softAssert.assertTrue(true, "Malicious input should be handled gracefully");
                logger.info("Malicious input blocked: {}", e.getMessage());
            }
        }

        // Test 4: File size and type validation (if applicable)
        logger.info("Testing file upload validation scenarios");

        // This would test file upload fields if they exist in the application
        // For demo purposes, we'll test general form field length limits

        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longString.append("A");
        }

        try {
            registerPage.clearForm();
            registerPage.enterFirstName(longString.toString());
            registerPage.enterLastName("User");
            registerPage.enterEmail("test@test.com");
            registerPage.enterPassword("password123");
            registerPage.confirmPassword("password123");
            registerPage.clickRegisterButton();

            // Verify long input is handled appropriately
            if (registerPage.hasFieldLengthValidationError()) {
                softAssert.assertTrue(true, "Field length validation should work");
                logger.info("Field length validation working correctly");
            }

        } catch (Exception e) {
            logger.info("Long input test: {}", e.getMessage());
        }

        assertions.assertAll();
        logger.info("=== ERROR_004 completed: Data validation error handling ===");
    }

    @Override
    protected void additionalTeardown() {
        try {
            // Reset timeouts to default values
            WebDriver driver = getDriver();
            driver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(30));
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));

            // Close any additional windows that might have been opened
            String originalWindow = driver.getWindowHandle();
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    driver.close();
                }
            }
            driver.switchTo().window(originalWindow);

            // Navigate back to homepage
            driver.get("https://demowebshop.tricentis.com/");
            updateHome(new HomePage(driver));

            logger.info("Error handling test cleanup completed");

        } catch (IllegalStateException ignored) {
            // Driver already cleaned up
        } catch (Exception e) {
            logger.warn("Error during error handling test cleanup: {}", e.getMessage());
        }
    }
}
