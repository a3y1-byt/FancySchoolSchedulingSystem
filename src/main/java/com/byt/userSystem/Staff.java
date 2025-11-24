package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Staff extends User {

    @Getter
    @Setter
    private LocalDate hireDate;

    public Staff(String firstName,
                 String lastName,
                 String familyName,
                 LocalDate dateOfBirth,
                 String phoneNumber,
                 String email,
                 LocalDate hireDate) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email);
        this.hireDate = hireDate;
    }

    public Staff() {
        super();
    }
}

