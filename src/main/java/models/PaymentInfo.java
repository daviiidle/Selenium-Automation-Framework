package models;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Model class representing payment information for checkout processes
 * Contains credit card and payment method details
 */
public class PaymentInfo {
    private String cardHolderName;
    private String cardNumber;
    private String cardType;
    private String expirationMonth;
    private String expirationYear;
    private String cvv;
    private String paymentMethod;
    private boolean savePaymentInfo;

    // Constructors
    public PaymentInfo() {
    }

    public PaymentInfo(String cardHolderName, String cardNumber, String cardType,
                       String expirationMonth, String expirationYear, String cvv,
                       String paymentMethod, boolean savePaymentInfo) {
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cvv = cvv;
        this.paymentMethod = paymentMethod;
        this.savePaymentInfo = savePaymentInfo;
    }

    // Getters
    public String getCardHolderName() { return cardHolderName; }
    public String getCardNumber() { return cardNumber; }
    public String getCardType() { return cardType; }
    public String getExpirationMonth() { return expirationMonth; }
    public String getExpirationYear() { return expirationYear; }
    public String getCvv() { return cvv; }
    public String getPaymentMethod() { return paymentMethod; }
    public boolean isSavePaymentInfo() { return savePaymentInfo; }

    // Setters
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setExpirationMonth(String expirationMonth) { this.expirationMonth = expirationMonth; }
    public void setExpirationYear(String expirationYear) { this.expirationYear = expirationYear; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setSavePaymentInfo(boolean savePaymentInfo) { this.savePaymentInfo = savePaymentInfo; }

    // Regex patterns for validation
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9]{13,19}$");
    private static final Pattern CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$");
    private static final Pattern MONTH_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])$");
    private static final Pattern YEAR_PATTERN = Pattern.compile("^20[0-9]{2}$");

    /**
     * Get masked card number (show only last 4 digits)
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    /**
     * Get formatted expiration date (MM/YY)
     */
    public String getFormattedExpirationDate() {
        if (expirationMonth == null || expirationYear == null) {
            return "";
        }
        String year = expirationYear.length() > 2 ? expirationYear.substring(2) : expirationYear;
        return String.format("%s/%s", expirationMonth, year);
    }

    /**
     * Get formatted expiration date (MM/YYYY)
     */
    public String getFormattedExpirationDateFull() {
        if (expirationMonth == null || expirationYear == null) {
            return "";
        }
        return String.format("%s/%s", expirationMonth, expirationYear);
    }

    /**
     * Validate card number format
     */
    public boolean isCardNumberValid() {
        if (cardNumber == null) {
            return false;
        }
        String cleanCardNumber = cardNumber.replaceAll("[\\s-]", "");
        return CARD_NUMBER_PATTERN.matcher(cleanCardNumber).matches() && passesLuhnCheck(cleanCardNumber);
    }

    /**
     * Validate CVV format
     */
    public boolean isCvvValid() {
        return cvv != null && CVV_PATTERN.matcher(cvv).matches();
    }

    /**
     * Validate expiration month
     */
    public boolean isExpirationMonthValid() {
        return expirationMonth != null && MONTH_PATTERN.matcher(expirationMonth).matches();
    }

    /**
     * Validate expiration year
     */
    public boolean isExpirationYearValid() {
        if (expirationYear == null || !YEAR_PATTERN.matcher(expirationYear).matches()) {
            return false;
        }
        int year = Integer.parseInt(expirationYear);
        int currentYear = LocalDate.now().getYear();
        return year >= currentYear && year <= currentYear + 10; // Valid for next 10 years
    }

    /**
     * Check if card is expired
     */
    public boolean isCardExpired() {
        if (!isExpirationMonthValid() || !isExpirationYearValid()) {
            return true;
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = LocalDate.of(
            Integer.parseInt(expirationYear),
            Integer.parseInt(expirationMonth),
            1
        ).plusMonths(1).minusDays(1); // Last day of expiration month

        return expirationDate.isBefore(currentDate);
    }

    /**
     * Validate cardholder name
     */
    public boolean isCardHolderNameValid() {
        return cardHolderName != null &&
               !cardHolderName.trim().isEmpty() &&
               cardHolderName.trim().length() >= 2 &&
               cardHolderName.matches("^[a-zA-Z\\s.'-]+$");
    }

    /**
     * Check if all payment info is valid
     */
    public boolean isValid() {
        return isCardHolderNameValid() &&
               isCardNumberValid() &&
               isCvvValid() &&
               isExpirationMonthValid() &&
               isExpirationYearValid() &&
               !isCardExpired();
    }

    /**
     * Luhn algorithm check for card number validation
     */
    private boolean passesLuhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10) == 0;
    }

    /**
     * Determine card type from card number
     */
    public String determineCardType() {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "Unknown";
        }

        String cleanCardNumber = cardNumber.replaceAll("[\\s-]", "");

        if (cleanCardNumber.startsWith("4")) {
            return "Visa";
        } else if (cleanCardNumber.startsWith("5") || cleanCardNumber.startsWith("2")) {
            return "MasterCard";
        } else if (cleanCardNumber.startsWith("34") || cleanCardNumber.startsWith("37")) {
            return "American Express";
        } else if (cleanCardNumber.startsWith("6")) {
            return "Discover";
        } else {
            return "Unknown";
        }
    }

    /**
     * Get clean card number (remove spaces and dashes)
     */
    public String getCleanCardNumber() {
        if (cardNumber == null) {
            return "";
        }
        return cardNumber.replaceAll("[\\s-]", "");
    }

    @Override
    public String toString() {
        return String.format("PaymentInfo{cardHolder='%s', cardType='%s', cardNumber='%s', expiration='%s', valid=%s}",
                cardHolderName, cardType, getMaskedCardNumber(), getFormattedExpirationDate(), isValid());
    }

    public static PaymentInfoBuilder builder() {
        return new PaymentInfoBuilder();
    }

    public static class PaymentInfoBuilder {
        private String cardHolderName;
        private String cardNumber;
        private String cardType;
        private String expirationMonth;
        private String expirationYear;
        private String cvv;
        private String paymentMethod;
        private boolean savePaymentInfo;

        public PaymentInfoBuilder cardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public PaymentInfoBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public PaymentInfoBuilder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public PaymentInfoBuilder expirationMonth(String expirationMonth) {
            this.expirationMonth = expirationMonth;
            return this;
        }

        public PaymentInfoBuilder expirationYear(String expirationYear) {
            this.expirationYear = expirationYear;
            return this;
        }

        public PaymentInfoBuilder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public PaymentInfoBuilder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentInfoBuilder savePaymentInfo(boolean savePaymentInfo) {
            this.savePaymentInfo = savePaymentInfo;
            return this;
        }

        public PaymentInfo build() {
            return new PaymentInfo(cardHolderName, cardNumber, cardType,
                    expirationMonth, expirationYear, cvv, paymentMethod, savePaymentInfo);
        }
    }

    /**
     * Create a copy of this payment info (for security, exclude sensitive data)
     */
    public PaymentInfo copyForDisplay() {
        return PaymentInfo.builder()
                .cardHolderName(this.cardHolderName)
                .cardNumber(getMaskedCardNumber())
                .cardType(this.cardType)
                .expirationMonth(this.expirationMonth)
                .expirationYear(this.expirationYear)
                .cvv("***") // Never copy CVV
                .paymentMethod(this.paymentMethod)
                .savePaymentInfo(this.savePaymentInfo)
                .build();
    }
}