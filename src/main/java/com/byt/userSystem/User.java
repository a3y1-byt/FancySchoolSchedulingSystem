package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public abstract class User {

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String familyName;

    @Getter
    @Setter
    private LocalDate dateOfBirth;

    @Getter
    @Setter
    private String phoneNumber;

    @Getter
    @Setter
    private String email;

    protected User() {
    }

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

    private void viewSchedule() {
    }

    private void manage() {
    }

}
