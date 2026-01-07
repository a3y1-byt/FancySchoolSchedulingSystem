package com.byt.data.user_system;

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
    private String superadminId;

    public Admin(String firstName, String lastName, String familyName,
                 LocalDate dateOfBirth, String phoneNumber, String email,
                 LocalDate  hireDate, LocalDateTime lastLoginTime,String superadminId) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate);

        this.lastLoginTime = lastLoginTime;
        this.superadminId = superadminId;
    }

    public static Admin copy(Admin admin) {
        if (admin == null) return null;

        return new Admin(
                admin.getFirstName(),
                admin.getLastName(),
                admin.getFamilyName(),
                admin.getDateOfBirth(),
                admin.getPhoneNumber(),
                admin.getEmail(),
                admin.getHireDate(),
                admin.getLastLoginTime(),
                admin.getSuperadminId()
        );
    }

}
