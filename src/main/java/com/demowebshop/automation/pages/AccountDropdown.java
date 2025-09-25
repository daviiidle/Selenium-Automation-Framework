package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Account Dropdown Menu
 * Handles account dropdown navigation and options
 */
public class AccountDropdown extends BasePage {

    public AccountDropdown(WebDriver driver) {
        super(driver);
    }

    public AccountDropdown() {
        super();
    }

    /**
     * Click my account option
     * @return CustomerInfoPage
     */
    public CustomerInfoPage clickMyAccount() {
        try {
            By myAccountSelector = By.cssSelector("a[href*='/customer/info'], .account-link");
            click(myAccountSelector);
            logger.info("Clicked my account from dropdown");
            return new CustomerInfoPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click my account: {}", e.getMessage());
            return new CustomerInfoPage(driver);
        }
    }

    /**
     * Click orders option
     * @return OrderHistoryPage
     */
    public OrderHistoryPage clickOrders() {
        try {
            By ordersSelector = By.cssSelector("a[href*='/customer/orders'], .orders-link");
            click(ordersSelector);
            logger.info("Clicked orders from dropdown");
            return new OrderHistoryPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click orders: {}", e.getMessage());
            return new OrderHistoryPage(driver);
        }
    }

    /**
     * Click addresses option
     * @return AddressBookPage
     */
    public AddressBookPage clickAddresses() {
        try {
            By addressesSelector = By.cssSelector("a[href*='/customer/addresses'], .addresses-link");
            click(addressesSelector);
            logger.info("Clicked addresses from dropdown");
            return new AddressBookPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click addresses: {}", e.getMessage());
            return new AddressBookPage(driver);
        }
    }

    /**
     * Click logout option
     * @return HomePage
     */
    public HomePage clickLogout() {
        try {
            By logoutSelector = By.cssSelector("a[href*='/logout'], .logout-link");
            click(logoutSelector);
            logger.info("Clicked logout from dropdown");
            return new HomePage(driver);
        } catch (Exception e) {
            logger.warn("Could not click logout: {}", e.getMessage());
            return new HomePage(driver);
        }
    }

    /**
     * Check if dropdown is displayed
     * @return true if dropdown is visible
     */
    public boolean isDropdownDisplayed() {
        try {
            By dropdownSelector = By.cssSelector(".account-dropdown, .dropdown-menu");
            return isElementDisplayed(dropdownSelector);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isPageLoaded() {
        return isDropdownDisplayed();
    }

    @Override
    public String getPageUrlPattern() {
        return "";  // Dropdown doesn't have its own page
    }
}