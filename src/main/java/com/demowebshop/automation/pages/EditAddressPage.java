package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Edit Address Page
 * Handles editing existing addresses in customer address book
 */
public class EditAddressPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/customer/addressedit";

    public EditAddressPage(WebDriver driver) {
        super(driver);
    }

    public EditAddressPage() {
        super();
    }

    /**
     * Update address form
     * @param address Updated address data
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateAddress(models.Address address) {
        try {
            if (address.getFirstName() != null) {
                clear(By.cssSelector("#Address_FirstName"));
                type(By.cssSelector("#Address_FirstName"), address.getFirstName());
            }
            if (address.getLastName() != null) {
                clear(By.cssSelector("#Address_LastName"));
                type(By.cssSelector("#Address_LastName"), address.getLastName());
            }
            if (address.getEmail() != null) {
                clear(By.cssSelector("#Address_Email"));
                type(By.cssSelector("#Address_Email"), address.getEmail());
            }
            if (address.getCompany() != null) {
                clear(By.cssSelector("#Address_Company"));
                type(By.cssSelector("#Address_Company"), address.getCompany());
            }
            if (address.getAddress1() != null) {
                clear(By.cssSelector("#Address_Address1"));
                type(By.cssSelector("#Address_Address1"), address.getAddress1());
            }
            if (address.getCity() != null) {
                clear(By.cssSelector("#Address_City"));
                type(By.cssSelector("#Address_City"), address.getCity());
            }
            if (address.getPostalCode() != null) {
                clear(By.cssSelector("#Address_ZipPostalCode"));
                type(By.cssSelector("#Address_ZipPostalCode"), address.getPostalCode());
            }
            if (address.getPhone() != null) {
                clear(By.cssSelector("#Address_PhoneNumber"));
                type(By.cssSelector("#Address_PhoneNumber"), address.getPhone());
            }

            logger.info("Updated address form");
        } catch (Exception e) {
            logger.warn("Could not update address form: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Save the address changes
     * @return AddressBookPage
     */
    public AddressBookPage saveChanges() {
        try {
            By saveSelector = By.cssSelector("input[type='submit'], .save-button");
            click(saveSelector);
            waitForPageToLoad();
            logger.info("Saved address changes");
            return new AddressBookPage(driver);
        } catch (Exception e) {
            logger.warn("Could not save address changes: {}", e.getMessage());
            return new AddressBookPage(driver);
        }
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN) ||
               isElementDisplayed(By.cssSelector("#Address_FirstName"));
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Update first name field
     * @param firstName First name
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateFirstName(String firstName) {
        clear(By.cssSelector("#Address_FirstName"));
        type(By.cssSelector("#Address_FirstName"), firstName);
        return this;
    }

    /**
     * Update last name field
     * @param lastName Last name
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateLastName(String lastName) {
        clear(By.cssSelector("#Address_LastName"));
        type(By.cssSelector("#Address_LastName"), lastName);
        return this;
    }

    /**
     * Update email field
     * @param email Email address
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateEmail(String email) {
        clear(By.cssSelector("#Address_Email"));
        type(By.cssSelector("#Address_Email"), email);
        return this;
    }

    /**
     * Update company field
     * @param company Company name
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateCompany(String company) {
        clear(By.cssSelector("#Address_Company"));
        type(By.cssSelector("#Address_Company"), company);
        return this;
    }

    /**
     * Update address line 1
     * @param address Address line 1
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateAddress1(String address) {
        clear(By.cssSelector("#Address_Address1"));
        type(By.cssSelector("#Address_Address1"), address);
        return this;
    }

    /**
     * Update city field
     * @param city City name
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updateCity(String city) {
        clear(By.cssSelector("#Address_City"));
        type(By.cssSelector("#Address_City"), city);
        return this;
    }

    /**
     * Update postal code field
     * @param postalCode Postal/ZIP code
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updatePostalCode(String postalCode) {
        clear(By.cssSelector("#Address_ZipPostalCode"));
        type(By.cssSelector("#Address_ZipPostalCode"), postalCode);
        return this;
    }

    /**
     * Update phone number field
     * @param phoneNumber Phone number
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage updatePhoneNumber(String phoneNumber) {
        clear(By.cssSelector("#Address_PhoneNumber"));
        type(By.cssSelector("#Address_PhoneNumber"), phoneNumber);
        return this;
    }

    /**
     * Get current field value
     * @param fieldName Field name (FirstName, LastName, etc.)
     * @return Current field value
     */
    public String getCurrentFieldValue(String fieldName) {
        try {
            By fieldSelector = By.cssSelector("#Address_" + fieldName);
            return getAttribute(fieldSelector, "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Clear specific field
     * @param fieldName Field name to clear
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage clearField(String fieldName) {
        try {
            By fieldSelector = By.cssSelector("#Address_" + fieldName);
            clear(fieldSelector);
            logger.info("Cleared field: {}", fieldName);
        } catch (Exception e) {
            logger.warn("Could not clear field {}: {}", fieldName, e.getMessage());
        }
        return this;
    }

    /**
     * Clear all form fields
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage clearForm() {
        try {
            clear(By.cssSelector("#Address_FirstName"));
            clear(By.cssSelector("#Address_LastName"));
            clear(By.cssSelector("#Address_Email"));
            clear(By.cssSelector("#Address_Company"));
            clear(By.cssSelector("#Address_Address1"));
            clear(By.cssSelector("#Address_City"));
            clear(By.cssSelector("#Address_ZipPostalCode"));
            clear(By.cssSelector("#Address_PhoneNumber"));
            logger.info("Cleared all form fields");
        } catch (Exception e) {
            logger.warn("Could not clear form: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if form has validation errors
     * @return true if validation errors are present
     */
    public boolean hasValidationErrors() {
        try {
            By errorSelector = By.cssSelector(".field-validation-error, .validation-summary-errors");
            return isElementDisplayed(errorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cancel editing and go back to address book
     * @return AddressBookPage
     */
    public AddressBookPage cancel() {
        try {
            By cancelSelector = By.cssSelector(".cancel-button, a[href*='addresses']");
            click(cancelSelector);
            logger.info("Cancelled address editing");
            return new AddressBookPage(driver);
        } catch (Exception e) {
            logger.warn("Could not cancel: {}", e.getMessage());
            return new AddressBookPage(driver);
        }
    }

    /**
     * Check if save button is enabled
     * @return true if save button can be clicked
     */
    public boolean isSaveButtonEnabled() {
        try {
            By saveSelector = By.cssSelector("input[type='submit'], .save-button");
            return isElementEnabled(saveSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if field has error
     * @param fieldName Field name to check
     * @return true if field has validation error
     */
    public boolean hasFieldError(String fieldName) {
        try {
            By errorSelector = By.cssSelector("#Address_" + fieldName + " + .field-validation-error");
            return isElementDisplayed(errorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reset form to original values
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage resetForm() {
        try {
            By resetSelector = By.cssSelector("input[type='reset'], .reset-button");
            if (isElementDisplayed(resetSelector)) {
                click(resetSelector);
                logger.info("Reset form to original values");
            }
        } catch (Exception e) {
            logger.warn("Could not reset form: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if form has been modified
     * @return true if form has unsaved changes
     */
    public boolean hasUnsavedChanges() {
        // This is a basic implementation - in reality, you'd compare current values
        // with original values stored when the page was loaded
        try {
            // Check if any field has content (basic check)
            return !getCurrentFieldValue("FirstName").trim().isEmpty() ||
                   !getCurrentFieldValue("LastName").trim().isEmpty() ||
                   !getCurrentFieldValue("Address1").trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get city value
     * @return Current city value
     */
    public String getCity() {
        return getCurrentFieldValue("City");
    }

    /**
     * Click save button (alias for saveChanges that returns EditAddressPage)
     * @return EditAddressPage for method chaining
     */
    public EditAddressPage clickSaveButton() {
        try {
            By saveSelector = By.cssSelector("input[type='submit'], .save-button");
            click(saveSelector);
            logger.info("Clicked save button");
        } catch (Exception e) {
            logger.warn("Could not click save button: {}", e.getMessage());
        }
        return this;
    }
}