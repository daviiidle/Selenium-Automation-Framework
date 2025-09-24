package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProductDetailsPage extends BasePage {

    @FindBy(css = ".product-name")
    private WebElement productName;

    @FindBy(css = ".price-value-1")
    private WebElement productPrice;

    @FindBy(id = "add-to-cart-button-1")
    private WebElement addToCartButton;

    @FindBy(id = "product_enteredQuantity_1")
    private WebElement quantityField;

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(productName);
            return isElementDisplayed(addToCartButton);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return getCurrentUrl();
    }

    public ProductDetailsPage setQuantity(int quantity) {
        quantityField.clear();
        quantityField.sendKeys(String.valueOf(quantity));
        return this;
    }

    public ShoppingCartPage addToCart() {
        safeClick(addToCartButton);
        return new ShoppingCartPage(driver);
    }
}