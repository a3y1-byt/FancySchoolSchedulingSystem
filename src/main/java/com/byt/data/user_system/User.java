package com.byt.data.user_system;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class User {

    private String firstName;
    private String lastName;
    private String familyName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;

    public User(String firstName, String lastName,
                String familyName, LocalDate  dateOfBirth,
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
