package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class Admin extends Staff {

    private LocalDateTime lastLoginTime;

    public Admin(String firstName, String lastName,
                 String familyName, LocalDate dateOfBirth,
                 String phoneNumber, String email,
                 LocalDate hireDate, LocalDateTime lastLoginTime) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, hireDate);
        this.lastLoginTime = lastLoginTime;
    }

    public Admin() {
        super();
    }

}
