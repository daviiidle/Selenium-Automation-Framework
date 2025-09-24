package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductCatalogPage extends BasePage {

    @FindBy(css = ".product-item")
    private List<WebElement> productItems;

    @FindBy(css = ".product-title a")
    private List<WebElement> productTitles;

    @FindBy(css = ".price")
    private List<WebElement> productPrices;

    @FindBy(css = ".add-to-cart-button")
    private List<WebElement> addToCartButtons;

    public ProductCatalogPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementsToBeVisible(productItems);
            return !productItems.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return getCurrentUrl();
    }

    public ProductDetailsPage clickProduct(int index) {
        safeClick(productTitles.get(index));
        return new ProductDetailsPage(driver);
    }
}