package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Page Object Model for Shopping Cart Page
 * Handles cart item management, quantity updates, price calculations, and checkout navigation
 */
public class ShoppingCartPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/cart";

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
    }

    public ShoppingCartPage() {
        super();
    }

    /**
     * Navigate directly to shopping cart page
     * @return ShoppingCartPage instance for method chaining
     */
    public ShoppingCartPage navigateToCart() {
        navigateTo("https://demowebshop.tricentis.com/cart");
        waitForPageToLoad();
        return this;
    }

    // Cart State Methods

    /**
     * Check if cart is empty
     * @return true if cart contains no items
     */
    public boolean isCartEmpty() {
        try {
            By emptyMessageSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.empty_cart.message");
            return isElementDisplayed(emptyMessageSelector);
        } catch (Exception e) {
            return getCartItems().isEmpty();
        }
    }

    /**
     * Get empty cart message
     * @return Empty cart message text
     */
    public String getEmptyCartMessage() {
        try {
            By emptyMessageSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.empty_cart.message");
            if (isElementDisplayed(emptyMessageSelector)) {
                return getText(emptyMessageSelector);
            }
        } catch (Exception e) {
            logger.debug("Empty cart message not found");
        }
        return "";
    }

    /**
     * Get all items in cart
     * @return List of CartItem objects
     */
    public List<CartItem> getCartItems() {
        try {
            By itemRowSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.item_row");
            List<WebElement> itemElements = findElements(itemRowSelector);

            return itemElements.stream()
                    .map(element -> new CartItem(element, driver))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.debug("No cart items found");
            return List.of();
        }
    }

    /**
     * Get cart item by index
     * @param index Index of the cart item (0-based)
     * @return CartItem object
     */
    public CartItem getCartItem(int index) {
        List<CartItem> items = getCartItems();
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        throw new IndexOutOfBoundsException("Cart item index " + index + " is out of bounds. Available items: " + items.size());
    }

    /**
     * Find cart item by product name
     * @param productName Name of the product to find
     * @return CartItem if found, null otherwise
     */
    public CartItem findCartItemByName(String productName) {
        List<CartItem> items = getCartItems();
        return items.stream()
                .filter(item -> item.getProductName().toLowerCase().contains(productName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get number of items in cart
     * @return Number of different products in cart
     */
    public int getCartItemCount() {
        return getCartItems().size();
    }

    /**
     * Get total quantity of all items in cart
     * @return Total quantity across all cart items
     */
    public int getTotalQuantity() {
        return getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Cart Modification Methods

    /**
     * Update quantity for a cart item by index
     * @param itemIndex Index of the cart item
     * @param newQuantity New quantity value
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage updateItemQuantity(int itemIndex, int newQuantity) {
        CartItem item = getCartItem(itemIndex);
        item.updateQuantity(newQuantity);

        // Click update cart button
        updateCart();
        logger.info("Updated item {} quantity to {}", itemIndex, newQuantity);
        return this;
    }

    /**
     * Update quantity for a cart item by product name
     * @param productName Name of the product
     * @param newQuantity New quantity value
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage updateItemQuantity(String productName, int newQuantity) {
        CartItem item = findCartItemByName(productName);
        if (item != null) {
            item.updateQuantity(newQuantity);
            updateCart();
            logger.info("Updated {} quantity to {}", productName, newQuantity);
        } else {
            throw new IllegalArgumentException("Product not found in cart: " + productName);
        }
        return this;
    }

    /**
     * Remove item from cart by index
     * @param itemIndex Index of the item to remove
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage removeItem(int itemIndex) {
        CartItem item = getCartItem(itemIndex);
        item.remove();
        waitForPageToLoad();
        logger.info("Removed item at index {}", itemIndex);
        return this;
    }

    /**
     * Remove item from cart by product name
     * @param productName Name of the product to remove
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage removeItem(String productName) {
        CartItem item = findCartItemByName(productName);
        if (item != null) {
            item.remove();
            waitForPageToLoad();
            logger.info("Removed product: {}", productName);
        } else {
            throw new IllegalArgumentException("Product not found in cart: " + productName);
        }
        return this;
    }

    /**
     * Update cart after making changes
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage updateCart() {
        try {
            By updateButtonSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.update_quantity");
            if (isElementDisplayed(updateButtonSelector)) {
                click(updateButtonSelector);
                waitForPageToLoad();
                logger.info("Updated cart");
            }
        } catch (Exception e) {
            logger.error("Error updating cart: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Clear all items from cart
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage clearCart() {
        List<CartItem> items = getCartItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            items.get(i).remove();
            waitForPageToLoad();
        }
        logger.info("Cleared all items from cart");
        return this;
    }

    // Price and Totals Methods

    /**
     * Get cart subtotal
     * @return Subtotal as BigDecimal
     */
    public BigDecimal getSubtotal() {
        try {
            By subtotalSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_totals.subtotal");
            String subtotalText = getText(subtotalSelector);
            return parsePriceString(subtotalText);
        } catch (Exception e) {
            logger.error("Error getting subtotal: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Get shipping cost
     * @return Shipping cost as BigDecimal
     */
    public BigDecimal getShippingCost() {
        try {
            By shippingSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_totals.shipping");
            String shippingText = getText(shippingSelector);
            return parsePriceString(shippingText);
        } catch (Exception e) {
            logger.debug("Shipping cost not found or is zero");
            return BigDecimal.ZERO;
        }
    }

    /**
     * Get cart total
     * @return Total amount as BigDecimal
     */
    public BigDecimal getTotal() {
        try {
            By totalSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_totals.total");
            String totalText = getText(totalSelector);
            return parsePriceString(totalText);
        } catch (Exception e) {
            logger.error("Error getting total: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Parse price string to BigDecimal
     * @param priceText Price text (e.g., "$19.99", "19.99")
     * @return Price as BigDecimal
     */
    private BigDecimal parsePriceString(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            // Remove currency symbols and extra whitespace
            String cleanPrice = priceText.replaceAll("[^0-9.,]", "").trim();
            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
            Number number = format.parse(cleanPrice);
            return BigDecimal.valueOf(number.doubleValue());
        } catch (ParseException e) {
            logger.error("Error parsing price: {}", priceText);
            return BigDecimal.ZERO;
        }
    }

    // Discount and Coupon Methods

    /**
     * Apply discount coupon code
     * @param couponCode Coupon code to apply
     * @return ShoppingCartPage for method chaining
     */
    public ShoppingCartPage applyCouponCode(String couponCode) {
        try {
            By couponInputSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.discount_coupon.coupon_code");
            By applyCouponSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.discount_coupon.apply_coupon");

            if (isElementDisplayed(couponInputSelector)) {
                type(couponInputSelector, couponCode);
                click(applyCouponSelector);
                waitForPageToLoad();
                logger.info("Applied coupon code: {}", couponCode);
            } else {
                logger.warn("Coupon input not found");
            }
        } catch (Exception e) {
            logger.error("Error applying coupon code: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Get current coupon code value
     * @return Current coupon code or empty string
     */
    public String getCouponCode() {
        try {
            By couponInputSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.discount_coupon.coupon_code");
            return getAttribute(couponInputSelector, "value");
        } catch (Exception e) {
            return "";
        }
    }

    // Navigation Methods

    /**
     * Continue shopping (go back to previous page or homepage)
     * @return HomePage
     */
    public HomePage continueShopping() {
        try {
            By continueShoppingSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_actions.continue_shopping");
            if (isElementDisplayed(continueShoppingSelector)) {
                click(continueShoppingSelector);
                logger.info("Clicked continue shopping");
            } else {
                navigateTo("https://demowebshop.tricentis.com/");
            }
        } catch (Exception e) {
            logger.error("Error continuing shopping: {}", e.getMessage());
            navigateTo("https://demowebshop.tricentis.com/");
        }
        return new HomePage(driver);
    }

    /**
     * Proceed to checkout
     * @return CheckoutPage
     */
    public CheckoutPage proceedToCheckout() {
        By checkoutButtonSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_actions.checkout_button");

        if (!isCartEmpty()) {
            click(checkoutButtonSelector);
            waitForPageToLoad();
            logger.info("Proceeded to checkout");
            return new CheckoutPage(driver);
        } else {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }
    }

    /**
     * Check if checkout button is available
     * @return true if checkout can be initiated
     */
    public boolean canCheckout() {
        try {
            if (isCartEmpty()) {
                return false;
            }
            By checkoutButtonSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_actions.checkout_button");
            return isElementDisplayed(checkoutButtonSelector) && isElementEnabled(checkoutButtonSelector);
        } catch (Exception e) {
            return false;
        }
    }

    // Additional Cart Utility Methods

    /**
     * Check if cart has items (opposite of isCartEmpty)
     * @return true if cart contains items
     */
    public boolean hasItems() {
        return !isCartEmpty();
    }

    /**
     * Get the number of items in the cart
     * @return Number of items in cart
     */
    public int getItemCount() {
        return getCartItems().size();
    }

    /**
     * Check if cart total is displayed
     * @return true if total amount is visible
     */
    public boolean isTotalDisplayed() {
        try {
            By totalSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.order_summary.order_total");
            return isElementDisplayed(totalSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if checkout button is displayed
     * @return true if checkout button is visible
     */
    public boolean isCheckoutButtonDisplayed() {
        try {
            By checkoutButtonSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_actions.checkout_button");
            return isElementDisplayed(checkoutButtonSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if empty cart message is displayed
     * @return true if empty cart message is shown
     */
    public boolean isEmptyCartMessageDisplayed() {
        return isCartEmpty();
    }

    /**
     * Get cart total amount as string
     * @return Cart total as string (with currency symbol)
     */
    public String getCartTotal() {
        try {
            By totalSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.order_summary.order_total");
            return getText(totalSelector);
        } catch (Exception e) {
            return "0.00";
        }
    }

    /**
     * Get cart total amount as BigDecimal
     * @return Cart total as BigDecimal
     */
    public java.math.BigDecimal getCartTotalAmount() {
        try {
            String totalText = getCartTotal();
            String cleanPrice = totalText.replaceAll("[^0-9.]", "");
            return new java.math.BigDecimal(cleanPrice);
        } catch (Exception e) {
            return java.math.BigDecimal.ZERO;
        }
    }

    /**
     * Check if subtotal is displayed
     * @return true if subtotal is visible
     */
    public boolean isSubtotalDisplayed() {
        try {
            By subtotalSelector = By.cssSelector(".cart-subtotal, .subtotal");
            return isElementDisplayed(subtotalSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if tax is displayed
     * @return true if tax amount is visible
     */
    public boolean isTaxDisplayed() {
        try {
            By taxSelector = By.cssSelector(".tax, .cart-tax");
            return isElementDisplayed(taxSelector);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Get tax amount
     * @return Tax as BigDecimal
     */
    public java.math.BigDecimal getTax() {
        try {
            By taxSelector = By.cssSelector(".tax, .cart-tax");
            String taxText = getText(taxSelector);
            String cleanPrice = taxText.replaceAll("[^0-9.]", "");
            return new java.math.BigDecimal(cleanPrice);
        } catch (Exception e) {
            return java.math.BigDecimal.ZERO;
        }
    }

    /**
     * Get shipping amount
     * @return Shipping as BigDecimal
     */
    public java.math.BigDecimal getShipping() {
        try {
            By shippingSelector = By.cssSelector(".shipping, .cart-shipping");
            String shippingText = getText(shippingSelector);
            String cleanPrice = shippingText.replaceAll("[^0-9.]", "");
            return new java.math.BigDecimal(cleanPrice);
        } catch (Exception e) {
            return java.math.BigDecimal.ZERO;
        }
    }

    // Page Validation Methods

    /**
     * Verify that shopping cart page is loaded correctly
     * @return true if page is loaded correctly
     */
    @Override
    public boolean isPageLoaded() {
        try {
            // Check for cart table or empty cart message
            By cartTableSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.cart_table");
            By emptyMessageSelector = SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.empty_cart.message");

            return isElementDisplayed(cartTableSelector) || isElementDisplayed(emptyMessageSelector);
        } catch (Exception e) {
            logger.error("Error checking if cart page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the page URL pattern for validation
     * @return Cart page URL pattern
     */
    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Check if on shopping cart page
     * @return true if on cart page
     */
    public boolean isOnCartPage() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN);
    }

    // Inner class for cart item management
    public static class CartItem {
        private final WebElement itemElement;
        private final WebDriver driver;

        public CartItem(WebElement itemElement, WebDriver driver) {
            this.itemElement = itemElement;
            this.driver = driver;
        }

        /**
         * Get product name
         * @return Product name text
         */
        public String getProductName() {
            try {
                WebElement nameElement = itemElement.findElement(
                    SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.item_name"));
                return nameElement.getText();
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * Get unit price
         * @return Unit price as BigDecimal
         */
        public BigDecimal getUnitPrice() {
            try {
                WebElement priceElement = itemElement.findElement(
                    SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.item_price"));
                String priceText = priceElement.getText();
                return parsePriceString(priceText);
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        /**
         * Get current quantity
         * @return Current quantity
         */
        public int getQuantity() {
            try {
                WebElement qtyInput = itemElement.findElement(
                    SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.quantity_input"));
                String qtyValue = qtyInput.getAttribute("value");
                return Integer.parseInt(qtyValue);
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * Get item total (unit price × quantity)
         * @return Item total as BigDecimal
         */
        public BigDecimal getItemTotal() {
            try {
                WebElement totalElement = itemElement.findElement(
                    SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.item_total"));
                String totalText = totalElement.getText();
                return parsePriceString(totalText);
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        /**
         * Update quantity for this item
         * @param newQuantity New quantity value
         * @return CartItem for method chaining
         */
        public CartItem updateQuantity(int newQuantity) {
            try {
                WebElement qtyInput = itemElement.findElement(
                    SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.quantity_input"));
                qtyInput.clear();
                qtyInput.sendKeys(String.valueOf(newQuantity));
            } catch (Exception e) {
                throw new RuntimeException("Could not update quantity", e);
            }
            return this;
        }

        /**
         * Remove this item from cart
         */
        public void remove() {
            try {
                WebElement removeButton = itemElement.findElement(
                    SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.remove_item"));
                removeButton.click();
            } catch (Exception e) {
                throw new RuntimeException("Could not remove item", e);
            }
        }

        /**
         * Get product image element
         * @return WebElement for product image
         */
        public WebElement getProductImage() {
            return itemElement.findElement(
                SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.item_image"));
        }

        /**
         * Click on product name to go to product details
         * @return ProductDetailsPage
         */
        public ProductDetailsPage clickProductName() {
            WebElement nameElement = itemElement.findElement(
                SelectorUtils.getCartSelector("cart_and_checkout.shopping_cart.cart_with_items.item_name"));
            nameElement.click();
            return new ProductDetailsPage(driver);
        }

        /**
         * Get underlying WebElement
         * @return WebElement representing this cart item
         */
        public WebElement getElement() {
            return itemElement;
        }

        /**
         * Parse price string to BigDecimal
         * @param priceText Price text
         * @return Price as BigDecimal
         */
        private BigDecimal parsePriceString(String priceText) {
            if (priceText == null || priceText.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }

            try {
                String cleanPrice = priceText.replaceAll("[^0-9.,]", "").trim();
                NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                Number number = format.parse(cleanPrice);
                return BigDecimal.valueOf(number.doubleValue());
            } catch (ParseException e) {
                return BigDecimal.ZERO;
            }
        }
    }
}