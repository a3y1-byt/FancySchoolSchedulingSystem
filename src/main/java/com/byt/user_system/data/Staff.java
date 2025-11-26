package com.byt.user_system.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public abstract class Staff extends User {

    private Instant  hireDate;

    public Staff(String firstName, String lastName, String familyName,
                 Instant  dateOfBirth, String phoneNumber, String email,
                 Instant hireDate) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email);

        this.hireDate = hireDate;
    }
}

