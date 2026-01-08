package com.byt.services.user_system;

import com.byt.data.user_system.Admin;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.user_system.AdminValidator;
import com.google.gson.reflect.TypeToken;

import javax.imageio.IIOException;
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
    private List<Admin> admins;

    private static final Type ADMIN_LIST_TYPE = new TypeToken<List<Admin>>() {}.getType();

    public AdminService(SaveLoadService service, List<Admin> admins) {
        this.service = service;
        this.admins = admins != null ? copyList(admins) : new ArrayList<>();
    }

    public AdminService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<Admin> loaded = loadFromDb();
        this.admins = copyList(loaded);
    }

    public Admin create(String firstName, String lastName, String familyName,
                        LocalDate dateOfBirth, String phoneNumber, String email,
                        LocalDate hireDate, LocalDateTime lastLoginTime, Admin superAdmin) throws IOException {

        AdminValidator.validateAdmin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime);

        if (email != null && exists(email)) {
            throw new IllegalStateException("Admin exists with this email already");
        }

        if (superAdmin != null) {
            if (Objects.equals(superAdmin.getEmail(), email)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
            if (!exists(superAdmin)) {
                throw new IllegalArgumentException("Superadmin with email = " + superAdmin.getEmail() + " does not exist");
            }
        }

        Admin admin = new Admin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime, superAdmin
        );

        admins.add(Admin.copy(admin));
        saveToDb();

        return Admin.copy(admin);
    }

    @Override
    public void create(Admin prototype) throws IllegalArgumentException, IOException {
        AdminValidator.validateClass(prototype);

        String email = prototype.getEmail();
        if (email != null && exists(email)) {
            throw new IllegalArgumentException("Admin with email = " + email + " already exists");
        }

        Admin superAdmin = prototype.getSuperAdmin();

        if (superAdmin != null) {
            if (Objects.equals(superAdmin.getEmail(), email)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
            if (!exists(superAdmin)) {
                throw new IllegalArgumentException("Superadmin with email = " + superAdmin.getEmail() + " does not exist");
            }
        }

        admins.add(Admin.copy(prototype));
        saveToDb();
    }

    @Override
    public Optional<Admin> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        for (Admin admin : admins) {
            if (Objects.equals(admin.getEmail(), email)) {
                return Optional.of(Admin.copy(admin));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Admin> getAll() throws IOException {
        return copyList(admins);
    }

    @Override
    public void update(String email, Admin prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        AdminValidator.validateClass(prototype);

        int index = -1;
        for (int i = 0; i < admins.size(); i++) {
            if (Objects.equals(admins.get(i).getEmail(), email)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("Admin with email = " + email + " not found");
        }

        String newEmail = prototype.getEmail();
        if (newEmail != null && !Objects.equals(newEmail, email) && exists(newEmail)) {
            throw new IllegalArgumentException("Admin with email = " + newEmail + " already exists");
        }

        Admin superAdmin = prototype.getSuperAdmin();

        if (superAdmin != null) {
            if (Objects.equals(superAdmin.getEmail(), newEmail)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
            if (!exists(superAdmin.getEmail())) {
                throw new IllegalArgumentException("Superadmin with email = " + superAdmin.getEmail() + " does not exist");
            }
        }

//        if (newEmail != null && !Objects.equals(newEmail, email)) {
//            for (Admin a : admins) {
//                if (Objects.equals(a.getSuperAdmin(), email)) {
//                    a.setS(newEmail);
//                }
//            }
//        }

        admins.set(index, Admin.copy(prototype));
        saveToDb();
    }

    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        List<Admin> subordinates = getSubordinates(email);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException(
                    "You are trying to delete SuperAdmin. Use deleteSuperAdmin(email, newSuperadminEmail)"
            );
        }

        for (int i = 0; i < admins.size(); i++) {
            if (Objects.equals(admins.get(i).getEmail(), email)) {
                admins.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with email = " + email + " not found");
    }

    public void deleteSuperAdmin(String email, String newSuperadminEmail) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        Admin adminToDelete = null;
        for (Admin a : admins) {
            if (Objects.equals(a.getEmail(), email)) {
                adminToDelete = a;
                break;
            }
        }
        if (adminToDelete == null) {
            throw new IllegalArgumentException("Admin with email = " + email + " not found");
        }

        boolean hasSubordinates = false;
        for (Admin a : admins) {
            if (Objects.equals(a.getSuperAdmin(), email)) {
                hasSubordinates = true;
                break;
            }
        }

        if (hasSubordinates) {
            if (newSuperadminEmail == null || newSuperadminEmail.isBlank()) {
                throw new IllegalArgumentException("You must provide new SuperAdmin email");
            }
            if (!exists(newSuperadminEmail)) {
                throw new IllegalArgumentException("Admin with email = " + newSuperadminEmail + " not found");
            }
            if (Objects.equals(newSuperadminEmail, email)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }

            makeSuperAdmin(newSuperadminEmail);

            Admin superAdmin = get(newSuperadminEmail).orElseThrow();

            for (Admin a : admins) {
                if (Objects.equals(a.getSuperAdmin(), superAdmin)) {
                    a.addSuperAdmin(superAdmin);
                }
            }
        }

        admins.remove(adminToDelete);
        saveToDb();
    }

    public void makeSuperAdmin(String email) throws IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        for (Admin admin : admins) {
            if (Objects.equals(admin.getEmail(), email)) {
                Admin superAdmin = get(email).orElseThrow();

                admin.addSuperAdmin(superAdmin);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with email = " + email + " not found");
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) return false;
        for (Admin admin : admins) {
            if (Objects.equals(admin.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    public boolean exists(Admin admin) throws IOException {
        return exists(admin.getEmail());
    }

    private List<Admin> getSubordinates(String superadminEmail) {
        List<Admin> raw = new ArrayList<>();
        for (Admin admin : admins) {
            if (Objects.equals(admin.getSuperAdmin(), superadminEmail)) {
                raw.add(admin);
            }
        }
        return copyList(raw);
    }

    private List<Admin> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.ADMINS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.ADMINS, ADMIN_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<Admin> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Admin admin) {
                    result.add(admin);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.ADMINS, admins);
    }

    private static List<Admin> copyList(List<Admin> list) {
        List<Admin> out = new ArrayList<>();
        if (list == null) return out;
        for (Admin a : list) {
            out.add(Admin.copy(a));
        }
        return out;
    }
}
