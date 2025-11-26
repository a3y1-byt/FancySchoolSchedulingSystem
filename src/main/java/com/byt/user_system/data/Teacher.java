package com.byt.user_system.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class Teacher extends Staff {

    private String title;
    private String position;

    public Teacher(String firstName, String lastName, String familyName,
                   Instant  dateOfBirth, String phoneNumber, String email,
                   Instant hireDate, String title,
                   String position) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate);

        this.title = title;
        this.position = position;
    }

}
