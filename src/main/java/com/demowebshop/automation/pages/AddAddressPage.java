package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Add Address Page
 * Handles adding new addresses to customer address book
 */
public class AddAddressPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/customer/addressadd";

    public AddAddressPage(WebDriver driver) {
        super(driver);
    }

    public AddAddressPage() {
        super();
    }

    /**
     * Fill address form
     * @param address Address data
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillAddress(models.Address address) {
        try {
            if (address.getFirstName() != null) {
                type(By.cssSelector("#Address_FirstName"), address.getFirstName());
            }
            if (address.getLastName() != null) {
                type(By.cssSelector("#Address_LastName"), address.getLastName());
            }
            if (address.getEmail() != null) {
                type(By.cssSelector("#Address_Email"), address.getEmail());
            }
            if (address.getCompany() != null) {
                type(By.cssSelector("#Address_Company"), address.getCompany());
            }
            if (address.getAddress1() != null) {
                type(By.cssSelector("#Address_Address1"), address.getAddress1());
            }
            if (address.getCity() != null) {
                type(By.cssSelector("#Address_City"), address.getCity());
            }
            if (address.getPostalCode() != null) {
                type(By.cssSelector("#Address_ZipPostalCode"), address.getPostalCode());
            }
            if (address.getPhone() != null) {
                type(By.cssSelector("#Address_PhoneNumber"), address.getPhone());
            }

            logger.info("Filled address form");
        } catch (Exception e) {
            logger.warn("Could not fill address form: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Save the address
     * @return AddressBookPage
     */
    public AddressBookPage saveAddress() {
        try {
            By saveSelector = By.cssSelector("input[type='submit'], .save-button");
            click(saveSelector);
            waitForPageToLoad();
            logger.info("Saved address");
            return new AddressBookPage(driver);
        } catch (Exception e) {
            logger.warn("Could not save address: {}", e.getMessage());
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
     * Fill first name field
     * @param firstName First name
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillFirstName(String firstName) {
        type(By.cssSelector("#Address_FirstName"), firstName);
        return this;
    }

    /**
     * Fill last name field
     * @param lastName Last name
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillLastName(String lastName) {
        type(By.cssSelector("#Address_LastName"), lastName);
        return this;
    }

    /**
     * Fill email field
     * @param email Email address
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillEmail(String email) {
        type(By.cssSelector("#Address_Email"), email);
        return this;
    }

    /**
     * Fill company field
     * @param company Company name
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillCompany(String company) {
        type(By.cssSelector("#Address_Company"), company);
        return this;
    }

    /**
     * Fill address line 1
     * @param address Address line 1
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillAddress1(String address) {
        type(By.cssSelector("#Address_Address1"), address);
        return this;
    }

    /**
     * Fill city field
     * @param city City name
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillCity(String city) {
        type(By.cssSelector("#Address_City"), city);
        return this;
    }

    /**
     * Fill postal code field
     * @param postalCode Postal/ZIP code
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillPostalCode(String postalCode) {
        type(By.cssSelector("#Address_ZipPostalCode"), postalCode);
        return this;
    }

    /**
     * Fill phone number field
     * @param phoneNumber Phone number
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillPhoneNumber(String phoneNumber) {
        type(By.cssSelector("#Address_PhoneNumber"), phoneNumber);
        return this;
    }

    /**
     * Clear all form fields
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage clearForm() {
        try {
            clear(By.cssSelector("#Address_FirstName"));
            clear(By.cssSelector("#Address_LastName"));
            clear(By.cssSelector("#Address_Email"));
            clear(By.cssSelector("#Address_Company"));
            clear(By.cssSelector("#Address_Address1"));
            clear(By.cssSelector("#Address_City"));
            clear(By.cssSelector("#Address_ZipPostalCode"));
            clear(By.cssSelector("#Address_PhoneNumber"));
            logger.info("Cleared address form");
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
     * Cancel address creation and go back to address book
     * @return AddressBookPage
     */
    public AddressBookPage cancel() {
        try {
            By cancelSelector = By.cssSelector(".cancel-button, a[href*='addresses']");
            click(cancelSelector);
            logger.info("Cancelled address creation");
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
     * Get field value
     * @param fieldName Field name (FirstName, LastName, etc.)
     * @return Field value
     */
    public String getFieldValue(String fieldName) {
        try {
            By fieldSelector = By.cssSelector("#Address_" + fieldName);
            return getAttribute(fieldSelector, "value");
        } catch (Exception e) {
            return "";
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
     * Fill address form (alias for fillAddress)
     * @param address Address data
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage fillAddressForm(models.Address address) {
        return fillAddress(address);
    }

    /**
     * Click save button (alias for saveAddress that returns AddAddressPage)
     * @return AddAddressPage for method chaining
     */
    public AddAddressPage clickSaveButton() {
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