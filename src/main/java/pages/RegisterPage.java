package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class RegisterPage extends BasePage {

    // Form Fields
    @FindBy(id = "gender-male")
    private WebElement genderMaleRadio;

    @FindBy(id = "gender-female")
    private WebElement genderFemaleRadio;

    @FindBy(id = "FirstName")
    private WebElement firstNameField;

    @FindBy(id = "LastName")
    private WebElement lastNameField;

    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(id = "Password")
    private WebElement passwordField;

    @FindBy(id = "ConfirmPassword")
    private WebElement confirmPasswordField;

    @FindBy(id = "Newsletter")
    private WebElement newsletterCheckbox;

    @FindBy(id = "register-button")
    private WebElement registerButton;

    // Validation Elements
    @FindBy(css = ".field-validation-error")
    private WebElement fieldValidationError;

    @FindBy(css = ".validation-summary-errors")
    private WebElement validationSummary;

    @FindBy(css = ".result")
    private WebElement registrationResult;

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(firstNameField);
            waitForElementToBeVisible(emailField);
            waitForElementToBeVisible(registerButton);
            return true;
        } catch (Exception e) {
            logger.error("RegisterPage is not loaded properly", e);
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return config.getBaseUrl() + "/register";
    }

    public RegisterPage selectGender(String gender) {
        if ("M".equalsIgnoreCase(gender) || "Male".equalsIgnoreCase(gender)) {
            safeClick(genderMaleRadio);
            logger.info("Selected Male gender");
        } else if ("F".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender)) {
            safeClick(genderFemaleRadio);
            logger.info("Selected Female gender");
        }
        return this;
    }

    public RegisterPage enterFirstName(String firstName) {
        safeType(firstNameField, firstName);
        logger.info("Entered first name: {}", firstName);
        return this;
    }

    public RegisterPage enterLastName(String lastName) {
        safeType(lastNameField, lastName);
        logger.info("Entered last name: {}", lastName);
        return this;
    }

    public RegisterPage enterEmail(String email) {
        safeType(emailField, email);
        logger.info("Entered email: {}", email);
        return this;
    }

    public RegisterPage enterPassword(String password) {
        safeType(passwordField, password);
        logger.info("Entered password");
        return this;
    }

    public RegisterPage confirmPassword(String password) {
        safeType(confirmPasswordField, password);
        logger.info("Confirmed password");
        return this;
    }

    public RegisterPage subscribeToNewsletter() {
        if (!newsletterCheckbox.isSelected()) {
            safeClick(newsletterCheckbox);
            logger.info("Subscribed to newsletter");
        }
        return this;
    }

    public HomePage clickRegisterButton() {
        safeClick(registerButton);
        logger.info("Clicked register button");
        return new HomePage(driver);
    }

    public boolean hasValidationErrors() {
        return isElementDisplayed(validationSummary) || isElementDisplayed(fieldValidationError);
    }
}