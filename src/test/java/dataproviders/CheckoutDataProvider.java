package dataproviders;

import factories.CheckoutDataFactory;
import models.Address;
import models.PaymentInfo;
import org.testng.annotations.DataProvider;

/**
 * Data provider class for checkout and payment related test scenarios
 * Provides test data for billing, shipping, payment, and order completion processes
 */
public class CheckoutDataProvider {

    /**
     * Provides comprehensive checkout test data for end-to-end testing
     */
    @DataProvider(name = "checkoutTestData")
    public static Object[][] getCheckoutTestData() {
        return CheckoutDataFactory.getCheckoutTestData();
    }

    /**
     * Provides payment method test data
     */
    @DataProvider(name = "paymentTestData")
    public static Object[][] getPaymentTestData() {
        return CheckoutDataFactory.getPaymentTestData();
    }

    /**
     * Provides valid billing address data for positive testing
     */
    @DataProvider(name = "validBillingAddresses")
    public static Object[][] getValidBillingAddresses() {
        return new Object[][]{
            {CheckoutDataFactory.createTestBillingAddress(), "Standard test billing address"},
            {CheckoutDataFactory.createRandomBillingAddress(), "Random valid billing address"},
            {CheckoutDataFactory.createRandomBillingAddress(), "Another random billing address"}
        };
    }

    /**
     * Provides valid shipping address data for positive testing
     */
    @DataProvider(name = "validShippingAddresses")
    public static Object[][] getValidShippingAddresses() {
        return new Object[][]{
            {CheckoutDataFactory.createTestShippingAddress(), "Standard test shipping address"},
            {CheckoutDataFactory.createRandomShippingAddress(), "Random valid shipping address"},
            {CheckoutDataFactory.createRandomShippingAddress(), "Another random shipping address"}
        };
    }

    /**
     * Provides shipping method test data
     */
    @DataProvider(name = "shippingMethods")
    public static Object[][] getShippingMethods() {
        String[] methods = CheckoutDataFactory.getAllShippingMethods();
        Object[][] data = new Object[methods.length][];

        for (int i = 0; i < methods.length; i++) {
            data[i] = new Object[]{methods[i], "Test shipping method: " + methods[i]};
        }

        return data;
    }

    /**
     * Provides payment method test data
     */
    @DataProvider(name = "paymentMethods")
    public static Object[][] getPaymentMethods() {
        String[] methods = CheckoutDataFactory.getAllPaymentMethods();
        Object[][] data = new Object[methods.length][];

        for (int i = 0; i < methods.length; i++) {
            data[i] = new Object[]{methods[i], "Test payment method: " + methods[i]};
        }

        return data;
    }

    /**
     * Provides country selection test data
     */
    @DataProvider(name = "countries")
    public static Object[][] getCountries() {
        String[] countries = CheckoutDataFactory.getAllCountries();
        Object[][] data = new Object[countries.length][];

        for (int i = 0; i < countries.length; i++) {
            data[i] = new Object[]{countries[i], "Test country selection: " + countries[i]};
        }

        return data;
    }

    /**
     * Provides complete guest checkout scenarios
     */
    @DataProvider(name = "guestCheckoutData")
    public static Object[][] getGuestCheckoutData() {
        return new Object[][]{
            {
                CheckoutDataFactory.createTestBillingAddress(),
                CheckoutDataFactory.createTestShippingAddress(),
                "Ground",
                "Credit Card",
                CheckoutDataFactory.createTestPaymentInfo(),
                "Complete guest checkout with different addresses"
            },
            {
                CheckoutDataFactory.createRandomBillingAddress(),
                null, // Same as billing
                "Express",
                "Check / Money Order",
                null,
                "Guest checkout with same address for billing and shipping"
            },
            {
                CheckoutDataFactory.createTestBillingAddress(),
                CheckoutDataFactory.createRandomShippingAddress(),
                "2nd Day Air",
                "Cash On Delivery (COD)",
                null,
                "Guest checkout with COD payment"
            }
        };
    }

    /**
     * Provides registered user checkout scenarios
     */
    @DataProvider(name = "registeredUserCheckoutData")
    public static Object[][] getRegisteredUserCheckoutData() {
        return new Object[][]{
            {
                "test.user@demowebshop.com",
                "TestPassword123",
                CheckoutDataFactory.createTestBillingAddress(),
                "Ground",
                "Credit Card",
                CheckoutDataFactory.createTestPaymentInfo(),
                "Registered user checkout with saved address"
            },
            {
                "customer@demowebshop.com",
                "CustomerPass123",
                CheckoutDataFactory.createRandomBillingAddress(),
                "Express",
                "Check / Money Order",
                null,
                "Registered user checkout with new address"
            }
        };
    }

    /**
     * Provides credit card validation test data
     */
    @DataProvider(name = "creditCardData")
    public static Object[][] getCreditCardData() {
        return new Object[][]{
            // Valid cards
            {"John Doe", "4111111111111111", "Visa", "12", "2025", "123", true, "Valid Visa card"},
            {"Jane Smith", "5555555555554444", "MasterCard", "06", "2026", "456", true, "Valid MasterCard"},
            {"Bob Johnson", "378282246310005", "American Express", "03", "2027", "1234", true, "Valid American Express"},
            {"Alice Brown", "6011111111111117", "Discover", "09", "2025", "789", true, "Valid Discover card"},

            // Invalid cards
            {"", "4111111111111111", "Visa", "12", "2025", "123", false, "Empty cardholder name"},
            {"John Doe", "1234567890123456", "Visa", "12", "2025", "123", false, "Invalid card number"},
            {"John Doe", "4111111111111112", "Visa", "12", "2025", "123", false, "Invalid card checksum"},
            {"John Doe", "4111111111111111", "Visa", "13", "2025", "123", false, "Invalid expiration month"},
            {"John Doe", "4111111111111111", "Visa", "12", "2020", "123", false, "Expired card"},
            {"John Doe", "4111111111111111", "Visa", "12", "2025", "12", false, "Invalid CVV length"},
            {"John Doe", "4111111111111111", "Visa", "12", "2025", "", false, "Empty CVV"}
        };
    }

    /**
     * Provides address validation test data
     */
    @DataProvider(name = "addressValidationData")
    public static Object[][] getAddressValidationData() {
        return new Object[][]{
            // Valid addresses
            {createCompleteAddress(), true, "Complete valid address"},
            {createMinimalAddress(), true, "Minimal required address fields"},

            // Invalid addresses
            {createAddressWithEmptyFirstName(), false, "Missing first name"},
            {createAddressWithEmptyLastName(), false, "Missing last name"},
            {createAddressWithEmptyAddress(), false, "Missing street address"},
            {createAddressWithEmptyCity(), false, "Missing city"},
            {createAddressWithEmptyZip(), false, "Missing zip code"},
            {createAddressWithEmptyCountry(), false, "Missing country"},
            {createAddressWithInvalidEmail(), false, "Invalid email format"},
            {createAddressWithInvalidPhone(), false, "Invalid phone format"}
        };
    }

    /**
     * Provides order total calculation test data
     */
    @DataProvider(name = "orderTotalData")
    public static Object[][] getOrderTotalData() {
        return new Object[][]{
            {10.00, 5.00, 1.00, 16.00, "Basic order total calculation"},
            {25.50, 10.00, 2.55, 38.05, "Order with tax calculation"},
            {100.00, 0.00, 8.75, 108.75, "Order with free shipping"},
            {50.00, 15.00, 0.00, 65.00, "Order with no tax"},
            {0.00, 5.00, 0.00, 5.00, "Order with only shipping cost"}
        };
    }

    /**
     * Provides checkout validation scenarios
     */
    @DataProvider(name = "checkoutValidationData")
    public static Object[][] getCheckoutValidationData() {
        return new Object[][]{
            // Missing required fields
            {null, CheckoutDataFactory.createTestShippingAddress(), "Ground", "Credit Card", false, "Missing billing address"},
            {CheckoutDataFactory.createTestBillingAddress(), null, "Ground", "Credit Card", false, "Missing shipping address"},
            {CheckoutDataFactory.createTestBillingAddress(), CheckoutDataFactory.createTestShippingAddress(), null, "Credit Card", false, "Missing shipping method"},
            {CheckoutDataFactory.createTestBillingAddress(), CheckoutDataFactory.createTestShippingAddress(), "Ground", null, false, "Missing payment method"},

            // Valid complete checkout
            {CheckoutDataFactory.createTestBillingAddress(), CheckoutDataFactory.createTestShippingAddress(), "Ground", "Credit Card", true, "Complete valid checkout"}
        };
    }

    /**
     * Provides parallel checkout test data for performance testing
     */
    @DataProvider(name = "parallelCheckoutData", parallel = true)
    public static Object[][] getParallelCheckoutData() {
        Object[][] data = new Object[5][];
        for (int i = 0; i < 5; i++) {
            data[i] = new Object[]{
                CheckoutDataFactory.createRandomBillingAddress(),
                CheckoutDataFactory.createRandomShippingAddress(),
                CheckoutDataFactory.getRandomShippingMethod(),
                CheckoutDataFactory.getRandomPaymentMethod(),
                "Parallel checkout test " + (i + 1)
            };
        }
        return data;
    }

    // Helper methods for creating specific test addresses

    private static Address createCompleteAddress() {
        return CheckoutDataFactory.createTestBillingAddress();
    }

    private static Address createMinimalAddress() {
        return Address.builder()
                .firstName("John")
                .lastName("Doe")
                .address1("123 Test St")
                .city("Test City")
                .zipPostalCode("12345")
                .country("United States")
                .build();
    }

    private static Address createAddressWithEmptyFirstName() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setFirstName("");
        return address;
    }

    private static Address createAddressWithEmptyLastName() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setLastName("");
        return address;
    }

    private static Address createAddressWithEmptyAddress() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setAddress1("");
        return address;
    }

    private static Address createAddressWithEmptyCity() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setCity("");
        return address;
    }

    private static Address createAddressWithEmptyZip() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setZipPostalCode("");
        return address;
    }

    private static Address createAddressWithEmptyCountry() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setCountry("");
        return address;
    }

    private static Address createAddressWithInvalidEmail() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setEmail("invalid-email-format");
        return address;
    }

    private static Address createAddressWithInvalidPhone() {
        Address address = CheckoutDataFactory.createTestBillingAddress();
        address.setPhoneNumber("invalid-phone");
        return address;
    }
}