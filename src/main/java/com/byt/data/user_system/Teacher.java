package com.byt.data.user_system;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Teacher extends Staff {

    private String title;
    private String position;

    public Teacher(String firstName, String lastName, String familyName,
                   LocalDate dateOfBirth, String phoneNumber, String email,
                   LocalDate hireDate, String title,
                   String position) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate);
        this.title = title;
        this.position = position;
    }

}
