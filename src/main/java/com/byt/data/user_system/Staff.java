package com.byt.data.user_system;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public abstract class Staff extends User {

    private LocalDate hireDate;

    public Staff(String firstName, String lastName, String familyName,
                 LocalDate dateOfBirth, String phoneNumber, String email,
                 LocalDate hireDate) {

        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email);
        this.hireDate = hireDate;
    }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
}
