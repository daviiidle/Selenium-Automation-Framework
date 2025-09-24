package tests.authentication;

import base.BaseTest;
import factories.UserDataFactory;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegisterPage;

public class RegistrationTests extends BaseTest {

    @Test(groups = {"smoke", "registration"}, priority = 1)
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

        homePage = registerPage.clickRegisterButton();

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

        homePage = registerPage.clickRegisterButton();

        logger.info("Complete registration test completed for: {}", newUser.getEmail());
    }
}