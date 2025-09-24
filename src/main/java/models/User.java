package models;

import java.util.Date;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private Date dateOfBirth;
    private String company;
    private boolean newsletter;

    // Private constructor for builder pattern
    private User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.gender = builder.gender;
        this.dateOfBirth = builder.dateOfBirth;
        this.company = builder.company;
        this.newsletter = builder.newsletter;
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String gender;
        private Date dateOfBirth;
        private String company;
        private boolean newsletter;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder dateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder newsletter(boolean newsletter) {
            this.newsletter = newsletter;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCompany() {
        return company;
    }

    public boolean isNewsletter() {
        return newsletter;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Utility methods
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", company='" + company + '\'' +
                ", newsletter=" + newsletter +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return email != null ? email.equals(user.email) : user.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}