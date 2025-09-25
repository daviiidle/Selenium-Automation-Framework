package models;

/**
 * PaymentInfo model class for storing payment information
 * Supports builder pattern for easy construction and validation
 */
public class PaymentInfo {
    private String cardHolderName;
    private String cardNumber;
    private String cardType;
    private String expirationMonth;
    private String expirationYear;
    private String cvv;
    private String billingAddress;
    private String paymentMethod;

    // Default constructor
    public PaymentInfo() {}

    // Constructor with all fields
    public PaymentInfo(String cardHolderName, String cardNumber, String cardType,
                      String expirationMonth, String expirationYear, String cvv,
                      String billingAddress, String paymentMethod) {
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cvv = cvv;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public String getCardHolderName() { return cardHolderName; }
    public String getCardNumber() { return cardNumber; }
    public String getCardType() { return cardType; }
    public String getExpirationMonth() { return expirationMonth; }
    public String getExpirationYear() { return expirationYear; }
    public String getCvv() { return cvv; }
    public String getBillingAddress() { return billingAddress; }
    public String getPaymentMethod() { return paymentMethod; }

    // Setters
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setExpirationMonth(String expirationMonth) { this.expirationMonth = expirationMonth; }
    public void setExpirationYear(String expirationYear) { this.expirationYear = expirationYear; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String cardHolderName;
        private String cardNumber;
        private String cardType;
        private String expirationMonth;
        private String expirationYear;
        private String cvv;
        private String billingAddress;
        private String paymentMethod;

        public Builder cardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; return this; }
        public Builder cardNumber(String cardNumber) { this.cardNumber = cardNumber; return this; }
        public Builder cardType(String cardType) { this.cardType = cardType; return this; }
        public Builder expirationMonth(String expirationMonth) { this.expirationMonth = expirationMonth; return this; }
        public Builder expirationYear(String expirationYear) { this.expirationYear = expirationYear; return this; }
        public Builder cvv(String cvv) { this.cvv = cvv; return this; }
        public Builder billingAddress(String billingAddress) { this.billingAddress = billingAddress; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }

        public PaymentInfo build() {
            return new PaymentInfo(cardHolderName, cardNumber, cardType, expirationMonth,
                                 expirationYear, cvv, billingAddress, paymentMethod);
        }
    }

    /**
     * Get formatted card number with masking for security
     * @return Masked card number (e.g., ****-****-****-1234)
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****-****-****-****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "****-****-****-" + lastFour;
    }

    /**
     * Validate if the payment info is complete for processing
     * @return true if all required fields are present
     */
    public boolean isValid() {
        return cardHolderName != null && !cardHolderName.trim().isEmpty() &&
               cardNumber != null && !cardNumber.trim().isEmpty() &&
               cardType != null && !cardType.trim().isEmpty() &&
               expirationMonth != null && !expirationMonth.trim().isEmpty() &&
               expirationYear != null && !expirationYear.trim().isEmpty() &&
               cvv != null && !cvv.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("PaymentInfo{cardHolderName='%s', cardType='%s', " +
                           "cardNumber='%s', expirationMonth='%s', expirationYear='%s'}",
                           cardHolderName, cardType, getMaskedCardNumber(),
                           expirationMonth, expirationYear);
    }
}