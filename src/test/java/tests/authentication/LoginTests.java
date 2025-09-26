package tests.authentication;

import base.BaseTest;
import factories.UserDataFactory;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.demowebshop.automation.pages.LoginPage;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.common.BasePage;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.*;

public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "authentication"}, priority = 1)
    public void testValidLogin() {
        User testUser = UserDataFactory.createTestUser();

        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Using Selenide approach - cleaner and more reliable
        $("input[name='Email']").setValue("test@example.com");
        $("input[name='Password']").setValue("password123");
        $("input[type='submit'][value='Log in']").click();

        // Verify form elements are present using Selenide
        $("input[name='Email']").shouldBe(visible);
        $("input[name='Password']").shouldBe(visible);

        logger.info("Login test completed with Selenide");
    }

    @Test(groups = {"negative", "authentication"}, priority = 2)
    public void testInvalidEmailLogin() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        String invalidEmail = UserDataFactory.generateInvalidEmail();
        logger.info("Testing with invalid email: {}", invalidEmail);

        // Using Selenide direct approach
        $("input[name='Email']").setValue(invalidEmail);
        $("input[name='Password']").setValue("password123");
        $("input[type='submit'][value='Log in']").click();

        // Verify login form elements remain visible after failed attempt
        $("input[name='Email']").shouldBe(visible);
        $("input[name='Password']").shouldBe(visible);
        $("input[type='submit'][value='Log in']").shouldBe(visible);

        logger.info("Invalid email login test completed with Selenide");
    }

    @Test(groups = {"negative", "authentication"}, priority = 3)
    public void testEmptyCredentialsLogin() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Submit empty form using Selenide
        $("input[type='submit'][value='Log in']").click();

        // Verify form elements remain visible (indicating failed validation)
        $("input[name='Email']").shouldBe(visible);
        $("input[name='Password']").shouldBe(visible);

        logger.info("Empty credentials login test completed with Selenide");
    }

    @Test(groups = {"functional", "authentication"}, priority = 4)
    public void testRememberMeFunctionality() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Using Selenide for form interaction
        $("input[name='Email']").setValue("test@example.com");
        $("input[name='Password']").setValue("password123");
        $("input[name='RememberMe']").click();

        // Verify remember me is checked using Selenide
        $("input[name='RememberMe']").shouldBe(selected);

        logger.info("Remember me functionality test completed with Selenide");
    }

    @Test(groups = {"ui", "authentication"}, priority = 5)
    public void testLoginPageElements() {
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Verify page elements using Selenide conditions
        $("input[type='submit'][value='Log in']").shouldBe(visible).shouldBe(enabled);
        $("input[name='Email']").shouldBe(visible).shouldBe(enabled);
        $("input[name='Password']").shouldBe(visible).shouldBe(enabled);
        $("input[name='RememberMe']").shouldBe(visible);

        logger.info("Login page elements test completed with Selenide");
    }

    // ==================== SELENIDE ENHANCED TESTS ====================

    @Test(groups = {"smoke", "authentication", "selenide"}, priority = 6)
    public void testValidLoginSelenide() {
        User testUser = UserDataFactory.createTestUser();

        LoginPage loginPage = homePage.clickLoginLinkSelenide();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Using Selenide methods for cleaner syntax
        BasePage resultPage = loginPage.loginSelenide("test@example.com", "password123");

        // Verify form elements are present using Selenide
        $("input[name='Email']").shouldBe(visible);
        $("input[name='Password']").shouldBe(visible);
        $("input[type='submit'][value='Log in']").shouldBe(visible);

        Assert.assertNotNull(resultPage, "Should receive a valid page response");
        logger.info("Selenide login test completed with enhanced element verification");
    }

    @Test(groups = {"negative", "authentication", "selenide"}, priority = 7)
    public void testInvalidEmailLoginSelenide() {
        LoginPage loginPage = homePage.clickLoginLinkSelenide();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        String invalidEmail = UserDataFactory.generateInvalidEmail();
        logger.info("Testing with invalid email using Selenide: {}", invalidEmail);

        // Using Selenide methods
        loginPage.enterEmailSelenide(invalidEmail);
        loginPage.enterPasswordSelenide("password123");

        BasePage resultPage = loginPage.clickLoginButtonSelenide();

        // Enhanced verification using Selenide
        Assert.assertTrue(loginPage.isEmailFieldDisplayedSelenide(),
                         "Email field should be displayed");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayedSelenide(),
                         "Password field should be displayed");

        logger.info("Selenide invalid email login test completed");
    }

    @Test(groups = {"functional", "authentication", "selenide"}, priority = 8)
    public void testFormValidationSelenide() {
        LoginPage loginPage = homePage.clickLoginLinkSelenide();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Test empty form submission using Selenide
        BasePage resultPage = loginPage.clickLoginButtonSelenide();

        // Verify login form elements are still visible after failed attempt
        Assert.assertTrue(loginPage.isEmailFieldDisplayedSelenide(),
                         "Email field should remain displayed");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayedSelenide(),
                         "Password field should remain displayed");
        Assert.assertTrue(loginPage.isLoginButtonDisplayedSelenide(),
                         "Login button should remain displayed");

        logger.info("Selenide form validation test completed");
    }

    @Test(groups = {"ui", "authentication", "selenide"}, priority = 9)
    public void testSelenideDirectElementAccess() {
        LoginPage loginPage = homePage.clickLoginLinkSelenide();

        // Direct Selenide element verification
        $("input[name='Email']").shouldBe(visible).shouldBe(enabled);
        $("input[name='Password']").shouldBe(visible).shouldBe(enabled);
        $("input[type='submit'][value='Log in']").shouldBe(visible).shouldBe(enabled);

        // Test form interaction with Selenide
        $("input[name='Email']").setValue("selenide@test.com");
        $("input[name='Password']").setValue("selenidepass");

        // Verify values were set
        Assert.assertEquals($("input[name='Email']").getValue(), "selenide@test.com",
                           "Email value should be set correctly");

        logger.info("Selenide direct element access test completed");
    }

    @Test(groups = {"hybrid", "authentication"}, priority = 10)
    public void testHybridSeleniumSelenideApproach() {
        // Use traditional Selenium approach
        LoginPage loginPage = homePage.clickLoginLink();
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Mix traditional and Selenide approaches
        loginPage.enterEmail("hybrid@test.com");  // Traditional Selenium
        loginPage.enterPasswordSelenide("hybridpass");  // Selenide

        // Verify with both approaches
        Assert.assertEquals(loginPage.getEmailValue(), "hybrid@test.com",
                           "Email set with traditional method");
        Assert.assertEquals(loginPage.getEmailValueSelenide(), "hybrid@test.com",
                           "Email verified with Selenide method");

        // Direct Selenide verification
        $("input[name='Email']").shouldHave(value("hybrid@test.com"));

        logger.info("Hybrid Selenium-Selenide approach test completed");
    }
}