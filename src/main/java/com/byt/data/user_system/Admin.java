package com.byt.data.user_system;

import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.AdminValidator;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class Admin extends Staff {

    private LocalDateTime lastLoginTime;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private Admin superAdmin;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private Set<Admin> supervisedAdmins = new HashSet<>();

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

    public void addSuperAdmin(Admin newSuperAdmin) {
        AdminValidator.validateClass(newSuperAdmin);

        if (this.superAdmin == newSuperAdmin) return;

        // detach from old
        if (this.superAdmin != null) {
            this.superAdmin.supervisedAdmins.remove(this);
        }

        // attach to new
        this.superAdmin = newSuperAdmin;
        newSuperAdmin.supervisedAdmins.add(this);
    }

    public void removeSuperAdmin(Admin superAdmin) {
        AdminValidator.validateClass(superAdmin);

        if (this.superAdmin == null || !this.superAdmin.equals(superAdmin)) return;

        this.superAdmin = null;
        superAdmin.supervisedAdmins.remove(this);
    }


    public Set<Admin> getSupervisedAdmins() {
        return new HashSet<>(supervisedAdmins);
    }

    public void addSupervisedAdmin(Admin admin) {
        AdminValidator.validateClass(admin);

        if (supervisedAdmins.contains(admin)) return;

        // detach admin from old super
        if (admin.superAdmin != null) {
            admin.superAdmin.supervisedAdmins.remove(admin);
        }

        supervisedAdmins.add(admin);
        admin.superAdmin = this;
    }

    public void removeSupervisedAdmin(Admin admin) {
        AdminValidator.validateClass(admin);

        if (!supervisedAdmins.contains(admin)) return;

        supervisedAdmins.remove(admin);

        if (admin.superAdmin == this) {
            admin.superAdmin = null;
        }
    }


    public static Admin copy(Admin admin) {
        if (admin == null) return null;

        Admin out = new Admin(
                admin.getFirstName(),
                admin.getLastName(),
                admin.getFamilyName(),
                admin.getDateOfBirth(),
                admin.getPhoneNumber(),
                admin.getEmail(),
                admin.getHireDate(),
                admin.getLastLoginTime(),
                null
        );

        return out;
    }
}
