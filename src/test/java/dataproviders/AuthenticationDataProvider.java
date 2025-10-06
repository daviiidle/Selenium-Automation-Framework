package dataproviders;

import factories.UserDataFactory;
import models.User;
import org.testng.annotations.DataProvider;

/**
 * Data provider class for authentication related test scenarios
 * Provides test data for login, registration, and user validation tests
 */
public class AuthenticationDataProvider {

    /**
     * Provides valid user registration data for positive testing
     * Returns multiple sets of valid user data for comprehensive testing
     */
    @DataProvider(name = "validRegistrationData")
    public static Object[][] getValidRegistrationData() {
        return new Object[][]{
            {UserDataFactory.createRandomUser(), "Valid user with all required fields"},
            {UserDataFactory.createRandomUser(), "Valid user with different data set"},
            {UserDataFactory.createRandomUser(), "Valid user for parallel execution"},
            {UserDataFactory.createTestUser(), "Standard test user data"},
            {UserDataFactory.createGuestUser(), "Guest user registration data"}
        };
    }

    /**
     * Provides invalid user registration data for negative testing
     * Tests various validation scenarios and error conditions
     */
    @DataProvider(name = "invalidRegistrationData")
    public static Object[][] getInvalidRegistrationData() {
        return new Object[][]{
            // Empty first name
            {createUserWithEmptyFirstName(), "First name is required", "firstName"},

            // Empty last name
            {createUserWithEmptyLastName(), "Last name is required", "lastName"},

            // Invalid email formats
            {createUserWithInvalidEmail("invalid"), "Invalid email format", "email"},
            {createUserWithInvalidEmail("test@"), "Invalid email format", "email"},
            {createUserWithInvalidEmail("@domain.com"), "Invalid email format", "email"},
            {createUserWithInvalidEmail("test..test@domain.com"), "Invalid email format", "email"},

            // Empty email
            {createUserWithInvalidEmail(""), "Email is required", "email"},

            // Weak passwords
            {createUserWithWeakPassword("123"), "Password too short", "password"},
            {createUserWithWeakPassword("pass"), "Password too weak", "password"},
            {createUserWithWeakPassword(""), "Password is required", "password"}
        };
    }

    /**
     * Provides valid login credentials for positive login testing
     */
    @DataProvider(name = "validLoginData")
    public static Object[][] getValidLoginData() {
        // Note: In real testing, these would be pre-registered users
        // For demo purposes, using test accounts that should exist
        // NOTE: Only customer@demowebshop.com exists on the live demo site
        // Other accounts would need to be registered first via RegistrationTests
        return new Object[][]{
            // {"test.user@demowebshop.com", "TestPassword123", "Valid registered user"}, // DISABLED - Account doesn't exist on demo site
            // {"admin@demowebshop.com", "AdminPassword123", "Admin user login"}, // DISABLED - Account doesn't exist on demo site
            {"customer@demowebshop.com", "CustomerPass123", "Customer account login"}
        };
    }

    /**
     * Provides invalid login credentials for negative testing
     */
    @DataProvider(name = "invalidLoginData")
    public static Object[][] getInvalidLoginData() {
        return new Object[][]{
            // Wrong password
            {"test.user@demowebshop.com", "wrongpassword", "Incorrect password", "Invalid credentials"},

            // Unregistered email
            {"unregistered@email.com", "password123", "Unregistered email", "Invalid credentials"},

            // Empty email
            {"", "password123", "Empty email", "Email required"},

            // Empty password
            {"test@email.com", "", "Empty password", "Password required"},

            // Both empty
            {"", "", "Empty credentials", "Credentials required"},

            // Invalid email format
            {"invalid-email", "password123", "Invalid email format", "Invalid email"},

            // SQL injection attempt
            {"'; DROP TABLE users; --", "password", "SQL injection attempt", "Invalid credentials"},

            // XSS attempt
            {"<script>alert('xss')</script>", "password", "XSS attempt", "Invalid credentials"}
        };
    }

    /**
     * Provides email validation test data
     */
    @DataProvider(name = "emailValidationData")
    public static Object[][] getEmailValidationData() {
        return new Object[][]{
            // Valid emails
            {"test@domain.com", true, "Standard valid email"},
            {"user.test@domain.co.uk", true, "Valid email with subdomain"},
            {"test123@domain-name.com", true, "Valid email with numbers and hyphens"},
            {"first.last+tag@example.com", true, "Valid email with plus tag"},

            // Invalid emails
            {"invalid", false, "Missing @ and domain"},
            {"@domain.com", false, "Missing username"},
            {"test@", false, "Missing domain"},
            {"test.domain.com", false, "Missing @ symbol"},
            {"test @domain.com", false, "Space in email"},
            {"test@domain", false, "Missing top-level domain"},
            {"test@.domain.com", false, "Starting with dot"},
            {"test@domain..com", false, "Double dots in domain"},
            {"", false, "Empty email"}
        };
    }

    /**
     * Provides password strength test data
     */
    @DataProvider(name = "passwordValidationData")
    public static Object[][] getPasswordValidationData() {
        return new Object[][]{
            // Strong passwords
            {UserDataFactory.generateSecurePassword(), true, "Generated secure password"},
            {"StrongPass123!", true, "Password with all requirements"},
            {"MySecureP@ssw0rd", true, "Complex password"},

            // Weak passwords
            {"123456", false, "Too short and only numbers"},
            {"password", false, "Common weak password"},
            {"Pass1", false, "Too short"},
            {"", false, "Empty password"},
            {"onlylowercase", false, "Only lowercase letters"},
            {"ONLYUPPERCASE", false, "Only uppercase letters"},
            {"1234567890", false, "Only numbers"},
            {"password123", false, "Common pattern"},
            {UserDataFactory.generateWeakPassword(), false, "Generated weak password"}
        };
    }

    /**
     * Provides multiple user data sets for parallel testing
     */
    @DataProvider(name = "parallelUserData", parallel = false)
    public static Object[][] getParallelUserData() {
        Object[][] userData = new Object[10][];
        for (int i = 0; i < 10; i++) {
            userData[i] = new Object[]{UserDataFactory.createRandomUser(), "Parallel test user " + (i + 1)};
        }
        return userData;
    }

    /**
     * Provides remember me functionality test data
     */
    @DataProvider(name = "rememberMeData")
    public static Object[][] getRememberMeData() {
        return new Object[][]{
            {"test.user@demowebshop.com", "password123", true, "Remember me checked"},
            {"test.user@demowebshop.com", "password123", false, "Remember me unchecked"},
            {"customer@demowebshop.com", "customerpass", true, "Customer with remember me"},
            {"customer@demowebshop.com", "customerpass", false, "Customer without remember me"}
        };
    }

    /**
     * Provides logout test scenarios
     */
    @DataProvider(name = "logoutScenarios")
    public static Object[][] getLogoutScenarios() {
        return new Object[][]{
            {"Standard logout after login", "test.user@demowebshop.com", "password123"},
            {"Logout after cart operations", "customer@demowebshop.com", "customerpass"},
            {"Logout after checkout attempt", "test.user@demowebshop.com", "password123"},
            {"Quick logout after login", "admin@demowebshop.com", "adminpass"}
        };
    }

    // Helper methods for creating specific invalid user data

    private static User createUserWithEmptyFirstName() {
        User user = UserDataFactory.createRandomUser();
        user.setFirstName("");
        return user;
    }

    private static User createUserWithEmptyLastName() {
        User user = UserDataFactory.createRandomUser();
        user.setLastName("");
        return user;
    }

    private static User createUserWithInvalidEmail(String invalidEmail) {
        User user = UserDataFactory.createRandomUser();
        user.setEmail(invalidEmail);
        return user;
    }

    private static User createUserWithWeakPassword(String weakPassword) {
        User user = UserDataFactory.createRandomUser();
        user.setPassword(weakPassword);
        return user;
    }
}