package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for the Login Page
 * Provides methods for user authentication and form validation
 */
public class LoginPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/login";

    // Static elements using @FindBy
    @FindBy(name = "Email")
    private WebElement emailInput;

    @FindBy(name = "Password")
    private WebElement passwordInput;

    @FindBy(name = "RememberMe")
    private WebElement rememberMeCheckbox;

    @FindBy(css = "input[type='submit'][value='Log in']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage() {
        super();
    }

    /**
     * Navigate directly to login page
     * @return LoginPage instance for method chaining
     */
    public LoginPage navigateToLoginPage() {
        navigateTo("https://demowebshop.tricentis.com/login");
        waitForPageToLoad();
        return this;
    }

    // Form Interaction Methods

    /**
     * Enter email address
     * @param email Email address to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterEmail(String email) {
        type(emailInput, email);
        logger.info("Entered email: {}", email);
        return this;
    }

    /**
     * Enter password
     * @param password Password to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        logger.info("Entered password");
        return this;
    }

    /**
     * Set remember me checkbox state
     * @param remember true to check, false to uncheck
     * @return LoginPage for method chaining
     */
    public LoginPage setRememberMe(boolean remember) {
        if (remember != rememberMeCheckbox.isSelected()) {
            click(rememberMeCheckbox);
            logger.info("Set remember me to: {}", remember);
        }
        return this;
    }

    /**
     * Click login button
     * @return HomePage if login successful, LoginPage if failed
     */
    public BasePage clickLoginButton() {
        click(loginButton);
        logger.info("Clicked login button");

        // Wait briefly for potential redirect or error messages
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if login was successful by looking for error messages
        if (hasValidationErrors()) {
            logger.warn("Login failed - validation errors present");
            return this;
        } else {
            logger.info("Login appears successful - redirecting to homepage");
            return new HomePage(driver);
        }
    }

    /**
     * Perform complete login with credentials
     * @param email Email address
     * @param password Password
     * @return HomePage if successful, LoginPage if failed
     */
    public BasePage login(String email, String password) {
        return login(email, password, false);
    }

    /**
     * Perform complete login with credentials and remember me option
     * @param email Email address
     * @param password Password
     * @param rememberMe Whether to check remember me
     * @return HomePage if successful, LoginPage if failed
     */
    public BasePage login(String email, String password, boolean rememberMe) {
        enterEmail(email);
        enterPassword(password);
        setRememberMe(rememberMe);
        return clickLoginButton();
    }

    // Navigation Methods

    /**
     * Click forgot password link
     * @return PasswordRecoveryPage
     */
    public PasswordRecoveryPage clickForgotPasswordLink() {
        By forgotPasswordSelector = SelectorUtils.getAuthSelector("authentication.login_page.links.forgot_password");
        click(forgotPasswordSelector);
        logger.info("Clicked forgot password link");
        return new PasswordRecoveryPage(driver);
    }

    /**
     * Click register link
     * @return RegisterPage
     */
    public RegisterPage clickRegisterLink() {
        By registerSelector = SelectorUtils.getAuthSelector("authentication.login_page.links.register_link");
        click(registerSelector);
        logger.info("Clicked register link");
        return new RegisterPage(driver);
    }

    // Validation and Error Handling Methods

    /**
     * Check if login form has validation errors
     * @return true if validation errors are present
     */
    public boolean hasValidationErrors() {
        try {
            By errorSelector = SelectorUtils.getAuthSelector("authentication.login_page.validation.error_messages");
            return isElementDisplayed(errorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all validation error messages
     * @return List of error messages
     */
    public List<String> getValidationErrors() {
        By errorSelector = SelectorUtils.getAuthSelector("authentication.login_page.validation.error_messages");
        if (isElementDisplayed(errorSelector)) {
            WebElement errorContainer = findElement(errorSelector);
            return List.of(errorContainer.getText().split("\n"));
        }
        return List.of();
    }

    /**
     * Get single validation error message (first error if multiple)
     * @return First error message or empty string
     */
    public String getValidationErrorMessage() {
        List<String> errors = getValidationErrors();
        return errors.isEmpty() ? "" : errors.get(0);
    }

    /**
     * Check remember me checkbox
     * @return LoginPage for method chaining
     */
    public LoginPage checkRememberMe() {
        return setRememberMe(true);
    }

    /**
     * Check if remember me is checked (alias for isRememberMeSelected)
     * @return true if remember me checkbox is checked
     */
    public boolean isRememberMeChecked() {
        return isRememberMeSelected();
    }

    /**
     * Check if email field is displayed
     * @return true if email input field is visible
     */
    public boolean isEmailFieldDisplayed() {
        return isElementDisplayed(By.name("Email"));
    }

    /**
     * Check if password field is displayed
     * @return true if password input field is visible
     */
    public boolean isPasswordFieldDisplayed() {
        return isElementDisplayed(By.name("Password"));
    }

    /**
     * Check if login button is displayed
     * @return true if login button is visible
     */
    public boolean isLoginButtonDisplayed() {
        return isElementDisplayed(By.cssSelector("input[type='submit'][value*='Log in'], .login-button"));
    }

    /**
     * Get field-specific validation errors
     * @return List of field validation errors
     */
    public List<String> getFieldValidationErrors() {
        try {
            By fieldErrorSelector = SelectorUtils.getAuthSelector("authentication.login_page.validation.field_errors");
            List<WebElement> fieldErrors = findElements(fieldErrorSelector);
            return fieldErrors.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if a specific field has a validation error
     * @param fieldName Name of the field to check (Email, Password)
     * @return true if field has validation error
     */
    public boolean hasFieldValidationError(String fieldName) {
        List<String> fieldErrors = getFieldValidationErrors();
        return fieldErrors.stream()
                .anyMatch(error -> error.toLowerCase().contains(fieldName.toLowerCase()));
    }

    // Form State Methods

    /**
     * Get current email input value
     * @return Current email value
     */
    public String getEmailValue() {
        return emailInput.getAttribute("value");
    }

    /**
     * Check if remember me is selected
     * @return true if remember me is checked
     */
    public boolean isRememberMeSelected() {
        return rememberMeCheckbox.isSelected();
    }

    /**
     * Clear all form fields
     * @return LoginPage for method chaining
     */
    public LoginPage clearForm() {
        emailInput.clear();
        passwordInput.clear();
        if (rememberMeCheckbox.isSelected()) {
            click(rememberMeCheckbox);
        }
        logger.info("Cleared login form");
        return this;
    }

    /**
     * Check if login button is enabled
     * @return true if login button can be clicked
     */
    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }

    /**
     * Check if remember me checkbox is displayed
     * @return true if remember me checkbox is visible
     */
    public boolean isRememberMeCheckboxDisplayed() {
        return isElementDisplayed(By.name("RememberMe"));
    }

    /**
     * Check if forgot password link is displayed
     * @return true if forgot password link is visible
     */
    public boolean isForgotPasswordLinkDisplayed() {
        try {
            By forgotPasswordSelector = By.cssSelector("a[href*='passwordrecovery'], .forgot-password");
            return isElementDisplayed(forgotPasswordSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if register link is displayed
     * @return true if register link is visible
     */
    public boolean isRegisterLinkDisplayed() {
        try {
            By registerSelector = By.cssSelector("a[href*='register'], .register-link");
            return isElementDisplayed(registerSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if password field is masked (input type password)
     * @return true if password field is masked
     */
    public boolean isPasswordFieldMasked() {
        try {
            String inputType = passwordInput.getAttribute("type");
            return "password".equals(inputType);
        } catch (Exception e) {
            return false;
        }
    }

    // Page Validation Methods

    /**
     * Verify that login page is loaded correctly
     * @return true if page is loaded correctly
     */
    @Override
    public boolean isPageLoaded() {
        try {
            // Check for essential login form elements
            return isElementDisplayed(By.name("Email")) &&
                   isElementDisplayed(By.name("Password")) &&
                   isElementDisplayed(By.cssSelector("input[type='submit'][value='Log in']"));
        } catch (Exception e) {
            logger.error("Error checking if login page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the page URL pattern for validation
     * @return Login page URL pattern
     */
    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Verify user is on login page by checking URL
     * @return true if on login page
     */
    public boolean isOnLoginPage() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN);
    }

    /**
     * Get page title
     * @return Login page title
     */
    public String getLoginPageTitle() {
        return getCurrentTitle();
    }
}