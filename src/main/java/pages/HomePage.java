package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class HomePage extends BasePage {

    // Header Elements
    @FindBy(css = ".header-logo a")
    private WebElement logo;

    @FindBy(id = "small-searchterms")
    private WebElement searchBox;

    @FindBy(css = "input[type='submit'][value='Search']")
    private WebElement searchButton;

    @FindBy(css = ".header-links a[href='/login']")
    private WebElement loginLink;

    @FindBy(css = ".header-links a[href='/register']")
    private WebElement registerLink;

    @FindBy(css = ".header-links a[href='/cart']")
    private WebElement shoppingCartLink;

    @FindBy(css = ".header-links a[href='/wishlist']")
    private WebElement wishlistLink;

    // Navigation Menu
    @FindBy(css = ".header-menu .top-menu a")
    private List<WebElement> mainMenuItems;

    @FindBy(css = ".header-menu a[href='/books']")
    private WebElement booksMenu;

    @FindBy(css = ".header-menu a[href='/computers']")
    private WebElement computersMenu;

    @FindBy(css = ".header-menu a[href='/electronics']")
    private WebElement electronicsMenu;

    @FindBy(css = ".header-menu a[href='/apparel-shoes']")
    private WebElement apparelShoesMenu;

    @FindBy(css = ".header-menu a[href='/digital-downloads']")
    private WebElement digitalDownloadsMenu;

    @FindBy(css = ".header-menu a[href='/jewelry']")
    private WebElement jewelryMenu;

    @FindBy(css = ".header-menu a[href='/gift-cards']")
    private WebElement giftCardsMenu;

    // Main Content
    @FindBy(css = ".main-product-img")
    private List<WebElement> featuredProducts;

    @FindBy(css = ".product-item")
    private List<WebElement> productItems;

    @FindBy(css = ".news-list-homepage")
    private WebElement newsSection;

    @FindBy(css = ".poll-block")
    private WebElement pollSection;

    // Footer Elements
    @FindBy(css = ".footer-menu-misc a")
    private List<WebElement> footerLinks;

    @FindBy(css = ".footer .topic-html-content")
    private WebElement footerContent;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        try {
            waitForElementToBeVisible(logo);
            waitForElementToBeVisible(searchBox);
            return isElementDisplayed(logo) && isElementDisplayed(searchBox);
        } catch (Exception e) {
            logger.error("HomePage is not loaded properly", e);
            return false;
        }
    }

    @Override
    public String getPageUrl() {
        return config.getBaseUrl();
    }

    // Navigation actions
    public HomePage navigateToHomePage() {
        driver.get(getPageUrl());
        waitForPageToLoad();
        logger.info("Navigated to HomePage");
        return this;
    }

    public LoginPage clickLoginLink() {
        safeClick(loginLink);
        logger.info("Clicked on Login link");
        return new LoginPage(driver);
    }

    public RegisterPage clickRegisterLink() {
        safeClick(registerLink);
        logger.info("Clicked on Register link");
        return new RegisterPage(driver);
    }

    public ShoppingCartPage clickShoppingCartLink() {
        safeClick(shoppingCartLink);
        logger.info("Clicked on Shopping Cart link");
        return new ShoppingCartPage(driver);
    }

    // Search functionality
    public SearchResultsPage searchForProduct(String searchTerm) {
        safeType(searchBox, searchTerm);
        safeClick(searchButton);
        logger.info("Searched for product: {}", searchTerm);
        return new SearchResultsPage(driver);
    }

    public HomePage enterSearchTerm(String searchTerm) {
        safeType(searchBox, searchTerm);
        logger.info("Entered search term: {}", searchTerm);
        return this;
    }

    public SearchResultsPage clickSearchButton() {
        safeClick(searchButton);
        logger.info("Clicked search button");
        return new SearchResultsPage(driver);
    }

    // Category navigation
    public ProductCatalogPage navigateToBooks() {
        safeClick(booksMenu);
        logger.info("Navigated to Books category");
        return new ProductCatalogPage(driver);
    }

    public ProductCatalogPage navigateToComputers() {
        safeClick(computersMenu);
        logger.info("Navigated to Computers category");
        return new ProductCatalogPage(driver);
    }

    public ProductCatalogPage navigateToElectronics() {
        safeClick(electronicsMenu);
        logger.info("Navigated to Electronics category");
        return new ProductCatalogPage(driver);
    }

    public ProductCatalogPage navigateToApparelShoes() {
        safeClick(apparelShoesMenu);
        logger.info("Navigated to Apparel & Shoes category");
        return new ProductCatalogPage(driver);
    }

    public ProductCatalogPage navigateToDigitalDownloads() {
        safeClick(digitalDownloadsMenu);
        logger.info("Navigated to Digital Downloads category");
        return new ProductCatalogPage(driver);
    }

    public ProductCatalogPage navigateToJewelry() {
        safeClick(jewelryMenu);
        logger.info("Navigated to Jewelry category");
        return new ProductCatalogPage(driver);
    }

    public ProductCatalogPage navigateToGiftCards() {
        safeClick(giftCardsMenu);
        logger.info("Navigated to Gift Cards category");
        return new ProductCatalogPage(driver);
    }

    // Product interactions
    public ProductDetailsPage clickFeaturedProduct(int index) {
        if (index >= 0 && index < featuredProducts.size()) {
            safeClick(featuredProducts.get(index));
            logger.info("Clicked on featured product at index: {}", index);
            return new ProductDetailsPage(driver);
        } else {
            logger.error("Invalid featured product index: {}", index);
            throw new IndexOutOfBoundsException("Featured product index out of bounds: " + index);
        }
    }

    public ProductDetailsPage clickProductItem(int index) {
        if (index >= 0 && index < productItems.size()) {
            safeClick(productItems.get(index));
            logger.info("Clicked on product item at index: {}", index);
            return new ProductDetailsPage(driver);
        } else {
            logger.error("Invalid product item index: {}", index);
            throw new IndexOutOfBoundsException("Product item index out of bounds: " + index);
        }
    }

    // Getters for validation
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public boolean isLoginLinkDisplayed() {
        return isElementDisplayed(loginLink);
    }

    public boolean isRegisterLinkDisplayed() {
        return isElementDisplayed(registerLink);
    }

    public boolean isShoppingCartLinkDisplayed() {
        return isElementDisplayed(shoppingCartLink);
    }

    public boolean isSearchBoxDisplayed() {
        return isElementDisplayed(searchBox);
    }

    public int getFeaturedProductsCount() {
        return featuredProducts.size();
    }

    public int getProductItemsCount() {
        return productItems.size();
    }

    public List<String> getMainMenuItemsText() {
        return mainMenuItems.stream()
                .map(this::getElementText)
                .collect(java.util.stream.Collectors.toList());
    }
}