package factories;

import com.github.javafaker.Faker;
import models.Address;
import models.PaymentInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

/**
 * Factory class for generating checkout and payment related test data
 * Used for testing billing, shipping, and payment processes
 */
public class CheckoutDataFactory {
    private static final Logger logger = LogManager.getLogger(CheckoutDataFactory.class);
    private static final Faker faker = new Faker(new Locale("en-US"));
    private static final Random random = new SecureRandom();

    private static final String[] COUNTRIES = {
        "United States", "Canada", "United Kingdom", "Germany", "France",
        "Australia", "Japan", "India", "Brazil", "Mexico"
    };

    private static final String[] SHIPPING_METHODS = {
        "Ground", "Next Day Air", "2nd Day Air", "Express", "Standard"
    };

    private static final String[] PAYMENT_METHODS = {
        "Credit Card", "Check / Money Order", "Cash On Delivery (COD)", "Purchase Order"
    };

    private static final String[] CARD_TYPES = {
        "Visa", "MasterCard", "American Express", "Discover"
    };

    /**
     * Create a random valid billing address
     */
    public static Address createRandomBillingAddress() {
        Address address = Address.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .company(faker.company().name())
                .country(getRandomCountry())
                .state(faker.address().state())
                .city(faker.address().city())
                .address1(faker.address().streetAddress())
                .address2(faker.address().secondaryAddress())
                .zipPostalCode(faker.address().zipCode())
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .faxNumber(faker.phoneNumber().phoneNumber())
                .build();

        logger.debug("Created random billing address for: {} {}",
                    address.getFirstName(), address.getLastName());
        return address;
    }

    /**
     * Create a random valid shipping address
     */
    public static Address createRandomShippingAddress() {
        Address address = Address.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .company(faker.company().name())
                .country(getRandomCountry())
                .state(faker.address().state())
                .city(faker.address().city())
                .address1(faker.address().streetAddress())
                .address2(faker.address().secondaryAddress())
                .zipPostalCode(faker.address().zipCode())
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .build();

        logger.debug("Created random shipping address for: {} {}",
                    address.getFirstName(), address.getLastName());
        return address;
    }

    /**
     * Create address with specific details
     */
    public static Address createAddressWithDetails(String firstName, String lastName,
                                                   String email, String country, String state,
                                                   String city, String address1, String zipCode,
                                                   String phone) {
        Address address = Address.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .country(country)
                .state(state)
                .city(city)
                .address1(address1)
                .zipPostalCode(zipCode)
                .phoneNumber(phone)
                .build();

        logger.debug("Created specific address for: {} {}", firstName, lastName);
        return address;
    }

    /**
     * Create test address for automation (consistent data)
     */
    public static Address createTestBillingAddress() {
        return Address.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .company("Test Company Inc.")
                .country("United States")
                .state("California")
                .city("Los Angeles")
                .address1("123 Test Street")
                .address2("Apartment 4B")
                .zipPostalCode("90210")
                .phoneNumber("555-123-4567")
                .faxNumber("555-123-4568")
                .build();
    }

    /**
     * Create test shipping address
     */
    public static Address createTestShippingAddress() {
        return Address.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@test.com")
                .company("Shipping Test Co.")
                .country("United States")
                .state("New York")
                .city("New York")
                .address1("456 Shipping Ave")
                .address2("Suite 100")
                .zipPostalCode("10001")
                .phoneNumber("555-987-6543")
                .build();
    }

    /**
     * Create invalid address for negative testing
     */
    public static Address createInvalidAddress() {
        return Address.builder()
                .firstName("") // Empty required field
                .lastName("") // Empty required field
                .email("invalid-email") // Invalid email format
                .country("")
                .state("")
                .city("")
                .address1("")
                .zipPostalCode("invalid-zip")
                .phoneNumber("invalid-phone")
                .build();
    }

    /**
     * Get random country
     */
    public static String getRandomCountry() {
        return COUNTRIES[random.nextInt(COUNTRIES.length)];
    }

    /**
     * Get all available countries
     */
    public static String[] getAllCountries() {
        return COUNTRIES.clone();
    }

    /**
     * Get random shipping method
     */
    public static String getRandomShippingMethod() {
        String method = SHIPPING_METHODS[random.nextInt(SHIPPING_METHODS.length)];
        logger.debug("Selected shipping method: {}", method);
        return method;
    }

    /**
     * Get all shipping methods
     */
    public static String[] getAllShippingMethods() {
        return SHIPPING_METHODS.clone();
    }

    /**
     * Get random payment method
     */
    public static String getRandomPaymentMethod() {
        String method = PAYMENT_METHODS[random.nextInt(PAYMENT_METHODS.length)];
        logger.debug("Selected payment method: {}", method);
        return method;
    }

    /**
     * Get all payment methods
     */
    public static String[] getAllPaymentMethods() {
        return PAYMENT_METHODS.clone();
    }

    /**
     * Create random payment information
     */
    public static PaymentInfo createRandomPaymentInfo() {
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .cardHolderName(faker.name().fullName())
                .cardNumber(generateValidCardNumber())
                .cardType(getRandomCardType())
                .expirationMonth(String.format("%02d", random.nextInt(12) + 1))
                .expirationYear(String.valueOf(2024 + random.nextInt(5)))
                .cvv(String.format("%03d", random.nextInt(1000)))
                .build();

        logger.debug("Created random payment info for: {}", paymentInfo.getCardHolderName());
        return paymentInfo;
    }

    /**
     * Create test payment information
     */
    public static PaymentInfo createTestPaymentInfo() {
        return PaymentInfo.builder()
                .cardHolderName("John Doe")
                .cardNumber("4111111111111111") // Valid test Visa number
                .cardType("Visa")
                .expirationMonth("12")
                .expirationYear("2025")
                .cvv("123")
                .build();
    }

    /**
     * Create invalid payment information for negative testing
     */
    public static PaymentInfo createInvalidPaymentInfo() {
        return PaymentInfo.builder()
                .cardHolderName("") // Empty name
                .cardNumber("1234") // Invalid card number
                .cardType("Invalid")
                .expirationMonth("13") // Invalid month
                .expirationYear("2020") // Expired year
                .cvv("12") // Invalid CVV length
                .build();
    }

    /**
     * Get random card type
     */
    public static String getRandomCardType() {
        return CARD_TYPES[random.nextInt(CARD_TYPES.length)];
    }

    /**
     * Generate valid card number based on type
     */
    public static String generateValidCardNumber() {
        // Using standard test card numbers
        String[] testCards = {
            "4111111111111111", // Visa
            "5555555555554444", // MasterCard
            "378282246310005",  // American Express
            "6011111111111117"  // Discover
        };
        return testCards[random.nextInt(testCards.length)];
    }

    /**
     * Generate invalid card number for negative testing
     */
    public static String generateInvalidCardNumber() {
        String[] invalidCards = {
            "1234567890123456", // Invalid format
            "4111111111111112", // Invalid checksum
            "123",              // Too short
            "41111111111111111111", // Too long
            "abcd1234efgh5678", // Contains letters
            "4111-1111-1111-1111" // Contains dashes
        };
        return invalidCards[random.nextInt(invalidCards.length)];
    }

    /**
     * Generate checkout test data for data-driven testing
     */
    public static Object[][] getCheckoutTestData() {
        return new Object[][]{
            {createTestBillingAddress(), createTestShippingAddress(), "Ground", "Credit Card", true},
            {createRandomBillingAddress(), createRandomShippingAddress(), "Express", "Check / Money Order", true},
            {createTestBillingAddress(), null, "Standard", "Cash On Delivery (COD)", true}, // Same address
            {createInvalidAddress(), createTestShippingAddress(), "Ground", "Credit Card", false}, // Invalid billing
            {createTestBillingAddress(), createInvalidAddress(), "Ground", "Credit Card", false} // Invalid shipping
        };
    }

    /**
     * Generate payment test data
     */
    public static Object[][] getPaymentTestData() {
        return new Object[][]{
            {createTestPaymentInfo(), true, "Valid payment info should be accepted"},
            {createInvalidPaymentInfo(), false, "Invalid payment info should be rejected"},
            {createRandomPaymentInfo(), true, "Random valid payment info should work"}
        };
    }
}