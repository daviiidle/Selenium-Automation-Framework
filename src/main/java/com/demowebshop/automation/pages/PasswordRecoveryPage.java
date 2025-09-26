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
            // Try multiple selectors with shorter timeout to avoid long waits
            String[] messageSelectors = {
                ".result",
                ".confirmation-message",
                ".success-message",
                ".message",
                ".info-message",
                ".notification",
                ".alert-info",
                ".alert-success"
            };

            for (String selector : messageSelectors) {
                try {
                    By messageBy = By.cssSelector(selector);
                    // Use soft wait to avoid exceptions for missing elements
                    if (waitUtils.softWaitForElementToBeVisible(messageBy, 3) != null) {
                        String messageText = getText(messageBy);
                        if (!messageText.trim().isEmpty()) {
                            return messageText;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            return "";
        } catch (Exception e) {
            logger.debug("Error getting confirmation message: {}", e.getMessage());
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

    /**
     * Check if validation errors are present
     * @return true if validation errors are displayed
     */
    public boolean hasValidationErrors() {
        try {
            // Try multiple selectors for validation errors
            String[] errorSelectors = {
                ".validation-summary-errors",
                ".field-validation-error",
                ".error-message",
                ".validation-error",
                ".form-errors",
                ".message-error",
                ".alert-danger",
                ".alert-error"
            };

            for (String selector : errorSelectors) {
                try {
                    By errorBy = By.cssSelector(selector);
                    if (isElementDisplayed(errorBy)) {
                        String errorText = getText(errorBy);
                        if (!errorText.trim().isEmpty()) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Check for any visible error text in common containers
            String[] containerSelectors = {
                ".result",
                ".confirmation-message",
                ".message",
                ".content"
            };

            for (String selector : containerSelectors) {
                try {
                    By containerBy = By.cssSelector(selector);
                    if (isElementDisplayed(containerBy)) {
                        String text = getText(containerBy).toLowerCase();
                        if (text.contains("error") || text.contains("invalid") ||
                            text.contains("required") || text.contains("must")) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            return false;
        } catch (Exception e) {
            logger.debug("Error checking for validation errors: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get validation error message
     * @return Validation error message text
     */
    public String getValidationErrorMessage() {
        try {
            // Try specific error selectors first
            String[] errorSelectors = {
                ".validation-summary-errors",
                ".field-validation-error",
                ".error-message",
                ".validation-error",
                ".form-errors",
                ".message-error",
                ".alert-danger",
                ".alert-error"
            };

            for (String selector : errorSelectors) {
                try {
                    By errorBy = By.cssSelector(selector);
                    if (isElementDisplayed(errorBy)) {
                        String errorText = getText(errorBy);
                        if (!errorText.trim().isEmpty()) {
                            return errorText;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

            // Fallback to checking confirmation message for errors
            String confirmationText = getConfirmationMessage();
            if (confirmationText.toLowerCase().contains("error") ||
                confirmationText.toLowerCase().contains("invalid") ||
                confirmationText.toLowerCase().contains("required")) {
                return confirmationText;
            }

            return "";
        } catch (Exception e) {
            logger.debug("Error getting validation error message: {}", e.getMessage());
            return "";
        }
    }
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