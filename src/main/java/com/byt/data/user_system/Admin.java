package com.byt.data.user_system;

import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.AdminValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class Admin extends Staff {

    private LocalDateTime lastLoginTime;
    private Admin superAdmin;

    public Admin(String firstName, String lastName, String familyName,
                 LocalDate dateOfBirth, String phoneNumber, String email,
                 LocalDate hireDate, LocalDateTime lastLoginTime, Admin superAdmin) {

        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, hireDate);
        this.lastLoginTime = lastLoginTime;
        this.superAdmin = superAdmin;
    }

    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public Admin getSuperAdmin() { return superAdmin; }
    public void addSuperAdmin(Admin superAdmin) {
        AdminValidator.validateClass(superAdmin);
        if (this.superAdmin != null) {
            Admin oldsuperAdmin = this.superAdmin;
            this.superAdmin = superAdmin;
            oldsuperAdmin.removeSuperAdmin(this);
        }
        this.superAdmin = superAdmin;
        superAdmin.addSuperAdmin(this);
    }
    public void removeSuperAdmin(Admin superAdmin) {
        if (this.superAdmin == null || !this.superAdmin.equals(superAdmin)) return ;
        Admin  oldsuperAdmin = this.superAdmin;
        this.superAdmin = null;
        oldsuperAdmin.removeSuperAdmin(this);
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
                admin.getSuperAdmin()
        );
    }
}
