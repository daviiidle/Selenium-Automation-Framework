package com.demowebshop.automation.pages;

import com.demowebshop.automation.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object Model for Change Password Page
 * Handles customer password change functionality
 */
public class ChangePasswordPage extends BasePage {

    private static final String PAGE_URL_PATTERN = "/customer/changepassword";

    public ChangePasswordPage(WebDriver driver) {
        super(driver);
    }

    public ChangePasswordPage() {
        super();
    }

    /**
     * Change password
     * @param currentPassword Current password
     * @param newPassword New password
     * @param confirmPassword Confirm new password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage changePassword(String currentPassword, String newPassword, String confirmPassword) {
        try {
            type(By.cssSelector("#OldPassword"), currentPassword);
            type(By.cssSelector("#NewPassword"), newPassword);
            type(By.cssSelector("#ConfirmNewPassword"), confirmPassword);

            By changePasswordSelector = By.cssSelector("input[type='submit'], .change-password-button");
            click(changePasswordSelector);
            waitForPageToLoad();

            logger.info("Changed password");
        } catch (Exception e) {
            logger.warn("Could not change password: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check if password change was successful
     * @return true if success message is displayed
     */
    public boolean isPasswordChangeSuccessful() {
        try {
            By successSelector = By.cssSelector(".result, .success-message");
            return isElementDisplayed(successSelector) &&
                   getText(successSelector).toLowerCase().contains("password");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isPageLoaded() {
        return getCurrentUrl().contains(PAGE_URL_PATTERN) ||
               isElementDisplayed(By.cssSelector("#OldPassword"));
    }

    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }

    /**
     * Fill current password field
     * @param currentPassword Current password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage fillCurrentPassword(String currentPassword) {
        try {
            By currentPasswordField = By.cssSelector("#OldPassword");
            clear(currentPasswordField);
            type(currentPasswordField, currentPassword);
            logger.info("Filled current password");
        } catch (Exception e) {
            logger.error("Failed to fill current password: {}", e.getMessage());
            throw new RuntimeException("Could not fill current password", e);
        }
        return this;
    }

    /**
     * Fill new password field
     * @param newPassword New password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage fillNewPassword(String newPassword) {
        try {
            By newPasswordField = By.cssSelector("#NewPassword");
            clear(newPasswordField);
            type(newPasswordField, newPassword);
            logger.info("Filled new password");
        } catch (Exception e) {
            logger.error("Failed to fill new password: {}", e.getMessage());
            throw new RuntimeException("Could not fill new password", e);
        }
        return this;
    }

    /**
     * Fill confirm password field
     * @param confirmPassword Confirm password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage fillConfirmPassword(String confirmPassword) {
        try {
            By confirmPasswordField = By.cssSelector("#ConfirmNewPassword");
            clear(confirmPasswordField);
            type(confirmPasswordField, confirmPassword);
            logger.info("Filled confirm password");
        } catch (Exception e) {
            logger.error("Failed to fill confirm password: {}", e.getMessage());
            throw new RuntimeException("Could not fill confirm password", e);
        }
        return this;
    }

    /**
     * Click the save button to change password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage clickSaveButton() {
        try {
            By saveButton = By.cssSelector("input[value='Change password'], .button-change-password, input[type='submit']");
            click(saveButton);
            logger.info("Clicked save button");
        } catch (Exception e) {
            logger.error("Failed to click save button: {}", e.getMessage());
            throw new RuntimeException("Could not click save button", e);
        }
        return this;
    }

    /**
     * Check if password mismatch error is displayed
     * @return true if password mismatch error is visible
     */
    public boolean hasPasswordMismatchError() {
        try {
            By errorMessage = By.cssSelector(".field-validation-error, .validation-summary-errors, .error-message");
            return isElementDisplayed(errorMessage) &&
                   getText(errorMessage).toLowerCase().contains("password");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enter current password (alias for fillCurrentPassword)
     * @param currentPassword Current password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage enterCurrentPassword(String currentPassword) {
        return fillCurrentPassword(currentPassword);
    }

    /**
     * Enter new password (alias for fillNewPassword)
     * @param newPassword New password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage enterNewPassword(String newPassword) {
        return fillNewPassword(newPassword);
    }

    /**
     * Confirm new password (alias for fillConfirmPassword)
     * @param confirmPassword Confirm password
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage confirmNewPassword(String confirmPassword) {
        return fillConfirmPassword(confirmPassword);
    }

    /**
     * Clear all form fields
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage clearForm() {
        try {
            clear(By.cssSelector("#OldPassword"));
            clear(By.cssSelector("#NewPassword"));
            clear(By.cssSelector("#ConfirmNewPassword"));
            logger.info("Cleared change password form");
        } catch (Exception e) {
            logger.error("Failed to clear form: {}", e.getMessage());
            throw new RuntimeException("Could not clear form", e);
        }
        return this;
    }

    /**
     * Submit the change password form (alias for clickSaveButton)
     * @return ChangePasswordPage for method chaining
     */
    public ChangePasswordPage submitForm() {
        return clickSaveButton();
    }

    /**
     * Check if form is complete
     * @return true if all required fields are filled
     */
    public boolean isFormComplete() {
        try {
            String oldPassword = getAttribute(By.cssSelector("#OldPassword"), "value");
            String newPassword = getAttribute(By.cssSelector("#NewPassword"), "value");
            String confirmPassword = getAttribute(By.cssSelector("#ConfirmNewPassword"), "value");

            return !oldPassword.trim().isEmpty() &&
                   !newPassword.trim().isEmpty() &&
                   !confirmPassword.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current password value
     * @return Current password field value
     */
    public String getCurrentPasswordValue() {
        try {
            return getAttribute(By.cssSelector("#OldPassword"), "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get new password value
     * @return New password field value
     */
    public String getNewPasswordValue() {
        try {
            return getAttribute(By.cssSelector("#NewPassword"), "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get confirm password value
     * @return Confirm password field value
     */
    public String getConfirmPasswordValue() {
        try {
            return getAttribute(By.cssSelector("#ConfirmNewPassword"), "value");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if save button is enabled
     * @return true if save button can be clicked
     */
    public boolean isSaveButtonEnabled() {
        try {
            By saveButton = By.cssSelector("input[value='Change password'], .button-change-password, input[type='submit']");
            return isElementEnabled(saveButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if password change success message is displayed
     * @return true if success message is visible
     */
    public boolean isPasswordChangeSuccessMessageDisplayed() {
        try {
            By successSelector = By.cssSelector(".result, .success-message, .notification-success");
            return isElementDisplayed(successSelector) &&
                   getText(successSelector).toLowerCase().contains("password");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current password field has validation error
     * @return true if current password field shows error
     */
    public boolean hasCurrentPasswordError() {
        try {
            By currentPasswordErrorSelector = By.cssSelector("#OldPassword + .field-validation-error, .field-validation-error[data-valmsg-for='OldPassword']");
            return isElementDisplayed(currentPasswordErrorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if new password field has validation error
     * @return true if new password field shows error
     */
    public boolean hasNewPasswordError() {
        try {
            By newPasswordErrorSelector = By.cssSelector("#NewPassword + .field-validation-error, .field-validation-error[data-valmsg-for='NewPassword']");
            return isElementDisplayed(newPasswordErrorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if confirm password field has validation error
     * @return true if confirm password field shows error
     */
    public boolean hasConfirmPasswordError() {
        try {
            By confirmPasswordErrorSelector = By.cssSelector("#ConfirmNewPassword + .field-validation-error, .field-validation-error[data-valmsg-for='ConfirmNewPassword']");
            return isElementDisplayed(confirmPasswordErrorSelector);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current password error message
     * @return Current password error message text
     */
    public String getCurrentPasswordErrorMessage() {
        try {
            By currentPasswordErrorSelector = By.cssSelector("#OldPassword + .field-validation-error, .field-validation-error[data-valmsg-for='OldPassword']");
            if (isElementDisplayed(currentPasswordErrorSelector)) {
                return getText(currentPasswordErrorSelector);
            }
        } catch (Exception e) {
            logger.debug("Could not get current password error message");
        }
        return "";
    }

    /**
     * Get new password error message
     * @return New password error message text
     */
    public String getNewPasswordErrorMessage() {
        try {
            By newPasswordErrorSelector = By.cssSelector("#NewPassword + .field-validation-error, .field-validation-error[data-valmsg-for='NewPassword']");
            if (isElementDisplayed(newPasswordErrorSelector)) {
                return getText(newPasswordErrorSelector);
            }
        } catch (Exception e) {
            logger.debug("Could not get new password error message");
        }
        return "";
    }

    /**
     * Get confirm password error message
     * @return Confirm password error message text
     */
    public String getConfirmPasswordErrorMessage() {
        try {
            By confirmPasswordErrorSelector = By.cssSelector("#ConfirmNewPassword + .field-validation-error, .field-validation-error[data-valmsg-for='ConfirmNewPassword']");
            if (isElementDisplayed(confirmPasswordErrorSelector)) {
                return getText(confirmPasswordErrorSelector);
            }
        } catch (Exception e) {
            logger.debug("Could not get confirm password error message");
        }
        return "";
    }

    /**
     * Check if passwords match (new password and confirm password)
     * @return true if passwords match
     */
    public boolean doPasswordsMatch() {
        try {
            String newPassword = getNewPasswordValue();
            String confirmPassword = getConfirmPasswordValue();
            return newPassword.equals(confirmPassword) && !newPassword.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if any validation errors are present
     * @return true if any field has validation errors
     */
    public boolean hasAnyValidationErrors() {
        return hasCurrentPasswordError() || hasNewPasswordError() || hasConfirmPasswordError() || hasPasswordMismatchError();
    }
}