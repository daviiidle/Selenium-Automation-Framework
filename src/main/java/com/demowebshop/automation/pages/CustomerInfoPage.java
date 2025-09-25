package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Customer Information Page
 * Handles customer account information and profile management
 */
public class CustomerInfoPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/customer/info";

    public CustomerInfoPage(WebDriver driver) {
        super(driver);
    }

    public CustomerInfoPage() {
        super();
    }

    /**
     * Navigate directly to customer info page
     * @return CustomerInfoPage instance for method chaining
     */
    public CustomerInfoPage navigateToCustomerInfo() {
        navigateTo("https://demowebshop.tricentis.com/customer/info");
        waitForPageToLoad();
        return this;
    }

    /**
     * Get customer first name
     * @return Customer first name
     */
    public String getFirstName() {
        try {
            By firstNameSelector = By.cssSelector("#FirstName");
            return getAttribute(firstNameSelector, "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get customer last name
     * @return Customer last name
     */
    public String getLastName() {
        try {
            By lastNameSelector = By.cssSelector("#LastName");
            return getAttribute(lastNameSelector, "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get customer email
     * @return Customer email address
     */
    public String getEmail() {
        try {
            By emailSelector = By.cssSelector("#Email");
            return getAttribute(emailSelector, "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Update customer information
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @return CustomerInfoPage for method chaining
     */
    public CustomerInfoPage updateCustomerInfo(String firstName, String lastName, String email) {
        try {
            if (firstName != null && !firstName.isEmpty()) {
                type(By.cssSelector("#FirstName"), firstName);
            }
            if (lastName != null && !lastName.isEmpty()) {
                type(By.cssSelector("#LastName"), lastName);
            }
            if (email != null && !email.isEmpty()) {
                type(By.cssSelector("#Email"), email);
            }
            logger.info("Updated customer information");
        } catch (Exception e) {
            logger.warn("Could not update customer info: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Save customer information changes
     * @return CustomerInfoPage for method chaining
     */
    public CustomerInfoPage saveChanges() {
        try {
            By saveSelector = By.cssSelector("input[type='submit'], .save-button");
            click(saveSelector);
            waitForPageToLoad();
            logger.info("Saved customer information changes");
        } catch (Exception e) {
            logger.warn("Could not save changes: {}", e.getMessage());
        }
        return this;
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN) ||
               isElementDisplayed(By.cssSelector("#FirstName"));
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Get the selected gender value
     * @return Selected gender (Male/Female)
     */
    public String getGender() {
        try {
            By maleRadio = By.cssSelector("#gender-male");
            By femaleRadio = By.cssSelector("#gender-female");

            if (isElementSelected(maleRadio)) {
                return "Male";
            } else if (isElementSelected(femaleRadio)) {
                return "Female";
            }
            return "";
        } catch (Exception e) {
            logger.warn("Could not get gender: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Update first name field
     * @param firstName New first name
     * @return CustomerInfoPage for method chaining
     */
    public CustomerInfoPage updateFirstName(String firstName) {
        try {
            By firstNameField = By.cssSelector("#FirstName");
            clear(firstNameField);
            type(firstNameField, firstName);
            logger.info("Updated first name to: {}", firstName);
        } catch (Exception e) {
            logger.error("Failed to update first name: {}", e.getMessage());
            throw new RuntimeException("Could not update first name", e);
        }
        return this;
    }

    /**
     * Update last name field
     * @param lastName New last name
     * @return CustomerInfoPage for method chaining
     */
    public CustomerInfoPage updateLastName(String lastName) {
        try {
            By lastNameField = By.cssSelector("#LastName");
            clear(lastNameField);
            type(lastNameField, lastName);
            logger.info("Updated last name to: {}", lastName);
        } catch (Exception e) {
            logger.error("Failed to update last name: {}", e.getMessage());
            throw new RuntimeException("Could not update last name", e);
        }
        return this;
    }

    /**
     * Click the save button to save customer information
     * @return CustomerInfoPage for method chaining
     */
    public CustomerInfoPage clickSaveButton() {
        try {
            By saveButton = By.cssSelector("input[value='Save'], .button-save, input[type='submit']");
            click(saveButton);
            logger.info("Clicked save button");
        } catch (Exception e) {
            logger.error("Failed to click save button: {}", e.getMessage());
            throw new RuntimeException("Could not click save button", e);
        }
        return this;
    }

    /**
     * Check if update success message is displayed
     * @return true if success message is visible
     */
    public boolean isUpdateSuccessMessageDisplayed() {
        try {
            By successMessage = By.cssSelector(".result, .success-message, .notification-success");
            return isElementDisplayed(successMessage);
        } catch (Exception e) {
            return false;
        }
    }
}