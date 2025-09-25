package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Order Complete/Success Page
 * Handles order confirmation and completion flow
 */
public class OrderCompletePage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/checkout/completed";

    public OrderCompletePage(WebDriver driver) {
        super(driver);
    }

    public OrderCompletePage() {
        super();
    }

    /**
     * Get order success message
     * @return Success message text
     */
    public String getSuccessMessage() {
        try {
            By successSelector = SelectorUtils.getCartSelector("cart_and_checkout.order_complete.success_message");
            return getText(successSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get order number
     * @return Order number
     */
    public String getOrderNumber() {
        try {
            By orderNumberSelector = SelectorUtils.getCartSelector("cart_and_checkout.order_complete.order_number");
            String orderText = getText(orderNumberSelector);
            // Extract order number from text like "Order Number: 123456"
            return orderText.replaceAll("[^0-9]", "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Click continue button to go to homepage
     * @return HomePage
     */
    public HomePage clickContinue() {
        By continueSelector = SelectorUtils.getCartSelector("cart_and_checkout.order_complete.continue_button");
        click(continueSelector);
        logger.info("Clicked continue button from order complete page");
        return new HomePage(driver);
    }

    /**
     * Check if order was completed successfully
     * @return true if success message is displayed
     */
    public boolean isOrderSuccessful() {
        return !getSuccessMessage().isEmpty() && !getOrderNumber().isEmpty();
    }

    /**
     * Check if order confirmation is displayed
     * @return true if order confirmation is visible
     */
    public boolean isOrderConfirmationDisplayed() {
        try {
            By confirmationSelector = By.cssSelector(".order-completed, .order-confirmation");
            return isElementDisplayed(confirmationSelector) || isOrderSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if order details are displayed
     * @return true if order details are visible
     */
    public boolean isOrderDetailsDisplayed() {
        try {
            By detailsSelector = By.cssSelector(".order-details, .order-summary");
            return isElementDisplayed(detailsSelector) || !getOrderNumber().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains("completed") || !getSuccessMessage().isEmpty();
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Check if view order link is displayed
     * @return true if view order link is visible
     */
    public boolean isViewOrderLinkDisplayed() {
        try {
            By viewOrderSelector = By.cssSelector("a[href*='/customer/orders'], .view-order");
            return isElementDisplayed(viewOrderSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click view order link
     * @return OrderDetailsPage
     */
    public OrderDetailsPage clickViewOrder() {
        try {
            By viewOrderSelector = By.cssSelector("a[href*='/customer/orders'], .view-order");
            click(viewOrderSelector);
            logger.info("Clicked view order link");
            return new OrderDetailsPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click view order: {}", e.getMessage());
            return new OrderDetailsPage(driver);
        }
    }
}
