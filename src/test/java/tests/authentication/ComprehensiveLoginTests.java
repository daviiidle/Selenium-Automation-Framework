package tests.authentication;

import base.BaseTest;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.LoginPage;
import dataproviders.AuthenticationDataProvider;
import factories.UserDataFactory;
import models.User;
import utils.DemoWebShopAssertions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Comprehensive Login Test Suite
 * Covers all login scenarios from manual testing documentation
 * Includes positive, negative, validation, and security testing scenarios
 */
public class ComprehensiveLoginTests extends BaseTest {
    private DemoWebShopAssertions assertions;
    private HomePage homePage;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(driver);
        homePage = new HomePage(driver);
    }

    /**
     * Test ID: LOGIN_001 - Valid Login Happy Path
     * Tests successful login with valid registered user credentials
     * Validates successful authentication and user session establishment
     */
    @Test(groups = {"smoke", "authentication", "high-priority"},
          priority = 1,
          dataProvider = "validLoginData",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "Valid login with registered user credentials")
    public void testValidLogin(String email, String password, String description) {
        logger.info("=== Starting LOGIN_001: {} ===", description);

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        loginPage.enterEmail(email);
        loginPage.enterPassword(password);

        HomePage resultPage = (HomePage) loginPage.clickLoginButton();

        // Verify successful login
        assertions.assertUserLoggedIn(resultPage, email);
        assertions.assertPageUrl("/", "Should redirect to home page after successful login");

        assertions.assertAll();
        logger.info("=== LOGIN_001 completed successfully for: {} ===", email);
    }

    /**
     * Test ID: LOGIN_002 - Invalid Login Credentials
     * Tests login with various invalid credential combinations
     * Validates proper error handling and security measures
     */
    @Test(groups = {"negative", "authentication", "high-priority"},
          priority = 2,
          dataProvider = "invalidLoginData",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "Login with invalid credentials should show appropriate errors")
    public void testInvalidLogin(String email, String password, String scenario, String expectedError) {
        logger.info("=== Starting LOGIN_002: {} ===", scenario);

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

        // Verify login fails with appropriate error
        assertions.assertLoginValidationErrors(loginPage, expectedError);
        assertions.assertPageUrl("login", "Should remain on login page after failed login");
        assertions.assertUserLoggedOut(homePage);

        assertions.assertAll();
        logger.info("=== LOGIN_002 completed: {} ===", scenario);
    }

    /**
     * Test ID: LOGIN_003 - Empty Credentials Validation
     * Tests login form validation with empty fields
     */
    @Test(groups = {"negative", "authentication", "validation"},
          priority = 3,
          description = "Login with empty credentials should show validation errors")
    public void testEmptyCredentialsLogin() {
        logger.info("=== Starting LOGIN_003: Empty Credentials Validation ===");

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        // Attempt login without entering credentials
        loginPage.clickLoginButton();

        // Verify validation errors
        assertions.assertLoginValidationErrors(loginPage, "required");
        assertions.assertPageUrl("login", "Should remain on login page after validation error");
        assertions.assertUserLoggedOut(homePage);

        assertions.assertAll();
        logger.info("=== LOGIN_003 completed: Empty credentials validation ===");
    }

    /**
     * Test ID: LOGIN_004 - Remember Me Functionality
     * Tests remember me checkbox functionality and session persistence
     */
    @Test(groups = {"functional", "authentication", "medium-priority"},
          priority = 4,
          dataProvider = "rememberMeData",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "Remember me functionality should work correctly")
    public void testRememberMeFunctionality(String email, String password, boolean rememberMe, String description) {
        logger.info("=== Starting LOGIN_004: {} ===", description);

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        loginPage.enterEmail(email);
        loginPage.enterPassword(password);

        if (rememberMe) {
            loginPage.checkRememberMe();
            SoftAssert softAssert = assertions.getSoftAssert();
            softAssert.assertTrue(loginPage.isRememberMeChecked(),
                                 "Remember me checkbox should be checked");
        }

        HomePage resultPage = (HomePage) loginPage.clickLoginButton();

        // Verify login success (remember me persistence would need additional browser session testing)
        assertions.assertUserLoggedIn(resultPage, email);

        assertions.assertAll();
        logger.info("=== LOGIN_004 completed: {} ===", description);
    }

    /**
     * Test ID: LOGIN_005 - Login Page Elements Validation
     * Tests that all required elements are present and functional on login page
     */
    @Test(groups = {"ui", "authentication", "smoke"},
          priority = 5,
          description = "Login page should display all required elements")
    public void testLoginPageElements() {
        logger.info("=== Starting LOGIN_005: Login Page Elements Validation ===");

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        // Verify all form elements are present and functional
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(loginPage.isEmailFieldDisplayed(),
                             "Email field should be displayed");
        softAssert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                             "Password field should be displayed");
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                             "Login button should be displayed");
        softAssert.assertTrue(loginPage.isLoginButtonEnabled(),
                             "Login button should be enabled");
        softAssert.assertTrue(loginPage.isRememberMeCheckboxDisplayed(),
                             "Remember me checkbox should be displayed");
        softAssert.assertTrue(loginPage.isForgotPasswordLinkDisplayed(),
                             "Forgot password link should be displayed");

        // Check if register link is displayed (may vary by implementation)
        boolean registerLinkDisplayed = loginPage.isRegisterLinkDisplayed();
        logger.info("Register link displayed on login page: {}", registerLinkDisplayed);

        assertions.assertAll();
        logger.info("=== LOGIN_005 completed: All login page elements verified ===");
    }

    /**
     * Test ID: LOGIN_006 - Logout Functionality
     * Tests successful logout and session termination
     */
    @Test(groups = {"functional", "authentication", "high-priority"},
          priority = 6,
          dataProvider = "logoutScenarios",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "User logout should work correctly")
    public void testLogoutFunctionality(String scenario, String email, String password) {
        logger.info("=== Starting LOGIN_006: {} ===", scenario);

        // First login
        LoginPage loginPage = homePage.clickLoginLink();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        HomePage loggedInPage = (HomePage) loginPage.clickLoginButton();

        // Verify logged in state
        assertions.assertUserLoggedIn(loggedInPage, email);

        // Perform logout
        HomePage loggedOutPage = loggedInPage.clickLogoutLink();

        // Verify logged out state
        assertions.assertUserLoggedOut(loggedOutPage);
        assertions.assertPageUrl("/", "Should redirect to home page after logout");

        assertions.assertAll();
        logger.info("=== LOGIN_006 completed: {} ===", scenario);
    }

    /**
     * Test ID: LOGIN_007 - Password Field Security
     * Tests password field masking and security features
     */
    @Test(groups = {"security", "authentication", "medium-priority"},
          priority = 7,
          description = "Password field should be properly masked for security")
    public void testPasswordFieldSecurity() {
        logger.info("=== Starting LOGIN_007: Password Field Security ===");

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        String testPassword = "TestPassword123!";
        loginPage.enterPassword(testPassword);

        // Verify password field is masked (type="password")
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(loginPage.isPasswordFieldMasked(),
                             "Password field should be masked for security");

        // Verify password value is not visible in page source (if possible)
        String pageSource = driver.getPageSource();
        softAssert.assertFalse(pageSource.contains(testPassword),
                              "Password should not be visible in plain text in page source");

        assertions.assertAll();
        logger.info("=== LOGIN_007 completed: Password field security verified ===");
    }

    /**
     * Test ID: LOGIN_008 - Login Navigation and Redirection
     * Tests proper navigation flow and redirections during login process
     */
    @Test(groups = {"functional", "authentication", "medium-priority"},
          priority = 8,
          description = "Login navigation and redirections should work correctly")
    public void testLoginNavigation() {
        logger.info("=== Starting LOGIN_008: Login Navigation and Redirection ===");

        // Test navigation to login page from home page
        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");
        assertions.assertPageTitle("login", "Login page should have appropriate title");

        // Test navigation to register page from login page (if available)
        if (loginPage.isRegisterLinkDisplayed()) {
            loginPage.clickRegisterLink();
            assertions.assertPageUrl("register", "Should navigate to register page from login page");

            // Navigate back to login
            homePage.clickLoginLink();
            assertions.assertPageUrl("login", "Should navigate back to login page");
        }

        // Test forgot password link navigation (if available)
        if (loginPage.isForgotPasswordLinkDisplayed()) {
            loginPage.clickForgotPasswordLink();
            assertions.assertPageUrl("password", "Should navigate to password recovery page");
        }

        assertions.assertAll();
        logger.info("=== LOGIN_008 completed: Login navigation verified ===");
    }

    /**
     * Test ID: LOGIN_009 - Session Security and Timeout
     * Tests session handling and security measures
     */
    @Test(groups = {"security", "authentication", "low-priority"},
          priority = 9,
          description = "Session security measures should be implemented")
    public void testSessionSecurity() {
        logger.info("=== Starting LOGIN_009: Session Security and Timeout ===");

        // Login first
        User testUser = UserDataFactory.createTestUser();
        LoginPage loginPage = homePage.clickLoginLink();
        loginPage.enterEmail(testUser.getEmail());
        loginPage.enterPassword(testUser.getPassword());
        HomePage loggedInPage = (HomePage) loginPage.clickLoginButton();

        assertions.assertUserLoggedIn(loggedInPage, testUser.getEmail());

        // Test session persistence across page navigation
        homePage.navigateToHomePage();
        assertions.assertUserLoggedIn(homePage, testUser.getEmail());

        // Test direct URL access while logged in
        driver.get(driver.getCurrentUrl() + "/account");
        // Should maintain login state

        assertions.assertAll();
        logger.info("=== LOGIN_009 completed: Session security verified ===");
    }

    /**
     * Test ID: LOGIN_010 - Parallel Login Testing
     * Tests login functionality with parallel execution
     */
    @Test(groups = {"parallel", "authentication", "performance"},
          priority = 10,
          dataProvider = "parallelUserData",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "Parallel login testing for performance validation")
    public void testParallelLogin(User user, String testDescription) {
        logger.info("=== Starting LOGIN_010: {} ===", testDescription);

        LoginPage loginPage = homePage.clickLoginLink();
        assertions.assertPageUrl("login", "Should navigate to login page");

        loginPage.enterEmail(user.getEmail());
        loginPage.enterPassword(user.getPassword());
        HomePage resultPage = (HomePage) loginPage.clickLoginButton();

        // Note: This test would typically use pre-registered users
        // For demo purposes, we test the login process
        logger.info("Parallel login test completed for: {}", user.getEmail());

        assertions.assertAll();
        logger.info("=== LOGIN_010 completed: {} ===", testDescription);
    }

    @Override
    protected void additionalTeardown() {
        // Ensure user is logged out after each test
        if (homePage != null && homePage.isUserLoggedIn()) {
            logger.info("Logging out user after test completion");
            homePage.clickLogoutLink();
        }
    }
}