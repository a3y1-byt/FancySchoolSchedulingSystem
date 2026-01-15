package com.byt.services.user_system;

import com.byt.data.user_system.Admin;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.services.reporting.IssueReportService;
import com.byt.validation.user_system.AdminValidator;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AdminService implements CRUDService<Admin> {

    private final SaveLoadService service;
    private final IssueReportService issueReportService; // can be null
    private List<Admin> admins;

    private static final Type ADMIN_LIST_TYPE = new TypeToken<List<Admin>>() {}.getType();

    public AdminService(SaveLoadService service) {
        this(service, null, null);
    public AdminService(SaveLoadService service, List<Admin> admins) {
        this.service = service;
        this.admins = admins != null ? new ArrayList<>(admins) : new ArrayList<>();
    }

    public AdminService(SaveLoadService service, IssueReportService issueReportService) {
        this(service, null, issueReportService);
    }

    public AdminService(SaveLoadService service, List<Admin> admins, IssueReportService issueReportService) {
        this.service = service;
        this.issueReportService = issueReportService;
        this.admins = admins != null ? copyList(admins) : new ArrayList<>();
    }

    @Override
    public void initialize() throws IOException {
        List<Admin> loaded = loadFromDb();
        this.admins = loaded != null ? loaded : new ArrayList<>();
        this.admins = new ArrayList<>(loaded);
    }

    public Admin create(String firstName, String lastName, String familyName,
                        LocalDate dateOfBirth, String phoneNumber, String email,
                        LocalDate hireDate, LocalDateTime lastLoginTime,
                        String superAdminEmail) throws IOException {

        AdminValidator.validateAdmin(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime
        );

        if (exists(email)) {
            throw new IllegalStateException("Admin exists with this email already");
        }

        Admin created = new Admin(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime,
                null
        );

        admins.add(created);

        if (superAdminEmail != null && !superAdminEmail.isBlank()) {
            Admin superInternal = findInternal(superAdminEmail)
                    .orElseThrow(() -> new IllegalArgumentException("SuperAdmin with email=" + superAdminEmail + " not found"));
            created.addSuperAdmin(superInternal);
        }

        saveToDb();
        return Admin.copy(created);
    }

    @Override
    public void create(Admin prototype) throws IllegalArgumentException, IOException {
        AdminValidator.validateClass(prototype);

        String email = prototype.getEmail();
        if (exists(email)) {
            throw new IllegalArgumentException("Admin with email=" + email + " already exists");
        }

        Admin created = new Admin(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getHireDate(),
                prototype.getLastLoginTime(),
                null
        );

        admins.add(created);

        if (prototype.getSuperAdmin() != null) {
            String se = prototype.getSuperAdmin().getEmail();
            Admin superInternal = findInternal(se)
                    .orElseThrow(() -> new IllegalArgumentException("SuperAdmin with email=" + se + " not found"));
            created.addSuperAdmin(superInternal);
        }

        saveToDb();
    }

    @Override
    public Optional<Admin> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
        return findInternal(email).map(Admin::copy);
    }

    @Override
    public List<Admin> getAll() throws IOException {
        return copyList(admins);
    }

    @Override
    public void update(String email, Admin prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
        AdminValidator.validateClass(prototype);

        String oldEmail = email;

        Admin current = findInternal(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin with email=" + email + " not found"));

        String newEmail = prototype.getEmail();
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("new email must not be null or blank");
        }
        if (!Objects.equals(newEmail, email) && exists(newEmail)) {
            throw new IllegalArgumentException("Admin with email=" + newEmail + " already exists");
        }

        current.setFirstName(prototype.getFirstName());
        current.setLastName(prototype.getLastName());
        current.setFamilyName(prototype.getFamilyName());
        current.setDateOfBirth(prototype.getDateOfBirth());
        current.setPhoneNumber(prototype.getPhoneNumber());
        current.setEmail(newEmail);
        current.setHireDate(prototype.getHireDate());
        current.setLastLoginTime(prototype.getLastLoginTime());

        Admin desiredSuper = null;
        if (prototype.getSuperAdmin() != null) {
            String se = prototype.getSuperAdmin().getEmail();
            desiredSuper = findInternal(se)
                    .orElseThrow(() -> new IllegalArgumentException("SuperAdmin with email=" + se + " not found"));
            if (desiredSuper == current) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
        }

        Admin oldSuper = current.getSuperAdmin();
        if (!Objects.equals(oldSuper, desiredSuper)) {
            if (oldSuper != null) current.removeSuperAdmin(oldSuper);
            if (desiredSuper != null) current.addSuperAdmin(desiredSuper);
        }

        saveToDb();

        if (issueReportService != null && !Objects.equals(oldEmail, newEmail)) {
            issueReportService.updateReporterEmail(oldEmail, newEmail);
        }
    }

    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        Admin toDelete = findInternal(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin with email=" + email + " not found"));

        if (hasSubordinates(toDelete)) {
            throw new IllegalStateException("Admin supervises other admins. Use deleteSuperAdmin(oldEmail, newSuperEmail)");
        }

        if (toDelete.getSuperAdmin() != null) {
            toDelete.removeSuperAdmin(toDelete.getSuperAdmin());
        }

        admins.remove(toDelete);
        saveToDb();
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) return false;
        return findInternal(email).isPresent();
    }

    public void assignSupervisor(String adminEmail, String superAdminEmail) throws IOException {
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalArgumentException("adminEmail must not be blank");
        }
        if (superAdminEmail == null || superAdminEmail.isBlank()) {
            throw new IllegalArgumentException("superAdminEmail must not be blank");
        }

        Admin admin = findInternal(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin with email=" + adminEmail + " not found"));
        Admin superAdmin = findInternal(superAdminEmail)
                .orElseThrow(() -> new IllegalArgumentException("SuperAdmin with email=" + superAdminEmail + " not found"));

        admin.addSuperAdmin(superAdmin);
        saveToDb();
    }

    public void deleteSuperAdmin(String oldSuperEmail, String newSuperEmail) throws IOException {
        if (oldSuperEmail == null || oldSuperEmail.isBlank()) {
            throw new IllegalArgumentException("oldSuperEmail must not be blank");
        }

        Admin oldSuper = findInternal(oldSuperEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin with email=" + oldSuperEmail + " not found"));

        List<Admin> subs = getSubordinates(oldSuper);

        if (!subs.isEmpty()) {
            if (newSuperEmail == null || newSuperEmail.isBlank()) {
                throw new IllegalArgumentException("newSuperEmail must not be blank");
            }

            Admin newSuper = findInternal(newSuperEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Admin with email=" + newSuperEmail + " not found"));

            if (newSuper == oldSuper) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }

            for (Admin sub : subs) {
                sub.addSuperAdmin(newSuper);
            }
        }

        if (oldSuper.getSuperAdmin() != null) {
            oldSuper.removeSuperAdmin(oldSuper.getSuperAdmin());
        }

        admins.remove(oldSuper);
        saveToDb();
    }

    private Optional<Admin> findInternal(String email) {
        for (Admin a : admins) {
            if (Objects.equals(a.getEmail(), email)) return Optional.of(a);
        }
        return Optional.empty();
    }

    private boolean hasSubordinates(Admin superAdmin) {
        for (Admin a : admins) {
            if (a.getSuperAdmin() != null && a.getSuperAdmin() == superAdmin) {
                return true;
            }
        }
        return false;
    }

    private List<Admin> getSubordinates(Admin superAdmin) {
        List<Admin> res = new ArrayList<>();
        for (Admin a : admins) {
            if (a.getSuperAdmin() != null && a.getSuperAdmin() == superAdmin) {
                res.add(a);
            }
        }
        return res;
    }

    private List<Admin> copyList(List<Admin> source) {
        List<Admin> result = new ArrayList<>();
        if (source == null) return result;

        for (Admin a : source) {
            result.add(Admin.copy(a));
        }
        return result;
    }

    private List<Admin> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.ADMINS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.ADMINS, ADMIN_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<Admin> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Admin admin) result.add(admin);
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.ADMINS, admins);
    }
}
