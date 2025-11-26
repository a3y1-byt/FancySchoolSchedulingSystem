package com.byt.user_system.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class Admin extends Staff {

    private Instant lastLoginTime;

    public Admin(String firstName, String lastName, String familyName,
                 Instant  dateOfBirth, String phoneNumber, String email,
                 Instant  hireDate, Instant  lastLoginTime) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate);

        this.lastLoginTime = lastLoginTime;
    }
}
