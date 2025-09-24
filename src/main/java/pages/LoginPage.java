package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    // Page Elements
    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(id = "Password")
    private WebElement passwordField;

    @FindBy(id = "RememberMe")
    private WebElement rememberMeCheckbox;

    @FindBy(css = "input[value='Log in']")
    private WebElement loginButton;

    @FindBy(css = ".forgot-password a")
    private WebElement forgotPasswordLink;

    @FindBy(css = ".new-customer a")
    private WebElement registerLink;

    @FindBy(css = ".validation-summary-errors")
    private WebElement validationErrorsContainer;

    @FindBy(css = ".field-validation-error")
    private WebElement fieldValidationError;

    @FindBy(css = ".page-title h1")
    private WebElement pageTitle;

    @FindBy(css = ".returning-customer")
    private WebElement returningCustomerSection;

    @FindBy(css = ".new-customer")
    private WebElement newCustomerSection;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(emailField);
            waitForElementToBeVisible(passwordField);
            waitForElementToBeVisible(loginButton);
            return isElementDisplayed(emailField) &&
                   isElementDisplayed(passwordField) &&
                   isElementDisplayed(loginButton);
        } catch (Exception e) {
            logger.error("LoginPage is not loaded properly", e);
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return config.getBaseUrl() + "/login";
    }

    // Navigation actions
    public LoginPage navigateToLoginPage() {
        driver.get(getPageUrl());
        waitForPageToLoad();
        logger.info("Navigated to LoginPage");
        return this;
    }

    // Form actions
    public LoginPage enterEmail(String email) {
        safeType(emailField, email);
        logger.info("Entered email: {}", email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        safeType(passwordField, password);
        logger.info("Entered password");
        return this;
    }

    public LoginPage checkRememberMe() {
        if (!rememberMeCheckbox.isSelected()) {
            safeClick(rememberMeCheckbox);
            logger.info("Checked Remember Me checkbox");
        }
        return this;
    }

    public LoginPage uncheckRememberMe() {
        if (rememberMeCheckbox.isSelected()) {
            safeClick(rememberMeCheckbox);
            logger.info("Unchecked Remember Me checkbox");
        }
        return this;
    }

    // Login actions
    public HomePage loginWithValidCredentials(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        safeClick(loginButton);
        logger.info("Attempted login with email: {}", email);
        return new HomePage(driver);
    }

    public LoginPage loginWithInvalidCredentials(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        safeClick(loginButton);
        logger.info("Attempted login with invalid credentials for email: {}", email);
        return this;
    }

    public HomePage clickLoginButton() {
        safeClick(loginButton);
        logger.info("Clicked login button");

        // Check if login was successful by looking for validation errors
        if (hasValidationErrors()) {
            logger.warn("Login failed - validation errors present");
            return null;
        } else {
            logger.info("Login appears successful");
            return new HomePage(driver);
        }
    }

    // Navigation to other pages
    public RegisterPage clickRegisterLink() {
        safeClick(registerLink);
        logger.info("Clicked register link");
        return new RegisterPage(driver);
    }

    public ForgotPasswordPage clickForgotPasswordLink() {
        safeClick(forgotPasswordLink);
        logger.info("Clicked forgot password link");
        return new ForgotPasswordPage(driver);
    }

    // Validation methods
    public boolean hasValidationErrors() {
        return isElementDisplayed(validationErrorsContainer) || isElementDisplayed(fieldValidationError);
    }

    public String getValidationErrorMessage() {
        if (isElementDisplayed(validationErrorsContainer)) {
            return getElementText(validationErrorsContainer);
        } else if (isElementDisplayed(fieldValidationError)) {
            return getElementText(fieldValidationError);
        }
        return "";
    }

    public boolean isEmailFieldEmpty() {
        return getElementAttribute(emailField, "value").isEmpty();
    }

    public boolean isPasswordFieldEmpty() {
        return getElementAttribute(passwordField, "value").isEmpty();
    }

    public boolean isRememberMeChecked() {
        return rememberMeCheckbox.isSelected();
    }

    // Field validation
    public boolean isEmailFieldHighlighted() {
        String classAttribute = getElementAttribute(emailField, "class");
        return classAttribute.contains("input-validation-error");
    }

    public boolean isPasswordFieldHighlighted() {
        String classAttribute = getElementAttribute(passwordField, "class");
        return classAttribute.contains("input-validation-error");
    }

    // Getters for validation
    public String getPageTitle() {
        return getElementText(pageTitle);
    }

    public String getEmailFieldValue() {
        return getElementAttribute(emailField, "value");
    }

    public boolean isLoginButtonEnabled() {
        return isElementEnabled(loginButton);
    }

    public boolean isForgotPasswordLinkDisplayed() {
        return isElementDisplayed(forgotPasswordLink);
    }

    public boolean isRegisterLinkDisplayed() {
        return isElementDisplayed(registerLink);
    }

    public boolean isReturningCustomerSectionDisplayed() {
        return isElementDisplayed(returningCustomerSection);
    }

    public boolean isNewCustomerSectionDisplayed() {
        return isElementDisplayed(newCustomerSection);
    }

    // Utility methods for comprehensive login flow
    public HomePage performCompleteLogin(String email, String password, boolean rememberMe) {
        enterEmail(email);
        enterPassword(password);

        if (rememberMe) {
            checkRememberMe();
        } else {
            uncheckRememberMe();
        }

        return clickLoginButton();
    }

    public void clearLoginForm() {
        emailField.clear();
        passwordField.clear();
        if (rememberMeCheckbox.isSelected()) {
            uncheckRememberMe();
        }
        logger.info("Cleared login form");
    }
}