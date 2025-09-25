package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Wishlist Page
 * Placeholder implementation - can be expanded based on requirements
 */
public class WishlistPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/wishlist";

    public WishlistPage(WebDriver driver) {
        super(driver);
    }

    public WishlistPage() {
        super();
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN);
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }
}