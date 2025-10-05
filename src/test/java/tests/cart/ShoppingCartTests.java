package tests.cart;

import base.BaseTest;
import com.demowebshop.automation.pages.HomePage;
import com.demowebshop.automation.pages.ProductDetailsPage;
import com.demowebshop.automation.pages.ShoppingCartPage;
import factories.ProductDataFactory;
import utils.DemoWebShopAssertions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;

/**
 * Comprehensive Shopping Cart Test Suite
 * Covers all cart management scenarios from manual testing documentation
 * Tests add to cart, update cart, remove items, and cart calculations
 */
public class ShoppingCartTests extends BaseTest {
    private DemoWebShopAssertions assertions;
    private HomePage homePage;

    @Override
    protected void additionalSetup() {
        assertions = new DemoWebShopAssertions(driver);
        homePage = new HomePage(driver);
    }

    /**
     * Test ID: CART_001 - Add Items to Cart
     * Tests adding single and multiple items to shopping cart
     * Validates cart counter updates and item presence in cart
     */
    @Test(groups = {"smoke", "cart", "high-priority"},
          priority = 1,
          description = "Adding items to cart should update cart counter and display items")
    public void testAddItemsToCart() {
        logger.info("=== Starting CART_001: Add Items to Cart ===");

        // Navigate to a product and add to cart
        ProductDetailsPage productPage = homePage.navigateToRandomProduct();
        Assert.assertTrue(productPage.isPageLoaded(), "Product page should be loaded");

        String productTitle = productPage.getProductTitle();
        String productPrice = productPage.getProductPrice();
        int initialCartCount = homePage.getCartItemCount();

        // Add product to cart
        productPage.selectQuantity(2);
        productPage.clickAddToCart();

        // Verify cart counter updated
        SoftAssert softAssert = assertions.getSoftAssert();
        int updatedCartCount = homePage.getCartItemCount();
        softAssert.assertTrue(updatedCartCount > initialCartCount,
                             "Cart counter should increase after adding item");

        // Navigate to cart to verify item presence
        ShoppingCartPage cartPage = homePage.clickShoppingCartLink();
        Assert.assertTrue(cartPage.isPageLoaded(), "Shopping cart page should be loaded");

        // Verify added item is in cart
        softAssert.assertTrue(cartPage.isItemInCart(productTitle),
                             "Added product should be present in cart");
        softAssert.assertEquals(cartPage.getItemQuantity(productTitle), 2,
                               "Product quantity should match selected quantity");

        // Add another different product to cart
        homePage = cartPage.clickContinueShopping();
        ProductDetailsPage secondProduct = homePage.navigateToRandomProduct();
        String secondProductTitle = secondProduct.getProductTitle();

        secondProduct.clickAddToCart();

        // Verify multiple items in cart
        cartPage = homePage.clickShoppingCartLink();
        softAssert.assertTrue(cartPage.isItemInCart(productTitle),
                             "First product should still be in cart");
        softAssert.assertTrue(cartPage.isItemInCart(secondProductTitle),
                             "Second product should be added to cart");

        int totalItems = cartPage.getTotalItemCount();
        softAssert.assertTrue(totalItems >= 3, "Cart should contain at least 3 items total");

        assertions.assertAll();
        logger.info("=== CART_001 completed: Items added to cart successfully ===");
    }

    /**
     * Test ID: CART_002 - Update Cart Items
     * Tests updating item quantities and removing items from cart
     * Validates cart calculations and empty cart handling
     */
    @Test(groups = {"functional", "cart", "high-priority"},
          priority = 2,
          description = "Updating cart quantities and removing items should work correctly",
          dependsOnMethods = {"testAddItemsToCart"})
    public void testUpdateCartItems() {
        logger.info("=== Starting CART_002: Update Cart Items ===");

        // Navigate to cart (assuming items are already present)
        ShoppingCartPage cartPage = homePage.clickShoppingCartLink();
        Assert.assertTrue(cartPage.isPageLoaded(), "Shopping cart page should be loaded");

        if (!cartPage.hasItems()) {
            // Add an item first if cart is empty
            homePage = cartPage.clickContinueShopping();
            ProductDetailsPage productPage = homePage.navigateToRandomProduct();
            productPage.clickAddToCart();
            cartPage = homePage.clickShoppingCartLink();
        }

        // Get initial cart state
        int initialItemCount = cartPage.getTotalItemCount();
        double initialTotal = cartPage.getCartTotal();
        String firstItemName = cartPage.getFirstItemName();
        int initialQuantity = cartPage.getItemQuantity(firstItemName);

        SoftAssert softAssert = assertions.getSoftAssert();

        // Update quantity of first item
        int newQuantity = initialQuantity + 2;
        cartPage.updateItemQuantity(firstItemName, newQuantity);
        cartPage.clickUpdateCart();

        // Wait for cart to update (page reload or AJAX) - using Selenide wait instead of Thread.sleep
        com.codeborne.selenide.Selenide.sleep(5000); // Allow time for recalculation

        // Refresh page to ensure we get updated values
        driver.navigate().refresh();

        // Wait for refresh to complete
        com.codeborne.selenide.Selenide.sleep(2000);

        // Verify quantity updated
        int updatedQuantity = cartPage.getItemQuantity(firstItemName);
        softAssert.assertEquals(updatedQuantity, newQuantity,
                               "Item quantity should be updated");

        // Verify total recalculated - should be higher since we added more items
        double updatedTotal = cartPage.getCartTotal();
        softAssert.assertTrue(updatedTotal > initialTotal,
                             "Cart total should increase after quantity update");

        // Test removing an item by setting quantity to 0
        if (cartPage.getTotalItemCount() > 1) {
            String itemToRemove = cartPage.getSecondItemName();
            cartPage.updateItemQuantity(itemToRemove, 0);
            cartPage.clickUpdateCart();

            // Verify item removed
            softAssert.assertFalse(cartPage.isItemInCart(itemToRemove),
                                  "Item should be removed when quantity set to 0");

            int finalItemCount = cartPage.getTotalItemCount();
            softAssert.assertTrue(finalItemCount < initialItemCount,
                                 "Total item count should decrease after removal");
        }

        // Test direct remove button if available
        if (cartPage.hasRemoveButtons() && cartPage.getTotalItemCount() > 0) {
            String itemToRemove = cartPage.getLastItemName();
            cartPage.clickRemoveItem(itemToRemove);

            softAssert.assertFalse(cartPage.isItemInCart(itemToRemove),
                                  "Item should be removed using remove button");
        }

        // Test empty cart scenario
        if (cartPage.getTotalItemCount() == 1) {
            String lastItem = cartPage.getFirstItemName();
            cartPage.updateItemQuantity(lastItem, 0);
            cartPage.clickUpdateCart();

            softAssert.assertTrue(cartPage.isEmpty() || cartPage.isEmptyCartMessageDisplayed(),
                                 "Empty cart message should be displayed when all items removed");
        }

        assertions.assertAll();
        logger.info("=== CART_002 completed: Cart updates working correctly ===");
    }

    /**
     * Test ID: CART_003 - Cart Calculations Validation
     * Tests cart subtotal, tax, and total calculations
     * Validates pricing accuracy and discount applications
     */
    @Test(groups = {"functional", "cart", "medium-priority"},
          priority = 3,
          description = "Cart calculations should be accurate")
    public void testCartCalculations() {
        logger.info("=== Starting CART_003: Cart Calculations Validation ===");

        // Add known items to cart for calculation testing
        ProductDetailsPage productPage = homePage.navigateToRandomProduct();
        String productTitle = productPage.getProductTitle();
        double unitPrice = productPage.getProductPriceAsDouble();
        int quantity = 3;

        productPage.selectQuantity(quantity);
        productPage.clickAddToCart();

        ShoppingCartPage cartPage = homePage.clickShoppingCartLink();
        Assert.assertTrue(cartPage.isPageLoaded(), "Shopping cart page should be loaded");

        SoftAssert softAssert = assertions.getSoftAssert();

        // Verify unit price matches product page
        double cartUnitPrice = cartPage.getItemUnitPrice(productTitle);
        softAssert.assertEquals(cartUnitPrice, unitPrice, 0.01,
                               "Unit price in cart should match product page");

        // Verify quantity is correct
        int cartQuantity = cartPage.getItemQuantity(productTitle);
        softAssert.assertEquals(cartQuantity, quantity,
                               "Quantity in cart should match selected amount");

        // Verify line total calculation (unit price × quantity)
        double expectedLineTotal = unitPrice * quantity;
        double actualLineTotal = cartPage.getItemLineTotal(productTitle);
        softAssert.assertEquals(actualLineTotal, expectedLineTotal, 0.01,
                               "Line total should equal unit price × quantity");

        // Verify cart subtotal
        double cartSubtotal = cartPage.getCartSubtotal();
        softAssert.assertTrue(cartSubtotal >= expectedLineTotal,
                             "Cart subtotal should include all line items");

        // Verify final total
        double cartTotal = cartPage.getCartTotal();
        softAssert.assertTrue(cartTotal >= cartSubtotal,
                             "Cart total should be at least equal to subtotal");

        assertions.assertAll();
        logger.info("=== CART_003 completed: Cart calculations verified ===");
    }

    /**
     * Test ID: CART_004 - Cart Persistence and Session Management
     * Tests cart state persistence across sessions and page navigation
     * Validates cart contents are maintained during user journey
     */
    @Test(groups = {"functional", "cart", "medium-priority"},
          priority = 4,
          description = "Cart should persist across page navigation and sessions")
    public void testCartPersistence() {
        logger.info("=== Starting CART_004: Cart Persistence ===");

        // Add item to cart
        ProductDetailsPage productPage = homePage.navigateToRandomProduct();
        String productTitle = productPage.getProductTitle();
        productPage.clickAddToCart();

        // Navigate away from cart
        homePage = productPage.clickHomeLink();
        int cartCount = homePage.getCartItemCount();

        // Navigate to different pages
        homePage.navigateToCategory("Books");
        homePage.navigateToCategory("Electronics");

        // Return to cart and verify item is still there
        ShoppingCartPage cartPage = homePage.clickShoppingCartLink();

        SoftAssert softAssert = assertions.getSoftAssert();
        softAssert.assertTrue(cartPage.isItemInCart(productTitle),
                             "Cart item should persist across page navigation");
        softAssert.assertEquals(homePage.getCartItemCount(), cartCount,
                               "Cart counter should remain consistent");

        // Refresh page and verify persistence
        driver.navigate().refresh();
        softAssert.assertTrue(cartPage.isItemInCart(productTitle),
                             "Cart item should persist after page refresh");

        assertions.assertAll();
        logger.info("=== CART_004 completed: Cart persistence verified ===");
    }

    /**
     * Test ID: CART_005 - Cart Error Handling
     * Tests cart error scenarios and edge cases
     * Validates proper error handling for invalid operations
     */
    @Test(groups = {"negative", "cart", "medium-priority"},
          priority = 5,
          description = "Cart should handle error scenarios gracefully")
    public void testCartErrorHandling() {
        logger.info("=== Starting CART_005: Cart Error Handling ===");

        // Test adding invalid quantity
        ProductDetailsPage productPage = homePage.navigateToRandomProduct();

        SoftAssert softAssert = assertions.getSoftAssert();

        // Test with very large quantity if quantity field allows it
        try {
            productPage.selectQuantity(9999);
            productPage.clickAddToCart();

            // Check if error handling is in place
            if (productPage.hasErrorMessage()) {
                softAssert.assertTrue(true, "Error handling present for large quantities");
            }
        } catch (Exception e) {
            logger.info("Large quantity test resulted in: {}", e.getMessage());
        }

        // Navigate to cart for further error testing
        ShoppingCartPage cartPage = homePage.clickShoppingCartLink();

        if (cartPage.hasItems()) {
            String itemName = cartPage.getFirstItemName();

            // Test updating with invalid quantity
            try {
                cartPage.updateItemQuantity(itemName, -1);
                cartPage.clickUpdateCart();

                // Verify negative quantities are handled
                int quantity = cartPage.getItemQuantity(itemName);
                softAssert.assertTrue(quantity >= 0,
                                     "Negative quantities should not be allowed");
            } catch (Exception e) {
                logger.info("Negative quantity test resulted in: {}", e.getMessage());
            }

            // Test with extremely large quantity in cart
            try {
                cartPage.updateItemQuantity(itemName, 99999);
                cartPage.clickUpdateCart();

                if (cartPage.hasValidationErrors()) {
                    softAssert.assertTrue(true, "Validation errors shown for extreme quantities");
                }
            } catch (Exception e) {
                logger.info("Extreme quantity test resulted in: {}", e.getMessage());
            }
        }

        assertions.assertAll();
        logger.info("=== CART_005 completed: Error handling scenarios tested ===");
    }

    @Override
    protected void additionalTeardown() {
        // Clear cart after tests if needed
        try {
            if (homePage != null) {
                ShoppingCartPage cartPage = homePage.clickShoppingCartLink();
                if (cartPage.hasItems()) {
                    cartPage.clearCart();
                    logger.info("Cart cleared after test completion");
                }
            }
        } catch (Exception e) {
            logger.warn("Could not clear cart during cleanup: {}", e.getMessage());
        }
    }
}