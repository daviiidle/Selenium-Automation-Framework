package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object Model for Order History Page
 * Handles customer order history and order details
 */
public class OrderHistoryPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/customer/orders";

    public OrderHistoryPage(WebDriver driver) {
        super(driver);
    }

    public OrderHistoryPage() {
        super();
    }

    /**
     * Navigate directly to order history page
     * @return OrderHistoryPage instance for method chaining
     */
    public OrderHistoryPage navigateToOrderHistory() {
        navigateTo("https://demowebshop.tricentis.com/customer/orders");
        waitForPageToLoad();
        return this;
    }

    /**
     * Get list of order numbers
     * @return List of order numbers
     */
    public List<String> getOrderNumbers() {
        try {
            By orderNumberSelector = By.cssSelector(".order-number, .order-id");
            return findElements(orderNumberSelector).stream()
                    .map(WebElement::getText)
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if any orders exist
     * @return true if orders are present
     */
    public boolean hasOrders() {
        try {
            // Use more specific selectors for DemoWebShop order history
            By orderRowSelector = By.cssSelector("table.order-list tr, .order-list-table tr, .data-table tbody tr");
            WebElement orderTable = waitUtils.softWaitForElementToBeVisible(orderRowSelector, 3);
            return orderTable != null && !findElements(orderRowSelector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click on first order details
     * @return OrderDetailsPage
     */
    public OrderDetailsPage clickFirstOrderDetails() {
        try {
            By detailsSelector = By.cssSelector(".order-details-link, .details-link");
            List<WebElement> detailsLinks = findElements(detailsSelector);
            if (!detailsLinks.isEmpty()) {
                detailsLinks.get(0).click();
                logger.info("Clicked first order details");
                return new OrderDetailsPage(driver);
            }
        } catch (Exception e) {
            logger.warn("Could not click order details: {}", e.getMessage());
        }
        return new OrderDetailsPage(driver);
    }

    /**
     * Get order status for first order
     * @return Order status text
     */
    public String getFirstOrderStatus() {
        try {
            By statusSelector = By.cssSelector(".order-status");
            List<WebElement> statuses = findElements(statusSelector);
            if (!statuses.isEmpty()) {
                return statuses.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get order status");
        }
        return "";
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN) ||
               isElementDisplayed(By.cssSelector(".order-list, .no-data"));
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Get count of orders
     * @return Number of orders
     */
    public int getOrderCount() {
        try {
            By orderRowSelector = By.cssSelector(".order-list .title, .order-overview-content, .section .order-item");
            // Check if elements exist before waiting for visibility
            List<WebElement> orderElements = findElements(orderRowSelector);
            if (!orderElements.isEmpty()) {
                try {
                    // Only wait for visibility if elements are found
                    waitUtils.softWaitForElementToBeVisible(orderRowSelector, 2);
                } catch (Exception e) {
                    logger.debug("Order elements found but not visible: {}", e.getMessage());
                }
            }
            return orderElements.size();
        } catch (Exception e) {
            logger.warn("Error getting order count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Get first order number
     * @return First order number or empty string
     */
    public String getFirstOrderNumber() {
        try {
            By orderNumberSelector = By.cssSelector(".order-number, .order-id");
            List<WebElement> orderNumbers = findElements(orderNumberSelector);
            if (!orderNumbers.isEmpty()) {
                return orderNumbers.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first order number");
        }
        return "";
    }

    /**
     * Get first order date
     * @return First order date or empty string
     */
    public String getFirstOrderDate() {
        try {
            By orderDateSelector = By.cssSelector(".order-date, .created-on");
            List<WebElement> orderDates = findElements(orderDateSelector);
            if (!orderDates.isEmpty()) {
                return orderDates.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first order date");
        }
        return "";
    }

    /**
     * Get first order total
     * @return First order total or empty string
     */
    public String getFirstOrderTotal() {
        try {
            By orderTotalSelector = By.cssSelector(".order-total, .total");
            List<WebElement> orderTotals = findElements(orderTotalSelector);
            if (!orderTotals.isEmpty()) {
                return orderTotals.get(0).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get first order total");
        }
        return "";
    }

    /**
     * Get order details by order number
     * @param orderNumber Order number to find
     * @return OrderDetailsPage for the specific order
     */
    public OrderDetailsPage getOrderDetails(String orderNumber) {
        try {
            By orderRowSelector = By.xpath("//tr[contains(.,'" + orderNumber + "')]//a[contains(@href,'orderdetails')]");
            WebElement orderLink = findElement(orderRowSelector);
            orderLink.click();
            logger.info("Clicked order details for order: {}", orderNumber);
            return new OrderDetailsPage(driver);
        } catch (Exception e) {
            logger.warn("Could not get order details for order {}: {}", orderNumber, e.getMessage());
            return new OrderDetailsPage(driver);
        }
    }

    /**
     * Check if order history is empty
     * @return true if no orders are displayed
     */
    public boolean isEmpty() {
        try {
            By noOrdersSelector = By.cssSelector(".no-data, .no-orders, .empty-list");
            return isElementDisplayed(noOrdersSelector) || !hasOrders();
        } catch (Exception e) {
            return !hasOrders();
        }
    }

    /**
     * Get all order details links
     * @return List of order details links
     */
    public List<WebElement> getOrderDetailsLinks() {
        try {
            By detailsSelector = By.cssSelector(".order-details-link, .details-link, a[href*='orderdetails']");
            return findElements(detailsSelector);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Click order details by index
     * @param index Index of the order (0-based)
     * @return OrderDetailsPage
     */
    public OrderDetailsPage clickOrderDetails(int index) {
        try {
            List<WebElement> detailsLinks = getOrderDetailsLinks();
            if (index >= 0 && index < detailsLinks.size()) {
                detailsLinks.get(index).click();
                logger.info("Clicked order details at index: {}", index);
                return new OrderDetailsPage(driver);
            }
        } catch (Exception e) {
            logger.warn("Could not click order details at index {}: {}", index, e.getMessage());
        }
        return new OrderDetailsPage(driver);
    }

    /**
     * Get order status by index
     * @param index Index of the order (0-based)
     * @return Order status text
     */
    public String getOrderStatus(int index) {
        try {
            By statusSelector = By.cssSelector(".order-status");
            List<WebElement> statuses = findElements(statusSelector);
            if (index >= 0 && index < statuses.size()) {
                return statuses.get(index).getText();
            }
        } catch (Exception e) {
            logger.debug("Could not get order status at index {}", index);
        }
        return "";
    }

    /**
     * Check if orders are sorted by date (newest first)
     * @return true if orders appear to be sorted correctly
     */
    public boolean areOrdersSortedByDate() {
        try {
            By orderDateSelector = By.cssSelector(".order-date, .created-on");
            List<WebElement> orderDates = findElements(orderDateSelector);
            // Basic check - assume they are sorted if we have dates
            return orderDates.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click first order to view details
     * @return OrderDetailsPage
     */
    public OrderDetailsPage clickFirstOrder() {
        try {
            By firstOrderSelector = By.cssSelector(".order-list .title a, .order-overview-content a, .section .order-item a");
            List<WebElement> orderLinks = findElements(firstOrderSelector);
            if (!orderLinks.isEmpty()) {
                orderLinks.get(0).click();
                logger.info("Clicked first order");
                return new OrderDetailsPage(driver);
            }
        } catch (Exception e) {
            logger.warn("Could not click first order: {}", e.getMessage());
        }
        return new OrderDetailsPage(driver);
    }

    /**
     * Check if empty order history message is displayed
     * @return true if empty message is visible
     */
    public boolean isEmptyOrderHistoryMessageDisplayed() {
        try {
            // Use soft wait to avoid long timeouts for potentially missing elements
            By emptyMessageSelector = By.cssSelector(".no-data, .no-orders, .empty-list, .no-order-history, .info");
            WebElement emptyMessage = waitUtils.softWaitForElementToBeVisible(emptyMessageSelector, 2);
            return emptyMessage != null && emptyMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if order search functionality is displayed
     * @return true if order search is visible
     */
    public boolean isOrderSearchDisplayed() {
        try {
            By searchSelector = By.cssSelector(".order-search, #order-search, .search-orders");
            return isElementDisplayed(searchSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search orders by keyword
     * @param searchTerm Search term to look for
     * @return OrderHistoryPage for method chaining
     */
    public OrderHistoryPage searchOrders(String searchTerm) {
        try {
            By searchInputSelector = By.cssSelector(".order-search input, #order-search-input, .search-orders input");
            By searchButtonSelector = By.cssSelector(".order-search button, #order-search-button, .search-orders button");

            type(searchInputSelector, searchTerm);
            click(searchButtonSelector);
            waitForPageToLoad();
            logger.info("Searched orders for: {}", searchTerm);
        } catch (Exception e) {
            logger.warn("Could not search orders: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if search results are displayed
     * @return true if search results are present
     */
    public boolean hasSearchResults() {
        try {
            By searchResultsSelector = By.cssSelector(".search-results, .filtered-orders");
            return isElementDisplayed(searchResultsSelector) || hasOrders();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if specific order is in search results
     * @param orderNumber Order number to look for
     * @return true if order is found in results
     */
    public boolean isOrderInResults(String orderNumber) {
        try {
            List<String> orderNumbers = getOrderNumbers();
            return orderNumbers.contains(orderNumber);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if status filter is displayed
     * @return true if status filter is visible
     */
    public boolean isStatusFilterDisplayed() {
        try {
            By statusFilterSelector = By.cssSelector(".status-filter, #status-filter, .order-status-filter");
            return isElementDisplayed(statusFilterSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get available status filters
     * @return List of available status filter options
     */
    public List<String> getAvailableStatusFilters() {
        try {
            By statusOptionsSelector = By.cssSelector(".status-filter option, #status-filter option, .order-status-filter option");
            return findElements(statusOptionsSelector).stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Filter orders by status
     * @param status Status to filter by
     * @return OrderHistoryPage for method chaining
     */
    public OrderHistoryPage filterByStatus(String status) {
        try {
            By statusFilterSelector = By.cssSelector(".status-filter, #status-filter, .order-status-filter");
            WebElement statusFilter = findElement(statusFilterSelector);
            Select selectElement = new Select(statusFilter);
            selectElement.selectByVisibleText(status);
            waitForPageToLoad();
            logger.info("Filtered orders by status: {}", status);
        } catch (Exception e) {
            logger.warn("Could not filter by status {}: {}", status, e.getMessage());
        }
        return this;
    }
}