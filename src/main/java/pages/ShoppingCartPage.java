package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ShoppingCartPage extends BasePage {

    @FindBy(css = ".cart-total")
    private WebElement cartTotal;

    @FindBy(css = ".checkout-button")
    private WebElement checkoutButton;

    @FindBy(css = ".cart-item")
    private WebElement cartItem;

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(cartTotal);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return config.getBaseUrl() + "/cart";
    }

    public CheckoutPage proceedToCheckout() {
        safeClick(checkoutButton);
        return new CheckoutPage(driver);
    }
}