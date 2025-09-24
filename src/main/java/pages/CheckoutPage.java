package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutPage extends BasePage {

    @FindBy(id = "checkout-step-billing")
    private WebElement billingSection;

    @FindBy(css = ".order-summary")
    private WebElement orderSummary;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(billingSection);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return config.getBaseUrl() + "/checkout";
    }
}