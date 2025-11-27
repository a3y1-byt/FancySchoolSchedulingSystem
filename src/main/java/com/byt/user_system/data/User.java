package com.byt.user_system.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

    private String id;
    private String firstName;
    private String lastName;
    private String familyName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;

    public User(String firstName, String lastName,
                String familyName, LocalDate  dateOfBirth,
                String phoneNumber, String email) {

        this.id = java.util.UUID.randomUUID().toString();
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
