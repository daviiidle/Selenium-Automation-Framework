package pages;

import config.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScreenshotUtils;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ConfigurationManager config;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigurationManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitTimeout()));
        PageFactory.initElements(driver, this);
        logger.debug("Initialized {} page", this.getClass().getSimpleName());
    }

    // Wait strategies
    protected WebElement waitForElementToBeVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected List<WebElement> waitForElementsToBeVisible(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    protected boolean waitForElementToBeInvisible(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected WebElement waitForElementToBePresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean waitForTextToBePresent(WebElement element, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    // Enhanced click methods
    protected void safeClick(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            logger.debug("Clicked element: {}", element.toString());
        } catch (Exception e) {
            logger.error("Failed to click element: {}", element.toString(), e);
            clickWithJavaScript(element);
        }
    }

    protected void clickWithJavaScript(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
        logger.debug("Clicked element using JavaScript: {}", element.toString());
    }

    // Enhanced input methods
    protected void safeType(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            logger.debug("Typed '{}' into element: {}", text, element.toString());
        } catch (Exception e) {
            logger.error("Failed to type into element: {}", element.toString(), e);
            throw e;
        }
    }

    protected void typeWithJavaScript(WebElement element, String text) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", element, text);
        logger.debug("Typed '{}' using JavaScript into element: {}", text, element.toString());
    }

    // Text and attribute methods
    protected String getElementText(WebElement element) {
        waitForElementToBeVisible(element);
        String text = element.getText();
        logger.debug("Retrieved text '{}' from element: {}", text, element.toString());
        return text;
    }

    protected String getElementAttribute(WebElement element, String attributeName) {
        waitForElementToBeVisible(element);
        String attributeValue = element.getAttribute(attributeName);
        logger.debug("Retrieved attribute '{}' with value '{}' from element: {}",
                    attributeName, attributeValue, element.toString());
        return attributeValue;
    }

    // Visibility and state checks
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    // Scroll methods
    protected void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: {}", element.toString());
    }

    protected void scrollToTop() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0);");
        logger.debug("Scrolled to top of page");
    }

    protected void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.debug("Scrolled to bottom of page");
    }

    // Page state methods
    protected void waitForPageToLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
        logger.debug("Page loading completed");
    }

    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }

    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }

    // Screenshot utility
    protected void takeScreenshot(String fileName) {
        if (config.isScreenshotOnFailure()) {
            ScreenshotUtils.captureScreenshot(driver, fileName);
        }
    }

    // Abstract methods to be implemented by child classes
    public abstract boolean isPageLoaded();
    public abstract String getPageUrl();
}