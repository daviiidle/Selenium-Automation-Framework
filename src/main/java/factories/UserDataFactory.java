package factories;

import com.github.javafaker.Faker;
import models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class UserDataFactory {
    private static final Logger logger = LogManager.getLogger(UserDataFactory.class);
    private static final Faker faker = new Faker(new Locale("en-US"));
    private static final Random random = new SecureRandom();

    private static final String[] GENDERS = {"Male", "Female"};
    private static final String[] DOMAINS = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "test.com"};

    public static User createRandomUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = generateUniqueEmail(firstName, lastName);
        String password = generateSecurePassword();

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .gender(GENDERS[random.nextInt(GENDERS.length)])
                .dateOfBirth(faker.date().birthday(18, 80))
                .company(faker.company().name())
                .newsletter(random.nextBoolean())
                .build();

        logger.debug("Created random user: {}", user.getEmail());
        return user;
    }

    public static User createUserWithSpecificEmail(String email) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(generateSecurePassword())
                .gender(GENDERS[random.nextInt(GENDERS.length)])
                .dateOfBirth(faker.date().birthday(18, 80))
                .company(faker.company().name())
                .newsletter(random.nextBoolean())
                .build();

        logger.debug("Created user with specific email: {}", email);
        return user;
    }

    public static User createUserWithSpecificData(String firstName, String lastName, String email, String password) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .gender(GENDERS[random.nextInt(GENDERS.length)])
                .dateOfBirth(faker.date().birthday(18, 80))
                .company(faker.company().name())
                .newsletter(random.nextBoolean())
                .build();

        logger.debug("Created user with specific data: {}", email);
        return user;
    }

    public static User createTestUser() {
        return createUserWithSpecificData(
                "Test",
                "User",
                "test.user." + System.currentTimeMillis() + "@test.com",
                "TestPassword123!"
        );
    }

    public static User createAdminUser() {
        return createUserWithSpecificData(
                "Admin",
                "User",
                "admin.user." + System.currentTimeMillis() + "@test.com",
                "AdminPassword123!"
        );
    }

    public static User createGuestUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(generateUniqueEmail(firstName, lastName))
                .password(generateSecurePassword())
                .gender(GENDERS[random.nextInt(GENDERS.length)])
                .build();
    }

    // Additional helper methods for test scenarios
    public static String generateFirstName() {
        return faker.name().firstName();
    }

    public static String generateLastName() {
        return faker.name().lastName();
    }

    public static String generateStrongPassword() {
        return generateSecurePassword();
    }


    public static String generateUniqueEmail(String firstName, String lastName) {
        String domain = DOMAINS[random.nextInt(DOMAINS.length)];
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nanoTime = String.valueOf(System.nanoTime());
        String randomNumber = String.valueOf(random.nextInt(100000)); // Larger range
        String threadId = String.valueOf(Thread.currentThread().getId());

        // Use combination of timestamp, nanotime, thread ID and random for maximum uniqueness
        return (firstName + "." + lastName + "." + timestamp + "." + nanoTime.substring(nanoTime.length() - 6) + "." + threadId + "." + randomNumber + "@" + domain).toLowerCase();
    }

    public static String generateSecurePassword() {
        return generateSecurePassword(12);
    }

    public static String generateSecurePassword(int length) {
        if (length < 8) {
            length = 8;
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCase + lowerCase + digits + specialChars;

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public static String generateInvalidEmail() {
        String[] invalidEmails = {
                "invalid-email",
                "@domain.com",
                "user@",
                "user.domain.com",
                "user@domain",
                "user space@domain.com",
                "user@domain..com"
        };
        return invalidEmails[random.nextInt(invalidEmails.length)];
    }

    public static String generateWeakPassword() {
        String[] weakPasswords = {
                "123456",
                "password",
                "123456789",
                "qwerty",
                "abc123",
                "password123"
        };
        return weakPasswords[random.nextInt(weakPasswords.length)];
    }

    public static User[] createMultipleUsers(int count) {
        User[] users = new User[count];
        for (int i = 0; i < count; i++) {
            users[i] = createRandomUser();
        }
        logger.info("Created {} random users", count);
        return users;
    }
}