package models;


/**
 * Model class representing address information for billing and shipping
 * Used in checkout processes and user account management
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

    // Constructors
    public Address() {
    }

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

    /**
     * Get full name (first + last)
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Get full address as a single string
     */
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();

        if (address1 != null && !address1.trim().isEmpty()) {
            fullAddress.append(address1);
        }

        if (address2 != null && !address2.trim().isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(address2);
        }

        if (city != null && !city.trim().isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(city);
        }

        if (state != null && !state.trim().isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(state);
        }

        if (zipPostalCode != null && !zipPostalCode.trim().isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(" ");
            }
            fullAddress.append(zipPostalCode);
        }

        if (country != null && !country.trim().isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(country);
        }

        return fullAddress.toString();
    }

    /**
     * Check if address is valid (has required fields)
     */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               address1 != null && !address1.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               zipPostalCode != null && !zipPostalCode.trim().isEmpty() &&
               country != null && !country.trim().isEmpty();
    }

    /**
     * Check if this is a complete address with all optional fields
     */
    public boolean isComplete() {
        return isValid() &&
               email != null && !email.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Address{name='%s', address='%s', city='%s', state='%s', zip='%s', country='%s'}",
                getFullName(), address1, city, state, zipPostalCode, country);
    }

    /**
     * Create a copy of this address
     */
    public Address copy() {
        return new Address(this.firstName, this.lastName, this.email, this.company,
                this.country, this.state, this.city, this.address1, this.address2,
                this.zipPostalCode, this.phoneNumber, this.faxNumber);
    }

    public static AddressBuilder builder() {
        return new AddressBuilder();
    }

    public static class AddressBuilder {
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

        public AddressBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AddressBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AddressBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AddressBuilder company(String company) {
            this.company = company;
            return this;
        }

        public AddressBuilder country(String country) {
            this.country = country;
            return this;
        }

        public AddressBuilder state(String state) {
            this.state = state;
            return this;
        }

        public AddressBuilder city(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder address1(String address1) {
            this.address1 = address1;
            return this;
        }

        public AddressBuilder address2(String address2) {
            this.address2 = address2;
            return this;
        }

        public AddressBuilder zipPostalCode(String zipPostalCode) {
            this.zipPostalCode = zipPostalCode;
            return this;
        }

        public AddressBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public AddressBuilder faxNumber(String faxNumber) {
            this.faxNumber = faxNumber;
            return this;
        }

        public Address build() {
            return new Address(firstName, lastName, email, company, country, state,
                    city, address1, address2, zipPostalCode, phoneNumber, faxNumber);
        }
    }

    /**
     * Check if two addresses are the same
     */
    public boolean isSameAs(Address other) {
        if (other == null) return false;

        return equals(other);
    }

    /**
     * Get a display string for the address (formatted for UI)
     */
    public String getDisplayString() {
        StringBuilder display = new StringBuilder();

        display.append(getFullName());

        if (company != null && !company.trim().isEmpty()) {
            display.append("\n").append(company);
        }

        display.append("\n").append(address1);

        if (address2 != null && !address2.trim().isEmpty()) {
            display.append("\n").append(address2);
        }

        display.append("\n").append(city);

        if (state != null && !state.trim().isEmpty()) {
            display.append(", ").append(state);
        }

        display.append(" ").append(zipPostalCode);
        display.append("\n").append(country);

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            display.append("\nPhone: ").append(phoneNumber);
        }

        return display.toString();
    }
}