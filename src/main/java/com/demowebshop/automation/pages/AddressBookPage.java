package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object Model for Address Book Page
 * Handles customer address management
 */
public class AddressBookPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/customer/addresses";

    public AddressBookPage(WebDriver driver) {
        super(driver);
    }

    public AddressBookPage() {
        super();
    }

    /**
     * Navigate directly to address book page
     * @return AddressBookPage instance for method chaining
     */
    public AddressBookPage navigateToAddressBook() {
        navigateTo("https://demowebshop.tricentis.com/customer/addresses");
        waitForPageToLoad();
        return this;
    }

    /**
     * Click add new address button
     * @return AddAddressPage
     */
    public AddAddressPage clickAddNewAddress() {
        try {
            By addAddressSelector = By.cssSelector(".add-address-button, input[value*='Add'], .button-1");
            click(addAddressSelector);
            logger.info("Clicked add new address");
            return new AddAddressPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click add new address: {}", e.getMessage());
            return new AddAddressPage(driver);
        }
    }

    /**
     * Check if any addresses exist
     * @return true if addresses are present
     */
    public boolean hasAddresses() {
        try {
            // Use the new waitForAddressElements method to safely find addresses
            List<org.openqa.selenium.WebElement> addressElements = waitUtils.waitForAddressElements();
            if (!addressElements.isEmpty()) {
                return true;
            }

            // Check if there's a "no data" message instead
            By noDataSelector = By.cssSelector(".no-data, .no-addresses, .empty-list, .no-items");
            if (waitUtils.softWaitForElementToBeVisible(noDataSelector, 2) != null) {
                return false; // No addresses message is displayed
            }

            // As final fallback, look for any container that might have addresses
            String[] containerSelectors = {".address-list", ".addresses", ".customer-addresses"};
            for (String selector : containerSelectors) {
                if (waitUtils.softWaitForElementToBeVisible(By.cssSelector(selector), 2) != null) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.debug("Error checking for addresses: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get number of saved addresses
     * @return Count of addresses
     */
    public int getAddressCount() {
        try {
            // Use the new waitForAddressElements method to safely count addresses
            List<org.openqa.selenium.WebElement> addressElements = waitUtils.waitForAddressElements();
            return addressElements.size();
        } catch (Exception e) {
            logger.debug("Error getting address count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Click edit button for first address
     * @return EditAddressPage
     */
    public EditAddressPage clickEditFirstAddress() {
        try {
            By editSelector = By.cssSelector(".edit-address-button, input[value*='Edit']");
            ElementsCollection editButtons = $$(editSelector);
            if (!editButtons.isEmpty()) {
                editButtons.get(0).click();
                logger.info("Clicked edit first address");
                return new EditAddressPage(driver);
            }
        } catch (Exception e) {
            logger.warn("Could not click edit address: {}", e.getMessage());
        }
        return new EditAddressPage(driver);
    }

    /**
     * Click delete button for first address
     * @return AddressBookPage for method chaining
     */
    public AddressBookPage clickDeleteFirstAddress() {
        try {
            By deleteSelector = By.cssSelector(".delete-address-button, input[value*='Delete']");
            ElementsCollection deleteButtons = $$(deleteSelector);
            if (!deleteButtons.isEmpty()) {
                deleteButtons.get(0).click();
                waitForPageToLoad();
                logger.info("Clicked delete first address");
            }
        } catch (Exception e) {
            logger.warn("Could not click delete address: {}", e.getMessage());
        }
        return this;
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN) ||
               isElementDisplayed(By.cssSelector(".address-list, .no-data"));
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Click add address button (alias for clickAddNewAddress)
     * @return AddAddressPage
     */
    public AddAddressPage clickAddAddress() {
        return clickAddNewAddress();
    }

    /**
     * Click edit address button by index
     * @param index Index of the address to edit (0-based)
     * @return EditAddressPage
     */
    public EditAddressPage clickEditAddress(int index) {
        try {
            By editSelector = By.cssSelector(".edit-address-button, input[value*='Edit']");
            ElementsCollection editButtons = $$(editSelector);
            if (index >= 0 && index < editButtons.size()) {
                editButtons.get(index).click();
                logger.info("Clicked edit address at index: {}", index);
                return new EditAddressPage(driver);
            }
        } catch (Exception e) {
            logger.warn("Could not click edit address at index {}: {}", index, e.getMessage());
        }
        return new EditAddressPage(driver);
    }

    /**
     * Click delete address button by index
     * @param index Index of the address to delete (0-based)
     * @return AddressBookPage for method chaining
     */
    public AddressBookPage clickDeleteAddress(int index) {
        try {
            By deleteSelector = By.cssSelector(".delete-address-button, input[value*='Delete']");
            ElementsCollection deleteButtons = $$(deleteSelector);
            if (index >= 0 && index < deleteButtons.size()) {
                deleteButtons.get(index).click();
                logger.info("Clicked delete address at index: {}", index);
            }
        } catch (Exception e) {
            logger.warn("Could not click delete address at index {}: {}", index, e.getMessage());
        }
        return this;
    }

    /**
     * Confirm deletion in dialog/popup
     * @return AddressBookPage for method chaining
     */
    public AddressBookPage confirmDeletion() {
        try {
            By confirmSelector = By.cssSelector(".confirm-delete, .button-confirm, input[value*='Delete'], button[type='submit']");
            if (isElementDisplayed(confirmSelector)) {
                click(confirmSelector);
                waitForPageToLoad();
                logger.info("Confirmed address deletion");
            }
        } catch (Exception e) {
            logger.warn("Could not confirm deletion: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Cancel deletion dialog
     * @return AddressBookPage for method chaining
     */
    public AddressBookPage cancelDeletion() {
        try {
            By cancelSelector = By.cssSelector(".cancel-delete, .button-cancel, input[value*='Cancel']");
            if (isElementDisplayed(cancelSelector)) {
                click(cancelSelector);
                logger.info("Cancelled address deletion");
            }
        } catch (Exception e) {
            logger.warn("Could not cancel deletion: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Get address list as text
     * @return List of address strings
     */
    public List<String> getAddressList() {
        try {
            // Use the new waitForAddressElements method to safely get addresses
            List<org.openqa.selenium.WebElement> addressElements = waitUtils.waitForAddressElements();
            return addressElements.stream()
                    .map(WebElement::getText)
                    .toList();
        } catch (Exception e) {
            logger.debug("Error getting address list: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get first address text
     * @return First address text or empty string
     */
    public String getFirstAddress() {
        try {
            // Use the new waitForAddressElements method to safely get first address
            List<org.openqa.selenium.WebElement> addressElements = waitUtils.waitForAddressElements();
            if (!addressElements.isEmpty()) {
                return addressElements.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first address: {}", e.getMessage());
        }
        return "";
    }

    /**
     * Check if address book is empty
     * @return true if no addresses are saved
     */
    public boolean isEmpty() {
        try {
            By noAddressSelector = By.cssSelector(".no-data, .no-addresses, .empty-list");
            return isElementDisplayed(noAddressSelector) || !hasAddresses();
        } catch (Exception e) {
            return !hasAddresses();
        }
    }

    /**
     * Check if add address button is displayed
     * @return true if add address button is visible
     */
    public boolean isAddAddressButtonDisplayed() {
        try {
            By addAddressSelector = By.cssSelector(".add-address-button, input[value*='Add'], .button-1");
            return isElementDisplayed(addAddressSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if edit buttons are displayed
     * @return true if at least one edit button is visible
     */
    public boolean areEditButtonsDisplayed() {
        try {
            By editSelector = By.cssSelector(".edit-address-button, input[value*='Edit']");
            return !findElements(editSelector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if delete buttons are displayed
     * @return true if at least one delete button is visible
     */
    public boolean areDeleteButtonsDisplayed() {
        try {
            By deleteSelector = By.cssSelector(".delete-address-button, input[value*='Delete']");
            return !findElements(deleteSelector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete the last address in the list
     * @return AddressBookPage for method chaining
     */
    public AddressBookPage deleteLastAddress() {
        try {
            By deleteSelector = By.cssSelector(".delete-address-button, input[value*='Delete']");
            ElementsCollection deleteButtons = $$(deleteSelector);
            if (!deleteButtons.isEmpty()) {
                int lastIndex = deleteButtons.size() - 1;
                deleteButtons.get(lastIndex).click();
                logger.info("Clicked delete last address");
            }
        } catch (Exception e) {
            logger.warn("Could not delete last address: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if delete confirmation dialog is displayed
     * @return true if delete confirmation is visible
     */
    public boolean isDeleteConfirmationDisplayed() {
        try {
            By confirmationSelector = By.cssSelector(".delete-confirmation, .confirm-dialog, .modal-dialog, .confirmation-popup");
            return isElementDisplayed(confirmationSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if add new address button is displayed (alias for isAddAddressButtonDisplayed)
     * @return true if add new address button is visible
     */
    public boolean isAddNewAddressButtonDisplayed() {
        return isAddAddressButtonDisplayed();
    }

    /**
     * Check if address added success message is displayed
     * @return true if success message is visible
     */
    public boolean isAddressAddedSuccessMessageDisplayed() {
        try {
            By successSelector = By.cssSelector(".result, .success-message, .notification-success");
            return isElementDisplayed(successSelector) &&
                   getText(successSelector).toLowerCase().contains("address");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if address is in list
     * @param firstName First name to look for
     * @param lastName Last name to look for
     * @return true if address with these names is found
     */
    public boolean isAddressInList(String firstName, String lastName) {
        try {
            List<String> addresses = getAddressList();
            return addresses.stream().anyMatch(address ->
                address.contains(firstName) && address.contains(lastName));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if edit buttons are displayed
     * @return true if edit buttons are present
     */
    public boolean hasEditButtons() {
        return areEditButtonsDisplayed();
    }

    /**
     * Check if address updated success message is displayed
     * @return true if update success message is visible
     */
    public boolean isAddressUpdatedSuccessMessageDisplayed() {
        try {
            By successSelector = By.cssSelector(".result, .success-message, .notification-success");
            return isElementDisplayed(successSelector) &&
                   (getText(successSelector).toLowerCase().contains("updated") ||
                    getText(successSelector).toLowerCase().contains("modified"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if set default buttons are displayed
     * @return true if set default buttons are present
     */
    public boolean hasSetDefaultButtons() {
        try {
            By setDefaultSelector = By.cssSelector(".set-default-button, input[value*='Set as default'], .default-address-button");
            return !findElements(setDefaultSelector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Set first address as default
     * @return AddressBookPage for method chaining
     */
    public AddressBookPage setFirstAddressAsDefault() {
        try {
            By setDefaultSelector = By.cssSelector(".set-default-button, input[value*='Set as default'], .default-address-button");
            ElementsCollection defaultButtons = $$(setDefaultSelector);
            if (!defaultButtons.isEmpty()) {
                defaultButtons.get(0).click();
                logger.info("Set first address as default");
            }
        } catch (Exception e) {
            logger.warn("Could not set first address as default: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if default address set success message is displayed
     * @return true if default address success message is visible
     */
    public boolean isDefaultAddressSetSuccessMessageDisplayed() {
        try {
            By successSelector = By.cssSelector(".result, .success-message, .notification-success");
            return isElementDisplayed(successSelector) &&
                   getText(successSelector).toLowerCase().contains("default");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if first address is marked as default
     * @return true if first address shows as default
     */
    public boolean isFirstAddressMarkedAsDefault() {
        try {
            By defaultMarkerSelector = By.cssSelector(".address-item:first-child .default-address, .address:first-child .default-indicator");
            return isElementDisplayed(defaultMarkerSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if delete buttons are displayed (alias for areDeleteButtonsDisplayed)
     * @return true if delete buttons are present
     */
    public boolean hasDeleteButtons() {
        return areDeleteButtonsDisplayed();
    }
}