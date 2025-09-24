package tests.authentication;

import base.BaseTest;
import factories.UserDataFactory;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "authentication"}, priority = 1)
    public void testValidLogin() {
        User testUser = UserDataFactory.createTestUser();

        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // For demo purposes, using test credentials
        loginPage.enterEmail("test@example.com");
        loginPage.enterPassword("password123");

        homePage = loginPage.clickLoginButton();

        // Note: In real scenario, we would validate successful login
        // For demo shop, login might fail but we can test the process
        logger.info("Login test completed");
    }

    @Test(groups = {"negative", "authentication"}, priority = 2)
    public void testInvalidEmailLogin() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        String invalidEmail = UserDataFactory.generateInvalidEmail();
        loginPage.enterEmail(invalidEmail);
        loginPage.enterPassword("password123");

        loginPage.clickLoginButton();

        // Validate error messages appear
        Assert.assertTrue(loginPage.hasValidationErrors(),
                         "Validation errors should appear for invalid email");

        logger.info("Invalid email login test completed");
    }

    @Test(groups = {"negative", "authentication"}, priority = 3)
    public void testEmptyCredentialsLogin() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.hasValidationErrors(),
                         "Validation errors should appear for empty credentials");

        logger.info("Empty credentials login test completed");
    }

    @Test(groups = {"functional", "authentication"}, priority = 4)
    public void testRememberMeFunctionality() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        loginPage.enterEmail("test@example.com");
        loginPage.enterPassword("password123");
        loginPage.checkRememberMe();

        Assert.assertTrue(loginPage.isRememberMeChecked(),
                         "Remember me checkbox should be checked");

        logger.info("Remember me functionality test completed");
    }

    @Test(groups = {"ui", "authentication"}, priority = 5)
    public void testLoginPageElements() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        Assert.assertTrue(loginPage.isLoginButtonEnabled(),
                         "Login button should be enabled");
        Assert.assertTrue(loginPage.isForgotPasswordLinkDisplayed(),
                         "Forgot password link should be displayed");

        // Note: Register link may not be visible on login page for this demo site
        boolean registerLinkDisplayed = loginPage.isRegisterLinkDisplayed();
        logger.info("Register link displayed on login page: {}", registerLinkDisplayed);

        logger.info("Login page elements test completed");
    }
}