package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class Teacher extends Staff {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String position;

    public Teacher(String firstName, String lastName,
                   String familyName, LocalDate dateOfBirth,
                   String phoneNumber, String email,
                   LocalDate hireDate, String title,
                   String position) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, hireDate);
        this.title = title;
        this.position = position;
    }

    public Teacher() {
        super();
    }

}
