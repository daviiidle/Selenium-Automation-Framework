package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ForgotPasswordPage extends BasePage {

    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(css = "input[value='Recover']")
    private WebElement recoverButton;

    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(emailField);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return config.getBaseUrl() + "/passwordrecovery";
    }
}