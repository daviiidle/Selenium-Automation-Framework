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
 * Page Object Model for the Registration Page
 * Provides methods for user registration and form validation
 */
public class RegisterPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/register";

    // Form elements using @FindBy
    @FindBy(css = "input[value='M']")
    private WebElement genderMaleRadio;

    @FindBy(css = "input[value='F']")
    private WebElement genderFemaleRadio;

    @FindBy(name = "FirstName")
    private WebElement firstNameInput;

    @FindBy(name = "LastName")
    private WebElement lastNameInput;

    @FindBy(name = "Email")
    private WebElement emailInput;

    @FindBy(name = "Password")
    private WebElement passwordInput;

    @FindBy(name = "ConfirmPassword")
    private WebElement confirmPasswordInput;

    @FindBy(css = "input[type='submit'][value='Register']")
    private WebElement registerButton;

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage() {
        super();
    }

    /**
     * Navigate directly to registration page
     * @return RegisterPage instance for method chaining
     */
    public RegisterPage navigateToRegisterPage() {
        navigateTo("https://demowebshop.tricentis.com/register");
        waitForPageToLoad();
        return this;
    }

    // Form Interaction Methods

    /**
     * Select gender
     * @param gender "Male" or "Female"
     * @return RegisterPage for method chaining
     */
    public RegisterPage selectGender(String gender) {
        if ("Male".equalsIgnoreCase(gender) || "M".equalsIgnoreCase(gender)) {
            if (!genderMaleRadio.isSelected()) {
                click(genderMaleRadio);
                logger.info("Selected Male gender");
            }
        } else if ("Female".equalsIgnoreCase(gender) || "F".equalsIgnoreCase(gender)) {
            if (!genderFemaleRadio.isSelected()) {
                click(genderFemaleRadio);
                logger.info("Selected Female gender");
            }
        } else {
            throw new IllegalArgumentException("Invalid gender: " + gender + ". Use 'Male' or 'Female'");
        }
        return this;
    }

    /**
     * Enter first name
     * @param firstName First name to enter
     * @return RegisterPage for method chaining
     */
    public RegisterPage enterFirstName(String firstName) {
        type(firstNameInput, firstName);
        logger.info("Entered first name: {}", firstName);
        return this;
    }

    /**
     * Enter last name
     * @param lastName Last name to enter
     * @return RegisterPage for method chaining
     */
    public RegisterPage enterLastName(String lastName) {
        type(lastNameInput, lastName);
        logger.info("Entered last name: {}", lastName);
        return this;
    }

    /**
     * Enter email address
     * @param email Email address to enter
     * @return RegisterPage for method chaining
     */
    public RegisterPage enterEmail(String email) {
        type(emailInput, email);
        logger.info("Entered email: {}", email);
        return this;
    }

    /**
     * Enter password
     * @param password Password to enter
     * @return RegisterPage for method chaining
     */
    public RegisterPage enterPassword(String password) {
        type(passwordInput, password);
        logger.info("Entered password");
        return this;
    }

    /**
     * Enter confirm password
     * @param confirmPassword Confirm password to enter
     * @return RegisterPage for method chaining
     */
    public RegisterPage enterConfirmPassword(String confirmPassword) {
        type(confirmPasswordInput, confirmPassword);
        logger.info("Entered confirm password");
        return this;
    }

    /**
     * Enter confirm password (alias method for compatibility)
     * @param confirmPassword Confirm password to enter
     * @return RegisterPage for method chaining
     */
    public RegisterPage confirmPassword(String confirmPassword) {
        return enterConfirmPassword(confirmPassword);
    }

    /**
     * Subscribe to newsletter by checking the newsletter checkbox
     * @return RegisterPage for method chaining
     */
    public RegisterPage subscribeToNewsletter() {
        try {
            By newsletterSelector = By.id("Newsletter");
            if (isElementDisplayed(newsletterSelector)) {
                WebElement newsletterCheckbox = findElement(newsletterSelector);
                if (!newsletterCheckbox.isSelected()) {
                    click(newsletterCheckbox);
                    logger.info("Subscribed to newsletter");
                }
            }
        } catch (Exception e) {
            logger.warn("Newsletter checkbox not found or not clickable");
        }
        return this;
    }

    /**
     * Check if registration was successful by looking for success indicators
     * @return true if registration was successful
     */
    public boolean isRegistrationSuccessful() {
        try {
            // Look for registration success indicators
            return getCurrentUrl().contains("registerresult") ||
                   isElementDisplayed(By.cssSelector(".result, .registration-result-page"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get registration success message
     * @return Success message text, empty if not found
     */
    public String getRegistrationSuccessMessage() {
        try {
            By successMsgSelector = By.cssSelector(".result, .registration-result");
            if (isElementDisplayed(successMsgSelector)) {
                return getText(successMsgSelector);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Click register button
     * @return HomePage if registration successful, RegisterPage if failed
     */
    public BasePage clickRegisterButton() {
        try {
            // Try multiple approaches to click the register button
            String[] buttonSelectors = {
                "input[type='submit'][value='Register']",
                "input[value='Register']",
                ".register-button",
                "button[type='submit']",
                "input[type='submit']"
            };

            boolean clicked = false;
            for (String selector : buttonSelectors) {
                try {
                    By buttonBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(buttonBy, 3) != null) {
                        // Try to click using enhanced element utils
                        elementUtils.clickElement(buttonBy);
                        logger.info("Successfully clicked register button using selector: {}", selector);
                        clicked = true;
                        break;
                    }
                } catch (Exception e) {
                    logger.debug("Register button click failed with selector '{}': {}", selector, e.getMessage());
                    // Continue to next selector
                }
            }

            // Fallback to original @FindBy element if all selectors failed
            if (!clicked) {
                try {
                    // Wait for the register button to be clickable using the standard method
                    WebElement clickableButton = waitUtils.waitForElementToBeClickable(registerButton, 5);
                    elementUtils.clickElement(clickableButton);
                    logger.info("Clicked register button using @FindBy element");
                    clicked = true;
                } catch (Exception e) {
                    logger.error("All register button click attempts failed: {}", e.getMessage());
                    throw new RuntimeException("Register button not clickable", e);
                }
            }

            if (!clicked) {
                throw new RuntimeException("Register button could not be clicked with any method");
            }

            // Wait briefly for processing
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Check if registration was successful
            if (hasValidationErrors()) {
                logger.warn("Registration failed - validation errors present");
                return this;
            } else {
                logger.info("Registration appears successful");
                return new HomePage(driver);
            }

        } catch (Exception e) {
            logger.error("Error clicking register button: {}", e.getMessage());
            throw new RuntimeException("Failed to click register button", e);
        }
    }

    /**
     * Perform complete registration
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email
     * @param password User's password
     * @return HomePage if successful, RegisterPage if failed
     */
    public BasePage register(String firstName, String lastName, String email, String password) {
        return register(null, firstName, lastName, email, password, password);
    }

    /**
     * Perform complete registration with all fields
     * @param gender User's gender ("Male" or "Female", optional)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email
     * @param password User's password
     * @param confirmPassword Confirm password
     * @return HomePage if successful, RegisterPage if failed
     */
    public BasePage register(String gender, String firstName, String lastName,
                            String email, String password, String confirmPassword) {

        if (gender != null && !gender.trim().isEmpty()) {
            selectGender(gender);
        }

        enterFirstName(firstName);
        enterLastName(lastName);
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(confirmPassword);

        return clickRegisterButton();
    }

    // Navigation Methods

    /**
     * Click privacy notice link
     * @return New page/tab with privacy policy
     */
    public void clickPrivacyNoticeLink() {
        By privacySelector = SelectorUtils.getAuthSelector("authentication.registration_page.links.privacy_notice");
        click(privacySelector);
        logger.info("Clicked privacy notice link");
    }

    /**
     * Click conditions of use link
     * @return New page/tab with conditions
     */
    public void clickConditionsOfUseLink() {
        By conditionsSelector = SelectorUtils.getAuthSelector("authentication.registration_page.links.conditions_of_use");
        click(conditionsSelector);
        logger.info("Clicked conditions of use link");
    }

    // Validation and Error Handling Methods

    /**
     * Check if registration form has validation errors
     * @return true if validation errors are present
     */
    public boolean hasValidationErrors() {
        try {
            // Try multiple selectors for validation errors
            String[] errorSelectors = {
                ".validation-summary-errors",
                ".field-validation-error",
                ".error-message",
                ".validation-error",
                ".form-errors",
                ".message-error",
                ".alert-danger",
                ".alert-error"
            };

            for (String selector : errorSelectors) {
                try {
                    By errorBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(errorBy, 2) != null) {
                        String errorText = getText(errorBy);
                        if (!errorText.trim().isEmpty()) {
                            logger.debug("Found validation error with selector '{}': {}", selector, errorText);
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check for any visible error text in common containers
            String[] containerSelectors = {
                ".result",
                ".message",
                ".content",
                ".registration-form",
                "form"
            };

            for (String selector : containerSelectors) {
                try {
                    By containerBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(containerBy, 2) != null) {
                        String text = getText(containerBy).toLowerCase();
                        if (text.contains("error") || text.contains("invalid") ||
                            text.contains("required") || text.contains("must") ||
                            text.contains("please") || text.contains("field")) {
                            logger.debug("Found validation error in container '{}': {}", selector, text);
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check if the error message list is not empty as final validation
            // BUT avoid infinite recursion by calling the underlying method directly
            List<String> errorMessages = getValidationErrorsInternal();
            boolean hasErrors = !errorMessages.isEmpty();
            if (hasErrors) {
                logger.debug("Found validation errors via getValidationErrorsInternal(): {}", errorMessages);
            }
            return hasErrors;

        } catch (Exception e) {
            logger.debug("Error checking for validation errors: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Internal method to get validation errors without consistency check
     * @return List of error messages
     */
    private List<String> getValidationErrorsInternal() {
        try {
            // Try specific error selectors first
            String[] errorSelectors = {
                ".validation-summary-errors",
                ".field-validation-error",
                ".error-message",
                ".validation-error",
                ".form-errors",
                ".message-error",
                ".alert-danger",
                ".alert-error"
            };

            for (String selector : errorSelectors) {
                try {
                    By errorBy = By.cssSelector(selector);
                    WebElement errorElement = waitUtils.softWaitForElementToBeVisible(errorBy, 2);
                    if (errorElement != null) {
                        String errorText = errorElement.getText();
                        if (!errorText.trim().isEmpty()) {
                            // Split by newlines and filter empty strings
                            List<String> errors = List.of(errorText.split("\n"))
                                    .stream()
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .toList();
                            if (!errors.isEmpty()) {
                                logger.debug("Found validation errors with selector '{}': {}", selector, errors);
                                return errors;
                            }
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check for multiple field validation errors using soft wait
            try {
                List<WebElement> fieldErrors = waitUtils.softWaitForElementsToBeVisible(By.cssSelector(".field-validation-error"), 2);
                if (fieldErrors != null && !fieldErrors.isEmpty()) {
                    List<String> errors = fieldErrors.stream()
                            .map(WebElement::getText)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList();
                    if (!errors.isEmpty()) {
                        logger.debug("Found field validation errors: {}", errors);
                        return errors;
                    }
                }
            } catch (Exception ignored) {
                // Field errors check failed, continue
            }

            // Fallback to original method with SelectorUtils
            try {
                By summaryErrorSelector = SelectorUtils.getAuthSelector("authentication.registration_page.validation.summary_errors");
                WebElement errorElement = waitUtils.softWaitForElementToBeVisible(summaryErrorSelector, 2);
                if (errorElement != null) {
                    String errorText = errorElement.getText();
                    if (!errorText.trim().isEmpty()) {
                        List<String> errors = List.of(errorText.split("\n"))
                                .stream()
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();
                        logger.debug("Found validation errors with SelectorUtils: {}", errors);
                        return errors;
                    }
                }
            } catch (Exception ignored) {
                // Continue to empty list
            }

        } catch (Exception e) {
            logger.error("Error getting validation errors: {}", e.getMessage());
        }
        return List.of();
    }

    /**
     * Get all validation error messages from summary (public wrapper)
     * @return List of error messages
     */
    public List<String> getValidationErrors() {
        return getValidationErrorsInternal();
    }

    /**
     * Get all validation error messages (alias for compatibility)
     * @return List of error messages
     */
    public List<String> getValidationErrorMessages() {
        List<String> errors = getValidationErrors();

        // Debug validation to ensure consistency
        boolean hasErrors = hasValidationErrors();
        boolean hasErrorMessages = !errors.isEmpty();

        if (hasErrors != hasErrorMessages) {
            logger.warn("VALIDATION INCONSISTENCY: hasValidationErrors()={}, getValidationErrorMessages().isEmpty()={}, errorMessages={}",
                       hasErrors, !hasErrorMessages, errors);

            // Additional debugging - check what selectors are actually finding
            debugValidationState();
        }

        return errors;
    }

    /**
     * Debug method to investigate validation state inconsistencies
     */
    private void debugValidationState() {
        logger.warn("=== VALIDATION DEBUG STATE ===");

        // Check all error selectors manually
        String[] errorSelectors = {
            ".validation-summary-errors",
            ".field-validation-error",
            ".error-message",
            ".validation-error",
            ".form-errors",
            ".message-error",
            ".alert-danger",
            ".alert-error"
        };

        for (String selector : errorSelectors) {
            try {
                List<org.openqa.selenium.WebElement> elements = findElements(By.cssSelector(selector));
                if (!elements.isEmpty()) {
                    for (int i = 0; i < elements.size(); i++) {
                        String text = elements.get(i).getText();
                        boolean isVisible = elements.get(i).isDisplayed();
                        logger.warn("Selector '{}' element #{}: visible={}, text='{}'", selector, i, isVisible, text);
                    }
                }
            } catch (Exception e) {
                logger.debug("Error checking selector '{}': {}", selector, e.getMessage());
            }
        }

        // Check container selectors
        String[] containerSelectors = {".result", ".message", ".content", ".registration-form", "form"};
        for (String selector : containerSelectors) {
            try {
                List<org.openqa.selenium.WebElement> elements = findElements(By.cssSelector(selector));
                if (!elements.isEmpty()) {
                    String text = elements.get(0).getText().toLowerCase();
                    if (text.contains("error") || text.contains("invalid") || text.contains("required")) {
                        logger.warn("Container '{}' has error text: '{}'", selector, text);
                    }
                }
            } catch (Exception e) {
                logger.debug("Error checking container '{}': {}", selector, e.getMessage());
            }
        }

        logger.warn("=== END VALIDATION DEBUG ===");
    }

    // Display check methods
    public boolean isGenderSelectionDisplayed() { return isElementDisplayed(By.name("Gender")); }
    public boolean isFirstNameFieldDisplayed() { return isElementDisplayed(By.name("FirstName")); }
    public boolean isLastNameFieldDisplayed() { return isElementDisplayed(By.name("LastName")); }
    public boolean isEmailFieldDisplayed() { return isElementDisplayed(By.name("Email")); }
    public boolean isPasswordFieldDisplayed() { return isElementDisplayed(By.name("Password")); }
    public boolean isConfirmPasswordFieldDisplayed() { return isElementDisplayed(By.name("ConfirmPassword")); }
    public boolean isNewsletterCheckboxDisplayed() {
        try {
            // Try multiple selectors for newsletter checkbox
            String[] newsletterSelectors = {
                "input[name='Newsletter']",
                "input[id='Newsletter']",
                "#Newsletter",
                "input[type='checkbox'][name*='newsletter']",
                "input[type='checkbox'][id*='newsletter']",
                ".newsletter input[type='checkbox']",
                ".newsletter-signup input[type='checkbox']"
            };

            for (String selector : newsletterSelectors) {
                try {
                    if (waitUtils.softWaitForElementToBeVisible(By.cssSelector(selector), 2) != null) {
                        logger.debug("Found newsletter checkbox with selector: {}", selector);
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // If no newsletter checkbox found, this might be expected on some registration forms
            logger.debug("Newsletter checkbox not found - might not be present on this registration form");
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for newsletter checkbox: {}", e.getMessage());
            return false;
        }
    }
    public boolean isRegisterButtonDisplayed() { return isElementDisplayed(By.cssSelector("input[type='submit'][value*='Register'], .register-button")); }
    public boolean isConfirmationMessageDisplayed() { return isRegistrationSuccessful(); }

    /**
     * Get field-specific validation errors
     * @return List of field validation errors
     */
    public List<String> getFieldValidationErrors() {
        try {
            // Try field-specific error selectors
            String[] fieldErrorSelectors = {
                ".field-validation-error",
                "[data-valmsg-for]",
                ".form-control + .text-danger",
                ".validation-error",
                ".error-message"
            };

            for (String selector : fieldErrorSelectors) {
                try {
                    List<WebElement> fieldErrors = findElements(By.cssSelector(selector));
                    if (!fieldErrors.isEmpty()) {
                        List<String> errors = fieldErrors.stream()
                                .map(WebElement::getText)
                                .map(String::trim)
                                .filter(text -> !text.isEmpty())
                                .toList();
                        if (!errors.isEmpty()) {
                            logger.debug("Found field validation errors with selector '{}': {}", selector, errors);
                            return errors;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to original method with SelectorUtils
            try {
                By fieldErrorSelector = SelectorUtils.getAuthSelector("authentication.registration_page.validation.validation_errors");
                List<WebElement> fieldErrors = findElements(fieldErrorSelector);
                List<String> errors = fieldErrors.stream()
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(text -> !text.isEmpty())
                        .toList();
                if (!errors.isEmpty()) {
                    logger.debug("Found field validation errors with SelectorUtils: {}", errors);
                    return errors;
                }
            } catch (Exception ignored) {
                // Continue to empty list
            }

        } catch (Exception e) {
            logger.error("Error getting field validation errors: {}", e.getMessage());
        }
        return List.of();
    }

    /**
     * Check if a specific field has a validation error
     * @param fieldName Name of the field to check
     * @return true if field has validation error
     */
    public boolean hasFieldValidationError(String fieldName) {
        List<String> fieldErrors = getFieldValidationErrors();
        return fieldErrors.stream()
                .anyMatch(error -> error.toLowerCase().contains(fieldName.toLowerCase()));
    }

    /**
     * Check if required field indicators are visible
     * @return true if required indicators are shown
     */
    public boolean areRequiredFieldsMarked() {
        try {
            By requiredSelector = SelectorUtils.getAuthSelector("authentication.registration_page.validation.required_indicators");
            return isElementDisplayed(requiredSelector);
        } catch (Exception e) {
            return false;
        }
    }

    // Form State Methods

    /**
     * Get selected gender
     * @return "Male", "Female", or null if none selected
     */
    public String getSelectedGender() {
        if (genderMaleRadio.isSelected()) {
            return "Male";
        } else if (genderFemaleRadio.isSelected()) {
            return "Female";
        }
        return null;
    }

    /**
     * Get current first name value
     * @return Current first name
     */
    public String getFirstNameValue() {
        return firstNameInput.getAttribute("value");
    }

    /**
     * Get current last name value
     * @return Current last name
     */
    public String getLastNameValue() {
        return lastNameInput.getAttribute("value");
    }

    /**
     * Get current email value
     * @return Current email
     */
    public String getEmailValue() {
        return emailInput.getAttribute("value");
    }

    /**
     * Clear all form fields
     * @return RegisterPage for method chaining
     */
    public RegisterPage clearForm() {
        firstNameInput.clear();
        lastNameInput.clear();
        emailInput.clear();
        passwordInput.clear();
        confirmPasswordInput.clear();

        // Clear gender selection if any
        if (genderMaleRadio.isSelected()) {
            click(genderFemaleRadio); // Switch to female first
            // Note: In some implementations, you might not be able to unselect radio buttons
        }

        logger.info("Cleared registration form");
        return this;
    }

    /**
     * Check if register button is enabled
     * @return true if register button can be clicked
     */
    public boolean isRegisterButtonEnabled() {
        return registerButton.isEnabled();
    }

    /**
     * Validate form fields individually
     * @return true if all required fields are filled
     */
    public boolean areRequiredFieldsFilled() {
        return !getFirstNameValue().trim().isEmpty() &&
               !getLastNameValue().trim().isEmpty() &&
               !getEmailValue().trim().isEmpty() &&
               !passwordInput.getAttribute("value").trim().isEmpty() &&
               !confirmPasswordInput.getAttribute("value").trim().isEmpty();
    }

    /**
     * Check if passwords match
     * @return true if password and confirm password match
     */
    public boolean doPasswordsMatch() {
        String password = passwordInput.getAttribute("value");
        String confirmPassword = confirmPasswordInput.getAttribute("value");
        return password.equals(confirmPassword);
    }

    // Page Validation Methods

    /**
     * Verify that register page is loaded correctly
     * @return true if page is loaded correctly
     */
    @Override
    public boolean isPageLoaded() {
        try {
            // Check for essential registration form elements
            return isElementDisplayed(By.name("FirstName")) &&
                   isElementDisplayed(By.name("LastName")) &&
                   isElementDisplayed(By.name("Email")) &&
                   isElementDisplayed(By.name("Password")) &&
                   isElementDisplayed(By.name("ConfirmPassword")) &&
                   isElementDisplayed(By.cssSelector("input[type='submit'][value='Register']"));
        } catch (Exception e) {
            logger.error("Error checking if register page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the page URL pattern for validation
     * @return Register page URL pattern
     */
    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Verify user is on registration page by checking URL
     * @return true if on registration page
     */
    public boolean isOnRegisterPage() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN);
    }

    /**
     * Get page title
     * @return Registration page title
     */
    public String getRegisterPageTitle() {
        return getCurrentTitle();
    }

    /**
     * Check if server errors are displayed
     * @return true if server errors are visible
     */
    public boolean hasServerErrors() {
        try {
            By serverErrorSelector = By.cssSelector(".server-error, .error-message, .validation-summary-errors");
            return isElementDisplayed(serverErrorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if security errors are displayed
     * @return true if security errors are visible
     */
    public boolean hasSecurityErrors() {
        try {
            By securityErrorSelector = By.cssSelector(".security-error, .error-message");
            return isElementDisplayed(securityErrorSelector) &&
                   getText(securityErrorSelector).toLowerCase().contains("security");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if email validation error is displayed
     * @return true if email validation error is visible
     */
    public boolean hasEmailValidationError() {
        try {
            // Try multiple selectors for email validation errors
            String[] emailErrorSelectors = {
                ".field-validation-error[data-valmsg-for='Email']",
                ".field-validation-error[data-valmsg-for='email']",
                ".validation-summary-errors",
                ".error-message"
            };

            for (String selector : emailErrorSelectors) {
                try {
                    By errorBy = By.cssSelector(selector);
                    if (waitUtils.softWaitForElementToBeVisible(errorBy, 2) != null) {
                        String errorText = getText(errorBy).toLowerCase();
                        if (errorText.contains("email") || errorText.contains("e-mail") ||
                            errorText.contains("address") || errorText.contains("@")) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check in general validation messages for email-related errors
            List<String> allErrors = getValidationErrorMessages();
            return allErrors.stream().anyMatch(error -> {
                String lowerError = error.toLowerCase();
                return lowerError.contains("email") || lowerError.contains("e-mail") ||
                       lowerError.contains("address") || lowerError.contains("@");
            });

        } catch (Exception e) {
            logger.debug("Error checking for email validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if password validation error is displayed
     * @return true if password validation error is visible
     */
    public boolean hasPasswordValidationError() {
        try {
            By passwordErrorSelector = By.cssSelector(".field-validation-error[data-valmsg-for='Password'], .validation-summary-errors");
            return isElementDisplayed(passwordErrorSelector) &&
                   getText(passwordErrorSelector).toLowerCase().contains("password");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if field length validation error is displayed
     * @return true if field length validation error is visible
     */
    public boolean hasFieldLengthValidationError() {
        try {
            By lengthErrorSelector = By.cssSelector(".field-validation-error, .validation-summary-errors");
            return isElementDisplayed(lengthErrorSelector) &&
                   (getText(lengthErrorSelector).toLowerCase().contains("length") ||
                    getText(lengthErrorSelector).toLowerCase().contains("characters"));
        } catch (Exception e) {
            return false;
        }
    }
}