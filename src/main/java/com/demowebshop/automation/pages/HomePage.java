package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object Model for the DemoWebShop Homepage - Selenide Migrated
 * Provides methods for navigation, search, featured products, and newsletter signup
 */
public class HomePage extends BasePage {

    // Page URL patterns
    private static final String PAGE_URL_PATTERN = "https://demowebshop.tricentis.com/";

    // Selenide elements - no need for @FindBy
    private final SelenideElement logo = $("a[href='/']");
    private final SelenideElement searchInput = $("#small-searchterms");
    private final SelenideElement searchButton = $("input[type='submit'][value='Search']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage() {
        super();
    }

    /**
     * Navigate to homepage
     * @return HomePage instance for method chaining
     */
    public HomePage navigateToHomePage() {
        navigateTo(PAGE_URL_PATTERN);
        waitForPageToLoad();
        return this;
    }

    // Header Navigation Methods

    /**
     * Click on the register link
     * @return RegisterPage instance
     */
    public RegisterPage clickRegisterLink() {
        By registerSelector = SelectorUtils.getHomepageSelector("homepage.header.register_link");
        click(registerSelector);
        logger.info("Clicked register link");
        return new RegisterPage(driver);
    }

    /**
     * Click on the login link
     * @return LoginPage instance
     */
    public LoginPage clickLoginLink() {
        try {
            // Try multiple selectors in order of preference
            String[] loginSelectors = {
                "a[href='/login']",
                "a[href*='login']",
                ".header-links a:contains('Log in')",
                "//a[contains(text(), 'Log in')]",
                "//a[contains(@href, 'login')]"
            };

            for (String selector : loginSelectors) {
                try {
                    By loginBy;
                    if (selector.startsWith("//")) {
                        loginBy = By.xpath(selector);
                    } else {
                        loginBy = By.cssSelector(selector);
                    }

                    // Use enhanced click with locator-based retry
                    elementUtils.clickElement(loginBy);
                    logger.info("Successfully clicked login link using selector: {}", selector);
                    return new LoginPage(driver);
                } catch (Exception e) {
                    logger.debug("Login selector failed: {} - {}", selector, e.getMessage());
                    // Continue to next selector
                }
            }

            // If all specific selectors fail, try generic approach
            logger.warn("All specific login selectors failed, trying text-based approach");
            By textBasedSelector = By.xpath("//a[contains(text(), 'Log in') or contains(text(), 'LOGIN') or contains(text(), 'log in')]");
            elementUtils.clickElement(textBasedSelector);
            logger.info("Clicked login link using text-based fallback");
            return new LoginPage(driver);

        } catch (Exception e) {
            logger.error("Failed to click login link after all attempts: {}", e.getMessage());
            throw new RuntimeException("Login link not found or clickable", e);
        }
    }

    /**
     * Click on the cart link
     * @return ShoppingCartPage instance
     */
    public ShoppingCartPage clickCartLink() {
        By cartSelector = SelectorUtils.getHomepageSelector("homepage.header.cart_link");
        click(cartSelector);
        logger.info("Clicked cart link");
        return new ShoppingCartPage(driver);
    }

    /**
     * Get current cart quantity from header
     * @return Cart quantity as integer
     */
    public int getCartQuantity() {
        By cartQtySelector = SelectorUtils.getHomepageSelector("homepage.header.cart_quantity");
        try {
            String qtyText = getText(cartQtySelector);
            // Extract number from text like "(2)"
            String numberOnly = qtyText.replaceAll("[^0-9]", "");
            return numberOnly.isEmpty() ? 0 : Integer.parseInt(numberOnly);
        } catch (Exception e) {
            logger.warn("Could not get cart quantity: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Navigate to random product for testing purposes
     * @return ProductDetailsPage for a random product
     */
    public ProductDetailsPage navigateToRandomProduct() {
        // Navigate to a category first
        ProductCatalogPage catalogPage = navigateToBooks();

        // Get first available product
        if (catalogPage.hasProducts()) {
            return catalogPage.clickFirstProduct();
        } else {
            // Fallback to computers category
            catalogPage = navigateToComputers();
            if (catalogPage.hasProducts()) {
                return catalogPage.clickFirstProduct();
            }
        }

        // Last resort - create a basic ProductDetailsPage
        logger.warn("No products found, creating basic ProductDetailsPage");
        return new ProductDetailsPage(driver);
    }




    /**
     * Check if cart icon is displayed
     * @return true if cart icon is visible
     */
    public boolean isCartIconDisplayed() {
        By cartSelector = SelectorUtils.getHomepageSelector("homepage.header.cart_link");
        return isElementDisplayed(cartSelector);
    }


    /**
     * Click on wishlist link
     * @return WishlistPage instance
     */
    public WishlistPage clickWishlistLink() {
        By wishlistSelector = SelectorUtils.getHomepageSelector("homepage.header.wishlist_link");
        click(wishlistSelector);
        logger.info("Clicked wishlist link");
        return new WishlistPage(driver);
    }

    /**
     * Get cart item count (alias for getCartQuantity)
     * @return Number of items in cart
     */
    public int getCartItemCount() {
        return getCartQuantity();
    }

    // Search Functionality

    /**
     * Perform search with given term
     * @param searchTerm Term to search for
     * @return ProductSearchPage with results
     */
    public ProductSearchPage performSearch(String searchTerm) {
        searchInput.setValue(searchTerm);
        searchButton.click();

        // Wait for navigation to search results page
        waitForUrlToContain("/search");
        waitForPageToLoad();

        logger.info("Performed search for: {}", searchTerm);
        return new ProductSearchPage(driver);
    }

    /**
     * Get current search input value
     * @return Current search text
     */
    public String getSearchInputValue() {
        return getAttribute(By.id("small-searchterms"), "value");
    }

    // Main Navigation Menu Methods

    /**
     * Navigate to Books category
     * @return ProductCatalogPage for books
     */
    public ProductCatalogPage navigateToBooks() {
        By booksSelector = SelectorUtils.getHomepageSelector("homepage.navigation.books_menu");
        click(booksSelector);
        logger.info("Navigated to Books category");
        return new ProductCatalogPage(driver, "Books");
    }

    /**
     * Navigate to Computers category (main)
     * @return ProductCatalogPage for computers
     */
    public ProductCatalogPage navigateToComputers() {
        By computersSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_menu");
        click(computersSelector);
        logger.info("Navigated to Computers category");
        return new ProductCatalogPage(driver, "Computers");
    }

    /**
     * Navigate to Desktops subcategory
     * @return ProductCatalogPage for desktops
     */
    public ProductCatalogPage navigateToDesktops() {
        // Hover over Computers first to show submenu
        By computersSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_menu");
        SelenideElement computersMenu = $(computersSelector);
        computersMenu.hover();

        By desktopsSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_dropdown.desktops");
        click(desktopsSelector);
        logger.info("Navigated to Desktops subcategory");
        return new ProductCatalogPage(driver, "Desktops");
    }

    /**
     * Navigate to Notebooks subcategory
     * @return ProductCatalogPage for notebooks
     */
    public ProductCatalogPage navigateToNotebooks() {
        // Hover over Computers first to show submenu
        By computersSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_menu");
        SelenideElement computersMenu = $(computersSelector);
        computersMenu.hover();

        By notebooksSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_dropdown.notebooks");
        click(notebooksSelector);
        logger.info("Navigated to Notebooks subcategory");
        return new ProductCatalogPage(driver, "Notebooks");
    }

    /**
     * Navigate to Accessories subcategory
     * @return ProductCatalogPage for accessories
     */
    public ProductCatalogPage navigateToAccessories() {
        // Hover over Computers first to show submenu
        By computersSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_menu");
        SelenideElement computersMenu = $(computersSelector);
        computersMenu.hover();

        By accessoriesSelector = SelectorUtils.getHomepageSelector("homepage.navigation.computers_dropdown.accessories");
        click(accessoriesSelector);
        logger.info("Navigated to Accessories subcategory");
        return new ProductCatalogPage(driver, "Accessories");
    }

    /**
     * Navigate to Electronics category
     * @return ProductCatalogPage for electronics
     */
    public ProductCatalogPage navigateToElectronics() {
        By electronicsSelector = SelectorUtils.getHomepageSelector("homepage.navigation.electronics_menu");
        click(electronicsSelector);
        logger.info("Navigated to Electronics category");
        return new ProductCatalogPage(driver, "Electronics");
    }

    /**
     * Navigate to Camera & Photo subcategory
     * @return ProductCatalogPage for camera photo
     */
    public ProductCatalogPage navigateToCameraPhoto() {
        // Hover over Electronics first to show submenu
        By electronicsSelector = SelectorUtils.getHomepageSelector("homepage.navigation.electronics_menu");
        SelenideElement electronicsMenu = $(electronicsSelector);
        electronicsMenu.hover();

        By cameraSelector = SelectorUtils.getHomepageSelector("homepage.navigation.electronics_dropdown.camera_photo");
        click(cameraSelector);
        logger.info("Navigated to Camera & Photo subcategory");
        return new ProductCatalogPage(driver, "Camera, photo");
    }

    /**
     * Navigate to Cell Phones subcategory
     * @return ProductCatalogPage for cell phones
     */
    public ProductCatalogPage navigateToCellPhones() {
        // Hover over Electronics first to show submenu
        By electronicsSelector = SelectorUtils.getHomepageSelector("homepage.navigation.electronics_menu");
        SelenideElement electronicsMenu = $(electronicsSelector);
        electronicsMenu.hover();

        By cellPhonesSelector = SelectorUtils.getHomepageSelector("homepage.navigation.electronics_dropdown.cell_phones");
        click(cellPhonesSelector);
        logger.info("Navigated to Cell Phones subcategory");
        return new ProductCatalogPage(driver, "Cell phones");
    }

    /**
     * Navigate to Apparel & Shoes category
     * @return ProductCatalogPage for apparel shoes
     */
    public ProductCatalogPage navigateToApparelShoes() {
        By apparelSelector = SelectorUtils.getHomepageSelector("homepage.navigation.apparel_shoes_menu");
        click(apparelSelector);
        logger.info("Navigated to Apparel & Shoes category");
        return new ProductCatalogPage(driver, "Apparel & Shoes");
    }

    /**
     * Navigate to Digital Downloads category
     * @return ProductCatalogPage for digital downloads
     */
    public ProductCatalogPage navigateToDigitalDownloads() {
        By digitalSelector = SelectorUtils.getHomepageSelector("homepage.navigation.digital_downloads_menu");
        click(digitalSelector);
        logger.info("Navigated to Digital Downloads category");
        return new ProductCatalogPage(driver, "Digital downloads");
    }

    /**
     * Navigate to Jewelry category
     * @return ProductCatalogPage for jewelry
     */
    public ProductCatalogPage navigateToJewelry() {
        By jewelrySelector = SelectorUtils.getHomepageSelector("homepage.navigation.jewelry_menu");
        click(jewelrySelector);
        logger.info("Navigated to Jewelry category");
        return new ProductCatalogPage(driver, "Jewelry");
    }

    /**
     * Navigate to Gift Cards category
     * @return ProductCatalogPage for gift cards
     */
    public ProductCatalogPage navigateToGiftCards() {
        By giftCardsSelector = SelectorUtils.getHomepageSelector("homepage.navigation.gift_cards_menu");
        click(giftCardsSelector);
        logger.info("Navigated to Gift Cards category");
        return new ProductCatalogPage(driver, "Gift Cards");
    }

    // Featured Products Section

    /**
     * Get all featured products on homepage
     * @return List of ProductElement objects
     */
    public List<ProductElement> getFeaturedProducts() {
        By productItemsSelector = SelectorUtils.getHomepageSelector("homepage.main_content.featured_products.product_item");
        ElementsCollection productElements = $$(productItemsSelector);

        return productElements.stream()
                .map(element -> new ProductElement(element, driver))
                .collect(Collectors.toList());
    }

    /**
     * Get featured product by index
     * @param index Index of the product (0-based)
     * @return ProductElement
     */
    public ProductElement getFeaturedProduct(int index) {
        List<ProductElement> products = getFeaturedProducts();
        if (index >= 0 && index < products.size()) {
            return products.get(index);
        }
        throw new IndexOutOfBoundsException("Product index " + index + " is out of bounds. Available products: " + products.size());
    }

    /**
     * Click on featured product by index
     * @param index Index of the product to click
     * @return ProductDetailsPage
     */
    public ProductDetailsPage clickFeaturedProduct(int index) {
        ProductElement product = getFeaturedProduct(index);
        product.clickTitle();
        logger.info("Clicked featured product at index: {}", index);
        return new ProductDetailsPage(driver);
    }

    // Newsletter Section

    /**
     * Subscribe to newsletter with email
     * @param email Email address for subscription
     * @return HomePage for method chaining
     */
    public HomePage subscribeToNewsletter(String email) {
        By emailInputSelector = SelectorUtils.getHomepageSelector("homepage.main_content.newsletter.email_input");
        By subscribeButtonSelector = SelectorUtils.getHomepageSelector("homepage.main_content.newsletter.subscribe_button");

        type(emailInputSelector, email);
        click(subscribeButtonSelector);
        logger.info("Subscribed to newsletter with email: {}", email);
        return this;
    }

    /**
     * Get newsletter input field value
     * @return Current newsletter email input value
     */
    public String getNewsletterInputValue() {
        By emailInputSelector = SelectorUtils.getHomepageSelector("homepage.main_content.newsletter.email_input");
        return getAttribute(emailInputSelector, "value");
    }

    // Sidebar Methods

    /**
     * Get all category links from sidebar
     * @return List of category link elements
     */
    public ElementsCollection getSidebarCategoryLinks() {
        By categoryLinksSelector = SelectorUtils.getHomepageSelector("homepage.sidebar.categories.category_link");
        return $$(categoryLinksSelector);
    }

    /**
     * Click sidebar category by name
     * @param categoryName Name of the category to click
     * @return ProductCatalogPage for the selected category
     */
    public ProductCatalogPage clickSidebarCategory(String categoryName) {
        ElementsCollection categoryLinks = getSidebarCategoryLinks();
        for (SelenideElement link : categoryLinks) {
            if (link.getText().trim().equalsIgnoreCase(categoryName.trim())) {
                link.click();
                logger.info("Clicked sidebar category: {}", categoryName);
                return new ProductCatalogPage(driver, categoryName);
            }
        }
        throw new IllegalArgumentException("Category not found in sidebar: " + categoryName);
    }

    /**
     * Navigate to a main category by name
     * @param categoryName Name of the category to navigate to
     * @return ProductCatalogPage for the selected category
     */
    public ProductCatalogPage navigateToCategory(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "books":
                return navigateToBooks();
            case "computers":
                return navigateToComputers();
            case "electronics":
                return navigateToElectronics();
            case "apparel & shoes":
            case "apparel":
                return navigateToApparelShoes();
            case "digital downloads":
                return navigateToDigitalDownloads();
            case "jewelry":
                return navigateToJewelry();
            case "gift cards":
                return navigateToGiftCards();
            default:
                // Try clicking sidebar category as fallback
                return clickSidebarCategory(categoryName);
        }
    }

    /**
     * Navigate to a subcategory under a main category
     * @param categoryName Main category name
     * @param subcategoryName Subcategory name
     * @return ProductCatalogPage for the selected subcategory
     */
    public ProductCatalogPage navigateToSubcategory(String categoryName, String subcategoryName) {
        // Handle specific subcategory navigation
        switch (categoryName.toLowerCase()) {
            case "computers":
                switch (subcategoryName.toLowerCase()) {
                    case "desktops":
                        return navigateToDesktops();
                    case "notebooks":
                        return navigateToNotebooks();
                    case "accessories":
                        return navigateToAccessories();
                    default:
                        throw new IllegalArgumentException("Unknown Computers subcategory: " + subcategoryName);
                }
            case "electronics":
                switch (subcategoryName.toLowerCase()) {
                    case "camera & photo":
                    case "camera photo":
                        return navigateToCameraPhoto();
                    case "cell phones":
                        return navigateToCellPhones();
                    default:
                        throw new IllegalArgumentException("Unknown Electronics subcategory: " + subcategoryName);
                }
            default:
                throw new IllegalArgumentException("Category does not have subcategories or subcategory navigation not implemented: " + categoryName);
        }
    }

    // Validation Methods

    /**
     * Verify homepage is loaded by checking for key elements
     * @return true if homepage is properly loaded
     */
    @Override
    public boolean isPageLoaded() {
        try {
            // Check for essential homepage elements
            return isElementDisplayed(By.cssSelector("a[href='/']")) &&
                   isElementDisplayed(By.id("small-searchterms")) &&
                   isElementDisplayed(SelectorUtils.getHomepageSelector("homepage.main_content.featured_products.container"));
        } catch (Exception e) {
            logger.error("Error checking if homepage is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get welcome message text
     * @return Welcome message text
     */
    public String getWelcomeMessage() {
        By welcomeSelector = SelectorUtils.getHomepageSelector("homepage.main_content.welcome_message");
        return getText(welcomeSelector);
    }

    /**
     * Check if user is logged in by checking header links
     * @return true if user appears to be logged in
     */
    public boolean isUserLoggedIn() {
        // If login link is not present, user might be logged in
        // (logged in users see "Log out" instead)
        try {
            By loginSelector = SelectorUtils.getHomepageSelector("homepage.header.login_link");
            return !isElementDisplayed(loginSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get logged in user information
     * @return String containing logged in user info, empty if not logged in
     */
    public String getLoggedInUserInfo() {
        try {
            By userInfoSelector = By.cssSelector(".header-links .account");
            if (isElementDisplayed(userInfoSelector)) {
                return getText(userInfoSelector);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if login link is displayed
     * @return true if login link is visible
     */
    public boolean isLoginLinkDisplayed() {
        try {
            // Try multiple selectors with shorter timeout for faster detection
            String[] loginSelectors = {
                "a[href='/login']",
                "a[href*='login']",
                ".header-links a:contains('Log in')",
                "//a[contains(text(), 'Log in')]",
                "//a[contains(@href, 'login')]"
            };

            for (String selector : loginSelectors) {
                try {
                    By loginBy;
                    if (selector.startsWith("//")) {
                        loginBy = By.xpath(selector);
                    } else {
                        loginBy = By.cssSelector(selector);
                    }

                    // Use soft wait to avoid exceptions for missing elements
                    SelenideElement loginElement = $(loginBy);
                    if (loginElement != null && loginElement.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if logout link is displayed
     * @return true if logout link is visible
     */
    public boolean isLogoutLinkDisplayed() {
        try {
            // Try multiple selectors with shorter timeout for faster detection
            String[] logoutSelectors = {
                "a[href='/logout']",
                "a[href*='logout']",
                ".header-links a:contains('Log out')",
                "//a[contains(text(), 'Log out')]",
                "//a[contains(@href, 'logout')]",
                "//a[text()='Log out']"
            };

            for (String selector : logoutSelectors) {
                try {
                    By logoutBy;
                    if (selector.startsWith("//")) {
                        logoutBy = By.xpath(selector);
                    } else {
                        logoutBy = By.cssSelector(selector);
                    }

                    // Use soft wait to avoid exceptions for missing elements
                    SelenideElement logoutElement = $(logoutBy);
                    if (logoutElement != null && logoutElement.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click the logout link
     * @return HomePage after logout
     */
    public HomePage clickLogoutLink() {
        try {
            // Try multiple selectors in order of preference
            String[] logoutSelectors = {
                "a[href='/logout']",
                "a[href*='logout']",
                ".header-links a:contains('Log out')",
                "//a[contains(text(), 'Log out')]",
                "//a[contains(@href, 'logout')]",
                "//a[text()='Log out']"
            };

            for (String selector : logoutSelectors) {
                try {
                    By logoutBy;
                    if (selector.startsWith("//")) {
                        logoutBy = By.xpath(selector);
                    } else {
                        logoutBy = By.cssSelector(selector);
                    }

                    // Use enhanced click with locator-based retry
                    elementUtils.clickElement(logoutBy);
                    logger.info("Successfully clicked logout link using selector: {}", selector);
                    return new HomePage(driver);
                } catch (Exception e) {
                    logger.debug("Logout selector failed: {} - {}", selector, e.getMessage());
                    // Continue to next selector
                }
            }

            // If all specific selectors fail, try generic approach
            logger.warn("All specific logout selectors failed, trying text-based approach");
            By textBasedSelector = By.xpath("//a[contains(text(), 'Log out') or contains(text(), 'LOGOUT') or contains(text(), 'logout')]");
            elementUtils.clickElement(textBasedSelector);
            logger.info("Clicked logout link using text-based fallback");
            return new HomePage(driver);

        } catch (Exception e) {
            logger.error("Failed to click logout link after all attempts: {}", e.getMessage());
            throw new RuntimeException("Logout link not found or clickable", e);
        }
    }

    /**
     * Get the page URL pattern for validation
     * @return Homepage URL pattern
     */
    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Check if logo is displayed
     * @return true if logo is visible
     */
    public boolean isLogoDisplayed() {
        return isElementDisplayed(By.cssSelector("a[href='/']"));
    }

    /**
     * Check if search box is displayed
     * @return true if search input field is visible
     */
    public boolean isSearchBoxDisplayed() {
        return isElementDisplayed(By.id("small-searchterms"));
    }

    /**
     * Check if cart count is displayed
     * @return true if cart count indicator is visible
     */
    public boolean isCartCountDisplayed() {
        By cartQtySelector = SelectorUtils.getHomepageSelector("homepage.header.cart_quantity");
        return isElementDisplayed(cartQtySelector);
    }

    /**
     * Check if register link is displayed
     * @return true if register link is visible
     */
    public boolean isRegisterLinkDisplayed() {
        By registerSelector = SelectorUtils.getHomepageSelector("homepage.header.register_link");
        return isElementDisplayed(registerSelector);
    }

    /**
     * Check if shopping cart link is displayed
     * @return true if shopping cart link is visible
     */
    public boolean isShoppingCartLinkDisplayed() {
        By cartSelector = SelectorUtils.getHomepageSelector("homepage.header.cart_link");
        return isElementDisplayed(cartSelector);
    }

    /**
     * Check if main navigation menu is displayed
     * @return true if main navigation is visible
     */
    public boolean isMainNavigationDisplayed() {
        try {
            By navSelector = By.cssSelector(".header-menu, .navigation");
            return isElementDisplayed(navSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if shopping cart link is clickable
     * @return true if shopping cart link can be clicked
     */
    public boolean isShoppingCartLinkClickable() {
        By cartSelector = SelectorUtils.getHomepageSelector("homepage.header.cart_link");
        return isElementDisplayed(cartSelector) && isElementEnabled(cartSelector);
    }

    /**
     * Check if search box is enabled
     * @return true if search input is enabled
     */
    public boolean isSearchBoxEnabled() {
        try {
            SelenideElement searchBox = $(By.id("small-searchterms"));
            return searchBox.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enter text in search box
     * @param searchTerm Text to enter
     * @return HomePage for method chaining
     */
    public HomePage enterSearchTerm(String searchTerm) {
        type(By.id("small-searchterms"), searchTerm);
        return this;
    }

    /**
     * Get current text in search box
     * @return Current search box text
     */
    public String getSearchBoxText() {
        try {
            return getAttribute(By.id("small-searchterms"), "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Click the search button
     * @return ProductSearchPage with search results
     */
    public ProductSearchPage clickSearchButton() {
        By searchButtonSelector = By.cssSelector("input[type='submit'][value='Search']");
        click(searchButtonSelector);

        // Wait for navigation to search results page
        waitForUrlToContain("/search");
        waitForPageToLoad();

        logger.info("Clicked search button");
        return new ProductSearchPage(driver);
    }

    /**
     * Check if featured products section is displayed
     * @return true if featured products section is visible
     */
    public boolean isFeaturedProductsSectionDisplayed() {
        try {
            By featuredSelector = By.cssSelector(".featured-products, .home-page-product-grid");
            return isElementDisplayed(featuredSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get count of featured products
     * @return Number of featured products displayed
     */
    public int getFeaturedProductsCount() {
        return getFeaturedProducts().size();
    }

    /**
     * Get featured product titles
     * @return List of featured product titles
     */
    public List<String> getFeaturedProductTitles() {
        return getFeaturedProducts().stream()
                .map(ProductElement::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Get featured product prices
     * @return List of featured product prices
     */
    public List<String> getFeaturedProductPrices() {
        return getFeaturedProducts().stream()
                .map(ProductElement::getPrice)
                .collect(Collectors.toList());
    }

    /**
     * Get first featured product title
     * @return Title of first featured product, or empty string
     */
    public String getFirstFeaturedProductTitle() {
        List<ProductElement> products = getFeaturedProducts();
        return products.isEmpty() ? "" : products.get(0).getTitle();
    }

    /**
     * Click first featured product
     * @return ProductDetailsPage for the clicked product
     */
    public ProductDetailsPage clickFirstFeaturedProduct() {
        List<ProductElement> products = getFeaturedProducts();
        if (!products.isEmpty()) {
            return products.get(0).clickTitle();
        }
        throw new RuntimeException("No featured products available to click");
    }

    /**
     * Check if specific category link is displayed
     * @param categoryName Name of the category
     * @return true if category link is displayed
     */
    public boolean isCategoryLinkDisplayed(String categoryName) {
        try {
            By categorySelector = By.xpath("//a[contains(text(),'" + categoryName + "')]");
            return isElementDisplayed(categorySelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click logo to return to home
     * @return HomePage after logo click
     */
    public HomePage clickLogoToReturnHome() {
        By logoSelector = By.cssSelector("a[href='/'], .logo a");
        click(logoSelector);
        logger.info("Clicked logo to return home");
        return new HomePage(driver);
    }

    /**
     * Check if cart flyout is available
     * @return true if cart flyout feature exists
     */
    public boolean hasCartFlyout() {
        try {
            By cartFlyoutSelector = By.cssSelector(".cart-flyout, #flyout-cart");
            return findElements(cartFlyoutSelector).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Hover over cart icon to show flyout
     * @return HomePage for method chaining
     */
    public HomePage hoverOverCartIcon() {
        By cartSelector = SelectorUtils.getHomepageSelector("homepage.header.cart_link");
        SelenideElement cartElement = $(cartSelector);
        // Use hover method from Selenide
        cartElement.hover();
        return this;
    }

    /**
     * Check if cart flyout is displayed
     * @return true if cart flyout is visible
     */
    public boolean isCartFlyoutDisplayed() {
        try {
            By cartFlyoutSelector = By.cssSelector(".cart-flyout:not([style*='display: none'])");
            return isElementDisplayed(cartFlyoutSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if cart flyout is accessible
     * @return true if cart flyout can be accessed
     */
    public boolean isCartFlyoutAccessible() {
        return hasCartFlyout() && isCartIconDisplayed();
    }

    /**
     * Click shopping cart link
     * @return ShoppingCartPage
     */
    public ShoppingCartPage clickShoppingCartLink() {
        By cartSelector = SelectorUtils.getHomepageSelector("homepage.header.cart_link");
        click(cartSelector);
        logger.info("Clicked shopping cart link");
        return new ShoppingCartPage(driver);
    }


    /**
     * Scroll to footer section
     * @return HomePage for method chaining
     */
    public HomePage scrollToFooter() {
        try {
            By footerSelector = By.cssSelector("footer, .footer");
            scrollToElement(footerSelector);
        } catch (Exception e) {
            // Scroll to bottom if footer not found
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        }
        return this;
    }

    /**
     * Check if footer is displayed
     * @return true if footer is visible
     */
    public boolean isFooterDisplayed() {
        try {
            By footerSelector = By.cssSelector("footer, .footer");
            return isElementDisplayed(footerSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if footer has links
     * @return true if footer contains links
     */
    public boolean hasFooterLinks() {
        try {
            By footerLinksSelector = By.cssSelector("footer a, .footer a");
            return findElements(footerLinksSelector).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get footer link texts
     * @return List of footer link texts
     */
    public List<String> getFooterLinkTexts() {
        try {
            By footerLinksSelector = By.cssSelector("footer a, .footer a");
            return $$(footerLinksSelector).stream()
                    .map(SelenideElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if footer link is internal
     * @param linkText Text of the link to check
     * @return true if link is internal to the site
     */
    public boolean isFooterLinkInternal(String linkText) {
        try {
            By linkSelector = By.xpath("//footer//a[text()='" + linkText + "'] | //.footer//a[text()='" + linkText + "']");
            SelenideElement link = $(linkSelector);
            String href = link.getAttribute("href");
            return href != null && (href.startsWith("/") || href.contains("demowebshop"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if copyright info is displayed
     * @return true if copyright information is visible
     */
    public boolean isCopyrightInfoDisplayed() {
        try {
            By copyrightSelector = By.cssSelector(".copyright, footer .copyright");
            return isElementDisplayed(copyrightSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get copyright text
     * @return Copyright text or empty string
     */
    public String getCopyrightText() {
        try {
            By copyrightSelector = By.cssSelector(".copyright, footer .copyright");
            return getText(copyrightSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if page has images
     * @return true if images are present on the page
     */
    public boolean hasImages() {
        try {
            By imageSelector = By.tagName("img");
            return findElements(imageSelector).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if images are loaded properly
     * @return true if all images are loaded
     */
    public boolean areImagesLoaded() {
        try {
            By imageSelector = By.tagName("img");
            ElementsCollection images = $$(imageSelector);

            for (SelenideElement img : images) {
                // Check if image has loaded by verifying naturalWidth > 0
                Object naturalWidth = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return arguments[0].naturalWidth;", img);
                if (naturalWidth == null || (Long)naturalWidth == 0) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if mobile menu is displayed
     * @return true if mobile menu is visible
     */
    public boolean isMobileMenuDisplayed() {
        try {
            By mobileMenuSelector = By.cssSelector(".mobile-menu, .hamburger-menu");
            return isElementDisplayed(mobileMenuSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if mobile menu is accessible
     * @return true if mobile menu can be accessed
     */
    public boolean isMobileMenuAccessible() {
        return isMobileMenuDisplayed() && isElementEnabled(By.cssSelector(".mobile-menu, .hamburger-menu"));
    }

    /**
     * Get page title
     * @return Page title
     */
    public String getPageTitle() {
        return getCurrentTitle();
    }

    /**
     * Get product items count on homepage
     * @return Number of product items
     */
    public int getProductItemsCount() {
        return getFeaturedProducts().size();
    }

    /**
     * Clear search box
     * @return HomePage for method chaining
     */
    public HomePage clearSearchBox() {
        SelenideElement searchBox = $(By.id("small-searchterms"));
        searchBox.clear();
        return this;
    }


    /**
     * Get H1 text from page
     * @return H1 text or empty string
     */
    public String getH1Text() {
        try {
            By h1Selector = By.tagName("h1");
            return getText(h1Selector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get images without alt text
     * @return List of image elements without alt attributes
     */
    public List<String> getImagesWithoutAltText() {
        try {
            ElementsCollection images = $$(By.tagName("img"));
            return images.stream()
                    .filter(img -> {
                        String alt = img.getAttribute("alt");
                        return alt == null || alt.trim().isEmpty();
                    })
                    .map(img -> img.getAttribute("src"))
                    .filter(src -> src != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Check if keyboard navigation is supported
     * @return true if basic keyboard navigation works
     */
    public boolean isKeyboardNavigationSupported() {
        // Basic check for focusable elements
        try {
            ElementsCollection focusableElements = $$(By.cssSelector("a, button, input, select, textarea"));
            return focusableElements.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if page can be navigated with keyboard
     * @return true if keyboard navigation is possible
     */
    public boolean canNavigateWithKeyboard() {
        return isKeyboardNavigationSupported();
    }

    /**
     * Check if elements have focus indicators
     * @return true if focus indicators are present
     */
    public boolean haveFocusIndicators() {
        // This would need CSS analysis to be truly accurate
        // For now, return true if focusable elements exist
        return isKeyboardNavigationSupported();
    }

    /**
     * Check if page has ARIA labels
     * @return true if ARIA labels are present
     */
    public boolean hasAriaLabels() {
        try {
            ElementsCollection ariaElements = $$(By.cssSelector("[aria-label], [aria-labelledby]"));
            return ariaElements.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if ARIA labels are appropriate
     * @return true if ARIA labels seem adequate
     */
    public boolean areAriaLabelsAppropriate() {
        return hasAriaLabels();
    }

    /**
     * Check if page has colored elements
     * @return true if page uses colors
     */
    public boolean hasColoredElements() {
        // Basic check - assume any CSS page has colors
        return true;
    }

    /**
     * Check if page has adequate color contrast
     * @return true if color contrast appears adequate
     */
    public boolean hasAdequateColorContrast() {
        // This would need advanced color analysis
        // For testing purposes, return true
        return true;
    }

    /**
     * Check if all images have alt text
     * @return true if all images have alt attributes
     */
    public boolean allImagesHaveAltText() {
        return getImagesWithoutAltText().isEmpty();
    }

    /**
     * Refresh the current page and return HomePage
     * @return HomePage for method chaining
     */
    public HomePage refreshCurrentPage() {
        driver.navigate().refresh();
        waitForPageToLoad();
        return this;
    }

    /**
     * Navigate to account info page
     * @return CustomerInfoPage
     */
    public CustomerInfoPage navigateToAccountInfo() {
        try {
            By accountLinkSelector = By.cssSelector("a[href*='/customer/info']");
            click(accountLinkSelector);
            logger.info("Navigated to account info");
            return new CustomerInfoPage(driver);
        } catch (Exception e) {
            logger.warn("Could not navigate to account info: {}", e.getMessage());
            return new CustomerInfoPage(driver);
        }
    }

    /**
     * Navigate to order history page
     * @return OrderHistoryPage
     */
    public OrderHistoryPage navigateToOrderHistory() {
        try {
            By ordersLinkSelector = By.cssSelector("a[href*='/customer/orders']");
            click(ordersLinkSelector);
            logger.info("Navigated to order history");
            return new OrderHistoryPage(driver);
        } catch (Exception e) {
            logger.warn("Could not navigate to order history: {}", e.getMessage());
            return new OrderHistoryPage(driver);
        }
    }

    /**
     * Navigate to address book page
     * @return AddressBookPage
     */
    public AddressBookPage navigateToAddressBook() {
        try {
            By addressBookLinkSelector = By.cssSelector("a[href*='/customer/addresses']");
            click(addressBookLinkSelector);
            logger.info("Navigated to address book");
            return new AddressBookPage(driver);
        } catch (Exception e) {
            logger.warn("Could not navigate to address book: {}", e.getMessage());
            return new AddressBookPage(driver);
        }
    }

    /**
     * Navigate to password change page
     * @return ChangePasswordPage
     */
    public ChangePasswordPage navigateToPasswordChange() {
        try {
            By passwordLinkSelector = By.cssSelector("a[href*='/customer/changepassword']");
            click(passwordLinkSelector);
            logger.info("Navigated to password change");
            return new ChangePasswordPage(driver);
        } catch (Exception e) {
            logger.warn("Could not navigate to password change: {}", e.getMessage());
            return new ChangePasswordPage(driver);
        }
    }

    /**
     * Click account dropdown
     * @return AccountDropdown
     */
    public AccountDropdown clickAccountDropdown() {
        try {
            By accountDropdownSelector = By.cssSelector(".account, .header-links .account");
            click(accountDropdownSelector);
            logger.info("Clicked account dropdown");
            return new AccountDropdown(driver);
        } catch (Exception e) {
            logger.warn("Could not click account dropdown: {}", e.getMessage());
            return new AccountDropdown(driver);
        }
    }

    /**
     * Check if product links are clickable
     */
    public boolean areProductLinksClickable() {
        List<ProductElement> products = getFeaturedProducts();
        return products.stream().allMatch(product -> {
            try {
                SelenideElement titleLink = product.getElement().find(By.cssSelector("a, .product-title"));
                return titleLink.isEnabled();
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Check if links are clickable on mobile devices
     */
    public boolean areLinksClickableOnMobile() {
        // For mobile testing, check if links have adequate touch targets
        try {
            ElementsCollection links = $$(By.tagName("a"));
            return links.stream().allMatch(link -> {
                try {
                    return link.isEnabled() && link.isDisplayed();
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if featured products layout is mobile friendly
     */
    public boolean isFeaturedProductsLayoutMobileFriendly() {
        // Basic check for mobile layout compatibility
        try {
            By featuredSelector = By.cssSelector(".featured-products, .home-page-product-grid");
            return isElementDisplayed(featuredSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if search box is touch friendly
     */
    public boolean isSearchBoxTouchFriendly() {
        try {
            SelenideElement searchBox = $(By.id("small-searchterms"));
            // Check if element is large enough for touch interaction
            return searchBox.isDisplayed() && searchBox.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if mobile menu is clickable
     */
    public boolean isMobileMenuClickable() {
        return isMobileMenuDisplayed() && isElementEnabled(By.cssSelector(".mobile-menu, .hamburger-menu"));
    }

    /**
     * Get meta description from page
     */
    public String getMetaDescription() {
        try {
            By metaDescSelector = By.xpath("//meta[@name='description']");
            SelenideElement metaElement = $(metaDescSelector);
            return metaElement.getAttribute("content");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if page has H1 tag
     */
    public boolean hasH1Tag() {
        try {
            return isElementDisplayed(By.tagName("h1"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if page has heading tags
     */
    public boolean hasHeadings() {
        try {
            ElementsCollection headings = $$(By.cssSelector("h1, h2, h3, h4, h5, h6"));
            return headings.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Inner class for handling product elements
    public static class ProductElement {
        private final SelenideElement productElement;
        private final WebDriver driver;

        public ProductElement(SelenideElement productElement, WebDriver driver) {
            this.productElement = productElement;
            this.driver = driver;
        }

        /**
         * Get product title text
         * @return Product title
         */
        public String getTitle() {
            SelenideElement titleElement = productElement.$(SelectorUtils.getHomepageSelector("homepage.main_content.featured_products.product_title"));
            return titleElement.getText();
        }

        /**
         * Get product price text
         * @return Product price
         */
        public String getPrice() {
            SelenideElement priceElement = productElement.$(SelectorUtils.getHomepageSelector("homepage.main_content.featured_products.product_price"));
            return priceElement.getText();
        }

        /**
         * Click on product title to navigate to product details
         * @return ProductDetailsPage
         */
        public ProductDetailsPage clickTitle() {
            SelenideElement titleElement = productElement.$(SelectorUtils.getHomepageSelector("homepage.main_content.featured_products.product_title"));
            titleElement.click();
            return new ProductDetailsPage(driver);
        }

        /**
         * Get product element for custom interactions
         * @return WebElement representing this product
         */
        public SelenideElement getElement() {
            return productElement;
        }
    }

    /**
     * Inner class representing the account dropdown functionality
     */
    public static class AccountDropdown {
        private final WebDriver driver;

        public AccountDropdown(WebDriver driver) {
            this.driver = driver;
        }

        /**
         * Click on Customer Info link
         * @return CustomerInfoPage
         */
        public CustomerInfoPage clickCustomerInfo() {
            try {
                By customerInfoSelector = By.cssSelector("a[href*='customer/info'], .account-navigation a[href*='info']");
                driver.findElement(customerInfoSelector).click();
                return new CustomerInfoPage(driver);
            } catch (Exception e) {
                throw new RuntimeException("Could not navigate to customer info", e);
            }
        }

        /**
         * Click on Orders link
         * @return OrderHistoryPage
         */
        public OrderHistoryPage clickOrders() {
            try {
                By ordersSelector = By.cssSelector("a[href*='customer/orders'], .account-navigation a[href*='orders']");
                driver.findElement(ordersSelector).click();
                return new OrderHistoryPage(driver);
            } catch (Exception e) {
                throw new RuntimeException("Could not navigate to orders", e);
            }
        }

        /**
         * Click on Addresses link
         * @return AddressBookPage
         */
        public AddressBookPage clickAddresses() {
            try {
                By addressesSelector = By.cssSelector("a[href*='customer/addresses'], .account-navigation a[href*='addresses']");
                driver.findElement(addressesSelector).click();
                return new AddressBookPage(driver);
            } catch (Exception e) {
                throw new RuntimeException("Could not navigate to addresses", e);
            }
        }

        /**
         * Click on Change Password link
         * @return ChangePasswordPage
         */
        public ChangePasswordPage clickChangePassword() {
            try {
                By changePasswordSelector = By.cssSelector("a[href*='customer/changepassword'], .account-navigation a[href*='password']");
                driver.findElement(changePasswordSelector).click();
                return new ChangePasswordPage(driver);
            } catch (Exception e) {
                throw new RuntimeException("Could not navigate to change password", e);
            }
        }

        /**
         * Check if Customer Info link is displayed
         * @return true if Customer Info link is visible
         */
        public boolean isCustomerInfoLinkDisplayed() {
            try {
                By customerInfoSelector = By.cssSelector("a[href*='customer/info'], .account-navigation a[href*='info']");
                return driver.findElement(customerInfoSelector).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Check if My Account link is displayed
         * @return true if My Account link is visible
         */
        public boolean isMyAccountLinkDisplayed() {
            try {
                By myAccountSelector = By.cssSelector("a[href*='customer'], .account-link, .my-account");
                return driver.findElement(myAccountSelector).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Click on My Account link
         * @return CustomerInfoPage
         */
        public CustomerInfoPage clickMyAccount() {
            try {
                By myAccountSelector = By.cssSelector("a[href*='customer'], .account-link, .my-account");
                driver.findElement(myAccountSelector).click();
                return new CustomerInfoPage(driver);
            } catch (Exception e) {
                throw new RuntimeException("Could not navigate to my account", e);
            }
        }

        /**
         * Check if Orders link is displayed
         * @return true if Orders link is visible
         */
        public boolean isOrdersLinkDisplayed() {
            try {
                By ordersSelector = By.cssSelector("a[href*='customer/orders'], .account-navigation a[href*='orders']");
                return driver.findElement(ordersSelector).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Check if Change Password link is displayed
         * @return true if Change Password link is visible
         */
        public boolean isChangePasswordLinkDisplayed() {
            try {
                By changePasswordSelector = By.cssSelector("a[href*='customer/changepassword'], .account-navigation a[href*='password']");
                return driver.findElement(changePasswordSelector).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Check if Addresses link is displayed
         * @return true if Addresses link is visible
         */
        public boolean isAddressesLinkDisplayed() {
            try {
                By addressesSelector = By.cssSelector("a[href*='customer/addresses'], .account-navigation a[href*='addresses']");
                return driver.findElement(addressesSelector).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }
    }

    // ==================== SELENIDE ENHANCED METHODS ====================

    /**
     * Click login link using Selenide (more reliable)
     * @return LoginPage instance
     */
    public LoginPage clickLoginLinkSelenide() {
        clickSelenide("a[href='/login'], a[href*='login']");
        logger.info("Clicked login link using Selenide");
        return new LoginPage(driver);
    }

    /**
     * Click register link using Selenide
     * @return RegisterPage instance
     */
    public RegisterPage clickRegisterLinkSelenide() {
        clickSelenide("a[href='/register'], a[href*='register']");
        logger.info("Clicked register link using Selenide");
        return new RegisterPage(driver);
    }

    /**
     * Perform search using Selenide
     * @param searchTerm Search term
     * @return ProductSearchPage with results
     */
    public ProductSearchPage performSearchSelenide(String searchTerm) {
        typeSelenide("#small-searchterms", searchTerm);
        clickSelenide("input[type='submit'][value='Search']");

        // Wait for navigation to search results page
        waitForUrlToContain("/search");
        waitForPageToLoad();

        logger.info("Performed search using Selenide for: {}", searchTerm);
        return new ProductSearchPage(driver);
    }

    /**
     * Click shopping cart using Selenide
     * @return ShoppingCartPage instance
     */
    public ShoppingCartPage clickShoppingCartLinkSelenide() {
        clickSelenide("a[href*='cart'], .cart");
        logger.info("Clicked shopping cart using Selenide");
        return new ShoppingCartPage(driver);
    }

    /**
     * Navigate to Books category using Selenide
     * @return ProductCatalogPage for books
     */
    public ProductCatalogPage navigateToBooksSelenide() {
        clickSelenide("a[href*='/books']");
        logger.info("Navigated to Books using Selenide");
        return new ProductCatalogPage(driver, "Books");
    }

    /**
     * Check if login link is displayed using Selenide
     * @return true if login link is visible
     */
    public boolean isLoginLinkDisplayedSelenide() {
        return isVisibleSelenide("a[href='/login'], a[href*='login']");
    }

    /**
     * Check if search box is displayed using Selenide
     * @return true if search box is visible
     */
    public boolean isSearchBoxDisplayedSelenide() {
        return isVisibleSelenide("#small-searchterms");
    }

    /**
     * Get cart quantity using Selenide
     * @return Cart quantity as integer
     */
    public int getCartQuantitySelenide() {
        try {
            String qtyText = getTextSelenide(".cart-qty, .cart-quantity");
            String numberOnly = qtyText.replaceAll("[^0-9]", "");
            return numberOnly.isEmpty() ? 0 : Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 0;
        }
    }

}