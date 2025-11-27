package com.byt.user_system.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public abstract class Staff extends User {

    private LocalDate  hireDate;

    public Staff(String firstName, String lastName, String familyName,
                 LocalDate dateOfBirth, String phoneNumber, String email,
                 LocalDate hireDate) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email);

        this.hireDate = hireDate;
    }
}

