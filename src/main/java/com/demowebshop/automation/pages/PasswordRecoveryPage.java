package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import com.demowebshop.automation.utils.data.SelectorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Password Recovery Page
 * Handles password recovery functionality
 */
public class PasswordRecoveryPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/passwordrecovery";

    public PasswordRecoveryPage(WebDriver driver) {
        super(driver);
    }

    public PasswordRecoveryPage() {
        super();
    }

    /**
     * Enter email for password recovery
     * @param email Email address
     * @return PasswordRecoveryPage for method chaining
     */
    public PasswordRecoveryPage enterEmail(String email) {
        By emailSelector = SelectorUtils.getAuthSelector("authentication.password_recovery.form_elements.email_input");
        type(emailSelector, email);
        logger.info("Entered email for password recovery: {}", email);
        return this;
    }

    /**
     * Click recover button
     * @return PasswordRecoveryPage for method chaining
     */
    public PasswordRecoveryPage clickRecover() {
        By recoverSelector = SelectorUtils.getAuthSelector("authentication.password_recovery.form_elements.recover_button");
        click(recoverSelector);
        logger.info("Clicked recover button");
        return this;
    }

    /**
     * Perform complete password recovery
     * @param email Email address
     * @return PasswordRecoveryPage for method chaining
     */
    public PasswordRecoveryPage recoverPassword(String email) {
        enterEmail(email);
        clickRecover();
        return this;
    }

    /**
     * Check if email field is displayed
     * @return true if email input field is visible
     */
    public boolean isEmailFieldDisplayed() {
        By emailSelector = SelectorUtils.getAuthSelector("authentication.password_recovery.form_elements.email_input");
        return isElementDisplayed(emailSelector);
    }

    /**
     * Check if recover button is displayed
     * @return true if recover button is visible
     */
    public boolean isRecoverButtonDisplayed() {
        By recoverSelector = SelectorUtils.getAuthSelector("authentication.password_recovery.form_elements.recover_button");
        return isElementDisplayed(recoverSelector);
    }

    /**
     * Click recover button (alias for clickRecover)
     * @return PasswordRecoveryPage for method chaining
     */
    public PasswordRecoveryPage clickRecoverButton() {
        return clickRecover();
    }

    /**
     * Get confirmation message after recovery attempt
     * @return Confirmation message text
     */
    public String getConfirmationMessage() {
        try {
            By messageSelector = By.cssSelector(".result, .confirmation-message, .success-message");
            return getText(messageSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Clear email field
     * @return PasswordRecoveryPage for method chaining
     */
    public PasswordRecoveryPage clearEmailField() {
        By emailSelector = SelectorUtils.getAuthSelector("authentication.password_recovery.form_elements.email_input");
        findElement(emailSelector).clear();
        return this;
    }

    public boolean hasValidationErrors() { return !getValidationErrorMessage().isEmpty(); }
    public String getValidationErrorMessage() { return getConfirmationMessage(); }
    public boolean isRecoverButtonEnabled() { return isElementEnabled(SelectorUtils.getAuthSelector("authentication.password_recovery.form_elements.recover_button")); }

    /**
     * Get instruction text on password recovery page
     */
    public String getInstructionText() {
        try {
            By instructionSelector = By.cssSelector(".instruction-text, .recovery-instructions, .page-body");
            return getText(instructionSelector);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if instruction text is displayed
     */
    public boolean isInstructionTextDisplayed() {
        try {
            By instructionSelector = By.cssSelector(".instruction-text, .recovery-instructions, .page-body");
            return isElementDisplayed(instructionSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click back to login link
     */
    public LoginPage clickBackToLoginLink() {
        try {
            By backToLoginSelector = By.cssSelector("a[href*='login'], .back-to-login");
            click(backToLoginSelector);
            logger.info("Clicked back to login link");
            return new LoginPage(driver);
        } catch (Exception e) {
            logger.warn("Could not click back to login link");
            return new LoginPage(driver);
        }
    }

    /**
     * Check if back to login link is displayed
     */
    public boolean isBackToLoginLinkDisplayed() {
        try {
            By backToLoginSelector = By.cssSelector("a[href*='login'], .back-to-login");
            return isElementDisplayed(backToLoginSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if confirmation message is displayed
     */
    public boolean isConfirmationMessageDisplayed() {
        return !getConfirmationMessage().isEmpty();
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