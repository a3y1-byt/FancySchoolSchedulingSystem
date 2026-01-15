package com.byt.data.user_system;

import com.byt.validation.user_system.AdminValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"superAdmin", "supervisedAdmins"})
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class Admin extends Staff {

    private LocalDateTime lastLoginTime;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient Admin superAdmin;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient Set<Admin> supervisedAdmins = new HashSet<>();

    public Admin(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            LocalDate hireDate,
            LocalDateTime lastLoginTime,
            Admin superAdmin
    ) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, hireDate);
        this.lastLoginTime = lastLoginTime;
        this.superAdmin = superAdmin;
    }

    public LocalDateTime getLastLoginTime() {return lastLoginTime;}

    public void setLastLoginTime(LocalDateTime lastLoginTime) {this.lastLoginTime = lastLoginTime;}

    public Admin getSuperAdmin() {return superAdmin;}

    public Set<Admin> getSupervisedAdmins() {return new HashSet<>(supervisedAdmins);}


    public void addSuperAdmin(Admin newSuperAdmin) {
        AdminValidator.validateClass(newSuperAdmin);

        if (newSuperAdmin == this) {
            throw new IllegalArgumentException("Admin cannot supervise himself");
        }

        if (Objects.equals(this.superAdmin, newSuperAdmin)) {
            return;
        }

        if (this.superAdmin != null) {
            Admin old = this.superAdmin;
            this.superAdmin = null;
            old.supervisedAdmins.remove(this);
        }

        this.superAdmin = newSuperAdmin;
        newSuperAdmin.supervisedAdmins.add(this);
    }

    public void removeSuperAdmin(Admin expectedSuperAdmin) {
        if (this.superAdmin == null) {
            return;
        }

        if (!Objects.equals(this.superAdmin, expectedSuperAdmin)) {
            return;
        }

        Admin old = this.superAdmin;
        this.superAdmin = null;
        old.supervisedAdmins.remove(this);
    }

    public void addSupervisedAdmin(Admin admin) {
        AdminValidator.validateClass(admin);

        if (admin == this) {
            throw new IllegalArgumentException("Admin cannot supervise himself");
        }

        admin.addSuperAdmin(this);
    }

    public void removeSupervisedAdmin(Admin admin) {
        AdminValidator.validateClass(admin);

        admin.removeSuperAdmin(this);
    }

    public static Admin copy(Admin admin) {
        if (admin == null) return null;

        Admin c = new Admin(
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

        c.supervisedAdmins = new HashSet<>(admin.supervisedAdmins);

        return c;
    }
}
