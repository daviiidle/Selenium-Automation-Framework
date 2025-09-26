package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;

/**
 * Page Object Model for Order Details Page
 * Handles individual order details and information
 */
public class OrderDetailsPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/orderdetails";

    public OrderDetailsPage(WebDriver driver) {
        super(driver);
    }

    public OrderDetailsPage() {
        super();
    }

    /**
     * Get order number
     * @return Order number
     */
    public String getOrderNumber() {
        try {
            By orderNumberSelector = By.cssSelector(".order-number, .order-id");
            String orderText = getText(orderNumberSelector);
            return orderText.replaceAll("[^0-9]", "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get order status
     * @return Order status text
     */
    public String getOrderStatus() {
        try {
            By statusSelector = By.cssSelector(".order-status");
            return getText(statusSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get order total
     * @return Order total amount
     */
    public String getOrderTotal() {
        try {
            By totalSelector = By.cssSelector(".order-total, .total");
            return getText(totalSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get ordered items
     * @return List of item names
     */
    public List<String> getOrderedItems() {
        try {
            By itemSelector = By.cssSelector(".product-name, .item-name");
            return findElements(itemSelector).stream()
                    .map(SelenideElement::getText)
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if order details are displayed
     * @return true if order details are visible
     */
    public boolean areOrderDetailsDisplayed() {
        try {
            By detailsSelector = By.cssSelector(".order-details, .order-info");
            return isElementDisplayed(detailsSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get billing address
     * @return Billing address text
     */
    public String getBillingAddress() {
        try {
            By billingSelector = By.cssSelector(".billing-address, .bill-to-address");
            return getText(billingSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get shipping address
     * @return Shipping address text
     */
    public String getShippingAddress() {
        try {
            By shippingSelector = By.cssSelector(".shipping-address, .ship-to-address");
            return getText(shippingSelector);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN) ||
               isElementDisplayed(By.cssSelector(".order-details, .order-overview"));
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Get order date
     * @return Order date text
     */
    public String getOrderDate() {
        try {
            By dateSelector = By.cssSelector(".order-date, .created-on, .order-created");
            return getText(dateSelector);
        } catch (Exception e) {
            logger.debug("Could not get order date");
            return "";
        }
    }

    /**
     * Get payment method
     * @return Payment method used for the order
     */
    public String getPaymentMethod() {
        try {
            By paymentSelector = By.cssSelector(".payment-method, .payment-info");
            return getText(paymentSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get shipping method
     * @return Shipping method used for the order
     */
    public String getShippingMethod() {
        try {
            By shippingMethodSelector = By.cssSelector(".shipping-method, .shipping-option");
            return getText(shippingMethodSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get order subtotal
     * @return Order subtotal amount
     */
    public String getOrderSubtotal() {
        try {
            By subtotalSelector = By.cssSelector(".order-subtotal, .sub-total");
            return getText(subtotalSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get shipping cost
     * @return Shipping cost amount
     */
    public String getShippingCost() {
        try {
            By shippingCostSelector = By.cssSelector(".shipping-cost, .shipping-charge");
            return getText(shippingCostSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get tax amount
     * @return Tax amount
     */
    public String getTaxAmount() {
        try {
            By taxSelector = By.cssSelector(".tax-amount, .order-tax");
            return getText(taxSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if order can be re-ordered
     * @return true if re-order option is available
     */
    public boolean canReorder() {
        try {
            By reorderSelector = By.cssSelector(".re-order-button, input[value*='Re-order']");
            return isElementDisplayed(reorderSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click re-order button
     * @return ShoppingCartPage after re-ordering
     */
    public ShoppingCartPage clickReorder() {
        try {
            By reorderSelector = By.cssSelector(".re-order-button, input[value*='Re-order']");
            click(reorderSelector);
            logger.info("Clicked re-order button");
            return new ShoppingCartPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click re-order: {}", e.getMessage());
            return new ShoppingCartPage(driver);
        }
    }

    /**
     * Get order items count
     * @return Number of items in the order
     */
    public int getOrderItemsCount() {
        try {
            By itemSelector = By.cssSelector(".product-name, .item-name, .order-item");
            return findElements(itemSelector).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if order has tracking information
     * @return true if tracking information is available
     */
    public boolean hasTrackingInfo() {
        try {
            By trackingSelector = By.cssSelector(".tracking-info, .tracking-number");
            return isElementDisplayed(trackingSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get tracking number
     * @return Tracking number or empty string
     */
    public String getTrackingNumber() {
        try {
            By trackingSelector = By.cssSelector(".tracking-number, .tracking-info");
            return getText(trackingSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Print order details
     * @return OrderDetailsPage for method chaining
     */
    public OrderDetailsPage printOrder() {
        try {
            By printSelector = By.cssSelector(".print-order-button, input[value*='Print']");
            if (isElementDisplayed(printSelector)) {
                click(printSelector);
                logger.info("Clicked print order button");
            }
        } catch (Exception e) {
            logger.warn("Could not print order: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Get order notes/comments
     * @return Order notes text
     */
    public String getOrderNotes() {
        try {
            By notesSelector = By.cssSelector(".order-notes, .order-comments");
            return getText(notesSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if order is cancelled
     * @return true if order status indicates cancellation
     */
    public boolean isCancelled() {
        try {
            String status = getOrderStatus().toLowerCase();
            return status.contains("cancel") || status.contains("cancelled");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if order is completed
     * @return true if order status indicates completion
     */
    public boolean isCompleted() {
        try {
            String status = getOrderStatus().toLowerCase();
            return status.contains("complete") || status.contains("delivered") || status.contains("shipped");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if order is pending
     * @return true if order status indicates pending
     */
    public boolean isPending() {
        try {
            String status = getOrderStatus().toLowerCase();
            return status.contains("pending") || status.contains("processing");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if order details are displayed (alias for areOrderDetailsDisplayed)
     * @return true if order details are visible
     */
    public boolean isOrderDetailsDisplayed() {
        return areOrderDetailsDisplayed();
    }

    /**
     * Check if order has items
     * @return true if order contains items
     */
    public boolean hasOrderItems() {
        try {
            By itemSelector = By.cssSelector(".product-name, .item-name, .order-item");
            return !findElements(itemSelector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get order item count (alias for getOrderItemsCount)
     * @return Number of items in the order
     */
    public int getOrderItemCount() {
        return getOrderItemsCount();
    }

    /**
     * Get first item name from order
     * @return First item name or empty string
     */
    public String getFirstItemName() {
        try {
            By itemNameSelector = By.cssSelector(".product-name, .item-name");
            ElementsCollection itemNames = $$(itemNameSelector);
            if (!itemNames.isEmpty()) {
                return itemNames.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first item name");
        }
        return "";
    }

    /**
     * Get first item price from order
     * @return First item price or empty string
     */
    public String getFirstItemPrice() {
        try {
            By itemPriceSelector = By.cssSelector(".product-price, .item-price, .unit-price");
            ElementsCollection itemPrices = $$(itemPriceSelector);
            if (!itemPrices.isEmpty()) {
                return itemPrices.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first item price");
        }
        return "";
    }

    /**
     * Get first item quantity from order
     * @return First item quantity or empty string
     */
    public String getFirstItemQuantity() {
        try {
            By itemQuantitySelector = By.cssSelector(".product-quantity, .item-quantity, .qty");
            ElementsCollection itemQuantities = $$(itemQuantitySelector);
            if (!itemQuantities.isEmpty()) {
                return itemQuantities.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first item quantity");
        }
        return "";
    }

    /**
     * Check if reorder button is displayed
     * @return true if reorder button is visible
     */
    public boolean isReorderButtonDisplayed() {
        return canReorder();
    }

    /**
     * Click reorder button (alias for clickReorder)
     * @return ShoppingCartPage after reordering
     */
    public ShoppingCartPage clickReorderButton() {
        return clickReorder();
    }
}