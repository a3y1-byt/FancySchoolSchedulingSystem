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

    private String superAdminEmail;
    private Set<String> supervisedAdminEmails = new HashSet<>();

    private transient Admin superAdmin;
    private transient Set<Admin> supervisedAdmins = new HashSet<>();


    public Admin(String firstName, String lastName, String familyName,
                 LocalDate dateOfBirth, String phoneNumber, String email,
                 LocalDate hireDate, LocalDateTime lastLoginTime, Admin superAdmin) {

        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, hireDate);
        this.lastLoginTime = lastLoginTime;
        if (superAdmin != null) {
            addSuperAdmin(superAdmin);
        }
    }

    public LocalDateTime getLastLoginTime() { return lastLoginTime; }

    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public Admin getSuperAdmin() { return superAdmin; }

    public String getSuperAdminEmail() {
        return superAdminEmail;
    }

    public void addSuperAdmin(Admin newSuperAdmin) {
        if (newSuperAdmin == null) return;

        if (this.superAdmin != null) {
            this.superAdmin.supervisedAdmins.remove(this);
            this.superAdmin.supervisedAdminEmails.remove(this.getEmail());
        }

        this.superAdmin = newSuperAdmin;
        this.superAdminEmail = newSuperAdmin.getEmail();

        newSuperAdmin.supervisedAdmins.add(this);
        newSuperAdmin.supervisedAdminEmails.add(this.getEmail());
    }

    public void removeSuperAdmin() {
        if (this.superAdmin == null) return;

        Admin old = this.superAdmin;

        old.supervisedAdmins.remove(this);
        old.supervisedAdminEmails.remove(this.getEmail());

        this.superAdmin = null;
        this.superAdminEmail = null;
    }


    public Set<Admin> getSupervisedAdmins() {
        return new HashSet<>(supervisedAdmins);
    }

    public void addSupervisedAdmin(Admin admin) {
        if (admin == null) return;

        if (supervisedAdmins.contains(admin)) return;

        if (admin.superAdmin != null) {
            admin.superAdmin.supervisedAdmins.remove(admin);
            admin.superAdmin.supervisedAdminEmails.remove(admin.getEmail());
        }

        supervisedAdmins.add(admin);
        supervisedAdminEmails.add(admin.getEmail());

        admin.superAdmin = this;
        admin.superAdminEmail = this.getEmail();
    }

    public void removeSupervisedAdmin(Admin admin) {
        if (admin == null) return;

        if (!supervisedAdmins.contains(admin)) return;

        supervisedAdmins.remove(admin);
        supervisedAdminEmails.remove(admin.getEmail());

        if (admin.superAdmin == this) {
            admin.superAdmin = null;
            admin.superAdminEmail = null;
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

        out.superAdminEmail = admin.superAdminEmail;
        out.supervisedAdminEmails = new HashSet<>(admin.supervisedAdminEmails);

        return out;
    }
}
