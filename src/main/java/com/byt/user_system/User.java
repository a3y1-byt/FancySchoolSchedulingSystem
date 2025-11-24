package com.byt.user_system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

    private String firstName;
    private String lastName;
    private String familyName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;

    private void viewSchedule() {
    }

    private void manage() {
    }

}
