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
        click(registerButton);
        logger.info("Clicked register button");

        // Wait for processing
        try {
            Thread.sleep(3000);
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
            By summaryErrorSelector = SelectorUtils.getAuthSelector("authentication.registration_page.validation.summary_errors");
            return isElementDisplayed(summaryErrorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all validation error messages from summary
     * @return List of error messages
     */
    public List<String> getValidationErrors() {
        try {
            By summaryErrorSelector = SelectorUtils.getAuthSelector("authentication.registration_page.validation.summary_errors");
            if (isElementDisplayed(summaryErrorSelector)) {
                WebElement errorContainer = findElement(summaryErrorSelector);
                return List.of(errorContainer.getText().split("\n"));
            }
        } catch (Exception e) {
            logger.error("Error getting validation errors: {}", e.getMessage());
        }
        return List.of();
    }

    /**
     * Get all validation error messages (alias for compatibility)
     * @return List of error messages
     */
    public List<String> getValidationErrorMessages() {
        return getValidationErrors();
    }

    // Display check methods
    public boolean isGenderSelectionDisplayed() { return isElementDisplayed(By.name("Gender")); }
    public boolean isFirstNameFieldDisplayed() { return isElementDisplayed(By.name("FirstName")); }
    public boolean isLastNameFieldDisplayed() { return isElementDisplayed(By.name("LastName")); }
    public boolean isEmailFieldDisplayed() { return isElementDisplayed(By.name("Email")); }
    public boolean isPasswordFieldDisplayed() { return isElementDisplayed(By.name("Password")); }
    public boolean isConfirmPasswordFieldDisplayed() { return isElementDisplayed(By.name("ConfirmPassword")); }
    public boolean isNewsletterCheckboxDisplayed() { return isElementDisplayed(By.name("Newsletter")); }
    public boolean isRegisterButtonDisplayed() { return isElementDisplayed(By.cssSelector("input[type='submit'][value*='Register'], .register-button")); }
    public boolean isConfirmationMessageDisplayed() { return isRegistrationSuccessful(); }

    /**
     * Get field-specific validation errors
     * @return List of field validation errors
     */
    public List<String> getFieldValidationErrors() {
        try {
            By fieldErrorSelector = SelectorUtils.getAuthSelector("authentication.registration_page.validation.validation_errors");
            List<WebElement> fieldErrors = findElements(fieldErrorSelector);
            return fieldErrors.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting field validation errors: {}", e.getMessage());
            return List.of();
        }
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
            By emailErrorSelector = By.cssSelector(".field-validation-error[data-valmsg-for='Email'], .validation-summary-errors");
            return isElementDisplayed(emailErrorSelector);
        } catch (Exception e) {
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