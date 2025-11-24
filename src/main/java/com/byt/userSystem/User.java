package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class User {

    private String firstName;
    private String lastName;
    private String familyName;
    private LocalDate dateOfBirth;
    private String phoneNumber;


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
