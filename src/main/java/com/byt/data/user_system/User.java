package com.byt.data.user_system;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@EqualsAndHashCode
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public abstract class User {

    private String firstName;
    private String lastName;
    private String familyName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;

    public User(String firstName, String lastName,
                String familyName, LocalDate dateOfBirth,
                String phoneNumber, String email) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.familyName = familyName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    private void viewSchedule() {}
    private void manage() {}
}
