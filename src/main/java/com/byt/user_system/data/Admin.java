package com.byt.user_system.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Staff {

    private LocalDateTime lastLoginTime;

    public Admin(String firstName, String lastName, String familyName,
                 LocalDate dateOfBirth, String phoneNumber, String email,
                 LocalDate  hireDate, LocalDateTime lastLoginTime) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate);

        this.lastLoginTime = lastLoginTime;
    }
}
