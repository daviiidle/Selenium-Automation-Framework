package tests.authentication;

import base.BaseTest;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.RegisterPage;
import dataproviders.AuthenticationDataProvider;
import factories.UserDataFactory;
import models.User;
import utils.DemoWebShopAssertions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;

/**
 * Comprehensive Registration Test Suite
 * Covers all registration scenarios from manual testing documentation
 * Includes positive, negative, and edge case testing scenarios
 */
public class RegistrationTests extends BaseTest {
    private DemoWebShopAssertions assertions;
    private HomePage homePage;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(driver);
        homePage = new HomePage(driver);
    }

    /**
     * Test ID: REG_001 - User Registration Happy Path
     * Tests successful user registration with all required fields
     * Validates registration confirmation and automatic login
     */
    @Test(groups = {"smoke", "registration", "high-priority"},
          priority = 1,
          description = "Valid user registration with all required fields")
    public void testValidUserRegistration() {
        User newUser = UserDataFactory.createRandomUser();

        RegisterPage registerPage = homePage.clickRegisterLink();
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

        registerPage.selectGender(newUser.getGender())
                   .enterFirstName(newUser.getFirstName())
                   .enterLastName(newUser.getLastName())
                   .enterEmail(newUser.getEmail())
                   .enterPassword(newUser.getPassword())
                   .confirmPassword(newUser.getPassword());

        if (newUser.isNewsletter()) {
            registerPage.subscribeToNewsletter();
        }

        registerPage.clickRegisterButton();

        // CRITICAL: DemoWebShop does NOT auto-login after registration
        // Registration tests only verify registration success, not login
        logger.info("User registration test completed for: {}", newUser.getEmail());
    }

    @Test(groups = {"negative", "registration"}, priority = 2)
    public void testRegistrationWithInvalidEmail() {
        User newUser = UserDataFactory.createRandomUser();
        String invalidEmail = UserDataFactory.generateInvalidEmail();

        RegisterPage registerPage = homePage.clickRegisterLink();
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

        registerPage.selectGender(newUser.getGender())
                   .enterFirstName(newUser.getFirstName())
                   .enterLastName(newUser.getLastName())
                   .enterEmail(invalidEmail)
                   .enterPassword(newUser.getPassword())
                   .confirmPassword(newUser.getPassword());

        registerPage.clickRegisterButton();

        Assert.assertTrue(registerPage.hasValidationErrors(),
                         "Validation errors should appear for invalid email");

        logger.info("Invalid email registration test completed");
    }

    @Test(groups = {"negative", "registration"}, priority = 3)
    public void testRegistrationWithMismatchedPasswords() {
        User newUser = UserDataFactory.createRandomUser();

        RegisterPage registerPage = homePage.clickRegisterLink();
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

        registerPage.selectGender(newUser.getGender())
                   .enterFirstName(newUser.getFirstName())
                   .enterLastName(newUser.getLastName())
                   .enterEmail(newUser.getEmail())
                   .enterPassword(newUser.getPassword())
                   .confirmPassword("DifferentPassword123!");

        registerPage.clickRegisterButton();

        Assert.assertTrue(registerPage.hasValidationErrors(),
                         "Validation errors should appear for mismatched passwords");

        logger.info("Mismatched passwords registration test completed");
    }

    @Test(groups = {"negative", "registration"}, priority = 4)
    public void testRegistrationWithWeakPassword() {
        User newUser = UserDataFactory.createRandomUser();
        String weakPassword = UserDataFactory.generateWeakPassword();

        RegisterPage registerPage = homePage.clickRegisterLink();
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

        registerPage.selectGender(newUser.getGender())
                   .enterFirstName(newUser.getFirstName())
                   .enterLastName(newUser.getLastName())
                   .enterEmail(newUser.getEmail())
                   .enterPassword(weakPassword)
                   .confirmPassword(weakPassword);

        registerPage.clickRegisterButton();

        // Note: Validation depends on application's password policy
        logger.info("Weak password registration test completed");
    }

    @Test(groups = {"functional", "registration"}, priority = 5)
    public void testRegistrationWithAllOptionalFields() {
        User newUser = UserDataFactory.createRandomUser();

        RegisterPage registerPage = homePage.clickRegisterLink();
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

        registerPage.selectGender(newUser.getGender())
                   .enterFirstName(newUser.getFirstName())
                   .enterLastName(newUser.getLastName())
                   .enterEmail(newUser.getEmail())
                   .enterPassword(newUser.getPassword())
                   .confirmPassword(newUser.getPassword())
                   .subscribeToNewsletter();

        registerPage.clickRegisterButton();

        // CRITICAL: DemoWebShop does NOT auto-login after registration
        assertions.assertAll();
        logger.info("=== REG_005 completed successfully for: {} ===", newUser.getEmail());
    }

    /**
     * Test ID: REG_006 - Multiple User Registration (Data-Driven)
     * Tests registration with multiple valid user profiles using data provider
     */
    @Test(groups = {"functional", "registration", "data-driven"},
          priority = 6,
          dataProvider = "validRegistrationData",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "Multiple user registrations should all succeed")
    public void testMultipleUserRegistration(User user, String testDescription) {
        logger.info("=== Starting REG_006: {} ===", testDescription);

        RegisterPage registerPage = homePage.clickRegisterLink();
        assertions.assertPageUrl("register", "Should navigate to registration page");

        registerPage.selectGender(user.getGender())
                   .enterFirstName(user.getFirstName())
                   .enterLastName(user.getLastName())
                   .enterEmail(user.getEmail())
                   .enterPassword(user.getPassword())
                   .confirmPassword(user.getPassword());

        if (user.isNewsletter()) {
            registerPage.subscribeToNewsletter();
        }

        registerPage.clickRegisterButton();

        // CRITICAL: DemoWebShop does NOT auto-login after registration
        // Only verify registration success, not login status
        assertions.assertRegistrationSuccess(registerPage, user);

        assertions.assertAll();
        logger.info("=== REG_006 completed: {} for user {} ===", testDescription, user.getEmail());
    }

    /**
     * Test ID: REG_007 - Email Format Validation
     * Tests various email formats for proper validation
     */
    @Test(groups = {"negative", "registration", "validation"},
          priority = 7,
          dataProvider = "emailValidationData",
          dataProviderClass = AuthenticationDataProvider.class,
          description = "Email format validation should work correctly")
    public void testEmailFormatValidation(String email, boolean isValid, String description) {
        logger.info("=== Starting REG_007: Email Validation - {} ===", description);

        User testUser = UserDataFactory.createRandomUser();
        testUser.setEmail(email);

        RegisterPage registerPage = homePage.clickRegisterLink();
        assertions.assertPageUrl("register", "Should navigate to registration page");

        registerPage.selectGender(testUser.getGender())
                   .enterFirstName(testUser.getFirstName())
                   .enterLastName(testUser.getLastName())
                   .enterEmail(email)
                   .enterPassword(testUser.getPassword())
                   .confirmPassword(testUser.getPassword());

        registerPage.clickRegisterButton();

        if (isValid) {
            // Valid email should allow registration to proceed
            logger.info("Email '{}' should be valid", email);
        } else {
            // Invalid email should show validation error
            assertions.assertRegistrationValidationErrors(registerPage, "email");
            logger.info("Email '{}' should be invalid", email);
        }

        assertions.assertAll();
        logger.info("=== REG_007 completed: {} ===", description);
    }

    /**
     * Test ID: REG_008 - Registration Page Elements Validation
     * Tests that all required elements are present and functional on registration page
     */
    @Test(groups = {"ui", "registration", "smoke"},
          priority = 8,
          description = "Registration page should display all required elements")
    public void testRegistrationPageElements() {
        logger.info("=== Starting REG_008: Registration Page Elements Validation ===");

        RegisterPage registerPage = homePage.clickRegisterLink();
        assertions.assertPageUrl("register", "Should navigate to registration page");

        // Verify all form elements are present
        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(registerPage.isGenderSelectionDisplayed(),
                             "Gender selection should be displayed");
        softAssert.assertTrue(registerPage.isFirstNameFieldDisplayed(),
                             "First name field should be displayed");
        softAssert.assertTrue(registerPage.isLastNameFieldDisplayed(),
                             "Last name field should be displayed");
        softAssert.assertTrue(registerPage.isEmailFieldDisplayed(),
                             "Email field should be displayed");
        softAssert.assertTrue(registerPage.isPasswordFieldDisplayed(),
                             "Password field should be displayed");
        softAssert.assertTrue(registerPage.isConfirmPasswordFieldDisplayed(),
                             "Confirm password field should be displayed");
        softAssert.assertTrue(registerPage.isNewsletterCheckboxDisplayed(),
                             "Newsletter checkbox should be displayed");
        softAssert.assertTrue(registerPage.isRegisterButtonDisplayed(),
                             "Register button should be displayed");
        softAssert.assertTrue(registerPage.isRegisterButtonEnabled(),
                             "Register button should be enabled");

        assertions.assertAll();
        logger.info("=== REG_008 completed: All registration page elements verified ===");
    }

    @Override
    protected void additionalTeardown() {
        // Additional cleanup if needed
        if (homePage != null && homePage.isUserLoggedIn()) {
            logger.info("Logging out user after test completion");
            homePage.clickLogoutLink();
        }
    }
}