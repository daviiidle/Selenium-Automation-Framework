package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SearchResultsPage extends BasePage {

    @FindBy(css = ".search-results")
    private WebElement searchResults;

    @FindBy(css = ".product-item")
    private List<WebElement> productItems;

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(searchResults);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return getCurrentUrl();
    }

    public int getProductCount() {
        return productItems.size();
    }
}