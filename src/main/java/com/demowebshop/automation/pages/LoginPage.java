package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

/**
 * Page Object Model for the Login Page
 * Migrated to use Selenide as primary automation approach
 * Provides reliable methods for user authentication and form validation
 */
public class LoginPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/login";

    // Selenide elements - no need for @FindBy
    private final SelenideElement emailInput = $("input[name='Email']");
    private final SelenideElement passwordInput = $("input[name='Password']");
    private final SelenideElement rememberMeCheckbox = $("input[name='RememberMe']");
    private final SelenideElement loginButton = $("input[type='submit'][value='Log in']");

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

    // Form Interaction Methods - Selenide Primary

    /**
     * Enter email address using Selenide
     * @param email Email address to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterEmail(String email) {
        $("input[name='Email']").setValue(email);
        logger.info("Entered email: {}", email);
        return this;
    }

    /**
     * Enter password using Selenide
     * @param password Password to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterPassword(String password) {
        $("input[name='Password']").setValue(password);
        logger.info("Entered password");
        return this;
    }

    /**
     * Set remember me checkbox state using Selenide
     * @param remember true to check, false to uncheck
     * @return LoginPage for method chaining
     */
    public LoginPage setRememberMe(boolean remember) {
        SelenideElement checkbox = $("input[name='RememberMe']");
        if (remember != checkbox.isSelected()) {
            checkbox.click();
            logger.info("Set remember me to: {}", remember);
        }
        return this;
    }

    /**
     * Click login button using Selenide
     * @return HomePage if login successful, LoginPage if failed
     */
    public BasePage clickLoginButton() {
        $("input[type='submit'][value='Log in']").click();
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
            return new HomePage();
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
            // DemoWebShop shows validation errors in different ways - check all possible indicators

            // Check if we're still on login page after submit (failed login stays on same page)
            if (!getCurrentUrl().contains("/login")) {
                return false; // Successful login - redirected away
            }

            // Try primary selector from authentication-selectors.json using soft wait
            try {
                By errorSelector = SelectorUtils.getAuthSelector("authentication.login_page.validation.error_messages");
                SelenideElement errorElement = $(errorSelector);
                if (errorElement != null) {
                    String errorText = errorElement.getText();
                    if (errorText != null && !errorText.trim().isEmpty()) {
                        logger.debug("Found validation error with primary selector: {}", errorText);
                        return true;
                    }
                }
            } catch (Exception ignored) {
                // Continue with fallback approaches
            }

            // Check for common validation error patterns
            String[] fallbackSelectors = {
                ".validation-summary-errors",
                ".validation-errors",
                ".field-validation-error",
                ".error-message",
                ".login-error",
                ".message-error",
                ".error",
                "[class*='error']:not([class*='no-error'])",
                "[class*='validation']:not([class*='valid'])",
                ".alert-danger",
                ".alert-error",
                "div[style*='color: red']",
                "span[style*='color: red']",
                ".text-danger"
            };

            for (String selector : fallbackSelectors) {
                try {
                    By fallbackBy = By.cssSelector(selector);
                    SelenideElement errorElement = $(fallbackBy);
                    if (errorElement != null) {
                        String errorText = errorElement.getText();
                        if (errorText != null && !errorText.trim().isEmpty()) {
                            logger.debug("Found validation error with fallback selector '{}': {}", selector, errorText);
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check page source for error indicators (last resort)
            try {
                String pageSource = driver.getPageSource().toLowerCase();
                String[] errorIndicators = {
                    "login was unsuccessful",
                    "invalid email",
                    "invalid password",
                    "authentication failed",
                    "login failed",
                    "incorrect email or password",
                    "validation error",
                    "field is required"
                };

                for (String indicator : errorIndicators) {
                    if (pageSource.contains(indicator)) {
                        logger.debug("Found validation error indicator in page source: {}", indicator);
                        return true;
                    }
                }
            } catch (Exception ignored) {
                // Fallback failed
            }

            return false;
        } catch (Exception e) {
            logger.debug("Error checking for validation errors: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get all validation error messages
     * @return List of error messages
     */
    public List<String> getValidationErrors() {
        try {
            // If no validation errors detected, return empty list
            if (!hasValidationErrors()) {
                return List.of();
            }

            // Try primary selector first
            try {
                By errorSelector = SelectorUtils.getAuthSelector("authentication.login_page.validation.error_messages");
                if (isElementDisplayed(errorSelector)) {
                    SelenideElement errorContainer = $(errorSelector);
                    String errorText = errorContainer.getText();
                    if (errorText != null && !errorText.trim().isEmpty()) {
                        return List.of(errorText.split("\n"));
                    }
                }
            } catch (Exception ignored) {
                // Continue with fallbacks
            }

            // Try fallback selectors with enhanced list
            String[] fallbackSelectors = {
                ".validation-summary-errors",
                ".validation-errors",
                ".field-validation-error",
                ".error-message",
                ".login-error",
                ".message-error",
                ".error",
                "[class*='error']:not([class*='no-error'])",
                "[class*='validation']:not([class*='valid'])",
                ".alert-danger",
                ".alert-error",
                "div[style*='color: red']",
                "span[style*='color: red']",
                ".text-danger"
            };

            for (String selector : fallbackSelectors) {
                try {
                    By fallbackBy = By.cssSelector(selector);
                    if (isElementDisplayed(fallbackBy)) {
                        SelenideElement errorContainer = $(fallbackBy);
                        String errorText = errorContainer.getText();
                        if (errorText != null && !errorText.trim().isEmpty()) {
                            return List.of(errorText.split("\n"));
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // If still on login page after submit, assume validation failed
            if (getCurrentUrl().contains("/login")) {
                return List.of("Login validation failed - still on login page");
            }

            return List.of();
        } catch (Exception e) {
            logger.warn("Failed to get validation errors: {}", e.getMessage());
            return List.of();
        }
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
            ElementsCollection fieldErrors = $$(fieldErrorSelector);
            return fieldErrors.stream()
                    .map(SelenideElement::getText)
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
        return emailInput.getValue();
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
            rememberMeCheckbox.click();
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

    /**
     * Check if account lockout message is displayed
     * @return true if account lockout message is visible
     */
    public boolean hasAccountLockoutMessage() {
        try {
            By lockoutSelector = By.cssSelector(".error-message, .validation-summary-errors, .field-validation-error");
            return isElementDisplayed(lockoutSelector) &&
                   (getText(lockoutSelector).toLowerCase().contains("locked") ||
                    getText(lockoutSelector).toLowerCase().contains("disabled") ||
                    getText(lockoutSelector).toLowerCase().contains("blocked"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if rate limiting message is displayed
     * @return true if rate limiting message is visible
     */
    public boolean hasRateLimitingMessage() {
        try {
            By rateLimitSelector = By.cssSelector(".error-message, .validation-summary-errors, .field-validation-error");
            return isElementDisplayed(rateLimitSelector) &&
                   (getText(rateLimitSelector).toLowerCase().contains("too many attempts") ||
                    getText(rateLimitSelector).toLowerCase().contains("rate limit") ||
                    getText(rateLimitSelector).toLowerCase().contains("try again later"));
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== SELENIDE ENHANCED METHODS ====================

    /**
     * Enter email using Selenide (more reliable)
     * @param email Email address to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterEmailSelenide(String email) {
        typeSelenide("input[name='Email']", email);
        logger.info("Entered email using Selenide: {}", email);
        return this;
    }

    /**
     * Enter password using Selenide (more reliable)
     * @param password Password to enter
     * @return LoginPage for method chaining
     */
    public LoginPage enterPasswordSelenide(String password) {
        typeSelenide("input[name='Password']", password);
        logger.info("Entered password using Selenide");
        return this;
    }

    /**
     * Click login button using Selenide (more reliable)
     * @return HomePage if successful, LoginPage if failed
     */
    public BasePage clickLoginButtonSelenide() {
        clickSelenide("input[type='submit'][value='Log in']");
        logger.info("Clicked login button using Selenide");

        // Brief wait for page response
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check success by URL or error presence
        if (hasValidationErrors()) {
            logger.warn("Login failed using Selenide - validation errors present");
            return this;
        } else {
            logger.info("Login successful using Selenide - redirecting to homepage");
            return new HomePage(driver);
        }
    }

    /**
     * Perform complete login using Selenide methods
     * @param email Email address
     * @param password Password
     * @return HomePage if successful, LoginPage if failed
     */
    public BasePage loginSelenide(String email, String password) {
        return loginSelenide(email, password, false);
    }

    /**
     * Perform complete login using Selenide with remember me option
     * @param email Email address
     * @param password Password
     * @param rememberMe Whether to check remember me
     * @return HomePage if successful, LoginPage if failed
     */
    public BasePage loginSelenide(String email, String password, boolean rememberMe) {
        enterEmailSelenide(email);
        enterPasswordSelenide(password);
        if (rememberMe) {
            setRememberMeSelenide(true);
        }
        return clickLoginButtonSelenide();
    }

    /**
     * Set remember me checkbox using Selenide
     * @param remember true to check, false to uncheck
     * @return LoginPage for method chaining
     */
    public LoginPage setRememberMeSelenide(boolean remember) {
        if (remember != $("input[name='RememberMe']").isSelected()) {
            clickSelenide("input[name='RememberMe']");
            logger.info("Set remember me using Selenide to: {}", remember);
        }
        return this;
    }

    /**
     * Clear form fields using Selenide
     * @return LoginPage for method chaining
     */
    public LoginPage clearFormSelenide() {
        $("input[name='Email']").clear();
        $("input[name='Password']").clear();
        if ($("input[name='RememberMe']").isSelected()) {
            $("input[name='RememberMe']").click();
        }
        logger.info("Cleared login form using Selenide");
        return this;
    }

    /**
     * Check if validation errors are visible using Selenide
     * @return true if errors are displayed
     */
    public boolean hasValidationErrorsSelenide() {
        return isVisibleSelenide(".validation-summary-errors") ||
               isVisibleSelenide(".field-validation-error") ||
               isVisibleSelenide(".error-message");
    }

    /**
     * Get validation error message using Selenide
     * @return Error message text or empty string
     */
    public String getValidationErrorMessageSelenide() {
        if (isVisibleSelenide(".validation-summary-errors")) {
            return getTextSelenide(".validation-summary-errors");
        } else if (isVisibleSelenide(".field-validation-error")) {
            return getTextSelenide(".field-validation-error");
        } else if (isVisibleSelenide(".error-message")) {
            return getTextSelenide(".error-message");
        }
        return "";
    }

    /**
     * Check if email field is displayed using Selenide
     * @return true if email field is visible
     */
    public boolean isEmailFieldDisplayedSelenide() {
        return isVisibleSelenide("input[name='Email']");
    }

    /**
     * Check if password field is displayed using Selenide
     * @return true if password field is visible
     */
    public boolean isPasswordFieldDisplayedSelenide() {
        return isVisibleSelenide("input[name='Password']");
    }

    /**
     * Check if login button is displayed using Selenide
     * @return true if login button is visible
     */
    public boolean isLoginButtonDisplayedSelenide() {
        return isVisibleSelenide("input[type='submit'][value='Log in']");
    }

    /**
     * Get email field value using Selenide
     * @return Current email value
     */
    public String getEmailValueSelenide() {
        return $("input[name='Email']").getValue();
    }

    /**
     * Check if remember me is selected using Selenide
     * @return true if checkbox is checked
     */
    public boolean isRememberMeSelectedSelenide() {
        return $("input[name='RememberMe']").isSelected();
    }
}