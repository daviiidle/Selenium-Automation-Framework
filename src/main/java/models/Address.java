package models;

/**
 * Address model class for storing address information
 * Supports both setter/getter and builder pattern for flexibility
 */
public class Address {
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private String country;
    private String state;
    private String city;
    private String address1;
    private String address2;
    private String zipPostalCode;
    private String phoneNumber;
    private String faxNumber;

    // Default constructor
    public Address() {}

    // Constructor with all fields
    public Address(String firstName, String lastName, String email, String company,
                  String country, String state, String city, String address1, String address2,
                  String zipPostalCode, String phoneNumber, String faxNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.country = country;
        this.state = state;
        this.city = city;
        this.address1 = address1;
        this.address2 = address2;
        this.zipPostalCode = zipPostalCode;
        this.phoneNumber = phoneNumber;
        this.faxNumber = faxNumber;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getCompany() { return company; }
    public String getCountry() { return country; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getAddress1() { return address1; }
    public String getAddress2() { return address2; }
    public String getZipPostalCode() { return zipPostalCode; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getFaxNumber() { return faxNumber; }

    // Alternative method names for compatibility
    public String getPostalCode() { return zipPostalCode; }
    public String getPhone() { return phoneNumber; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setCompany(String company) { this.company = company; }
    public void setCountry(String country) { this.country = country; }
    public void setState(String state) { this.state = state; }
    public void setCity(String city) { this.city = city; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public void setZipPostalCode(String zipPostalCode) { this.zipPostalCode = zipPostalCode; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setFaxNumber(String faxNumber) { this.faxNumber = faxNumber; }

    // Alternative setter names for compatibility
    public void setPostalCode(String postalCode) { this.zipPostalCode = postalCode; }
    public void setPhone(String phone) { this.phoneNumber = phone; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String company;
        private String country;
        private String state;
        private String city;
        private String address1;
        private String address2;
        private String zipPostalCode;
        private String phoneNumber;
        private String faxNumber;

        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder company(String company) { this.company = company; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder address1(String address1) { this.address1 = address1; return this; }
        public Builder address2(String address2) { this.address2 = address2; return this; }
        public Builder zipPostalCode(String zipPostalCode) { this.zipPostalCode = zipPostalCode; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder faxNumber(String faxNumber) { this.faxNumber = faxNumber; return this; }

        public Address build() {
            return new Address(firstName, lastName, email, company, country, state, city,
                             address1, address2, zipPostalCode, phoneNumber, faxNumber);
        }
    }

    @Override
    public String toString() {
        return String.format("Address{firstName='%s', lastName='%s', email='%s', " +
                           "address1='%s', city='%s', state='%s', country='%s', zipPostalCode='%s'}",
                           firstName, lastName, email, address1, city, state, country, zipPostalCode);
    }
}