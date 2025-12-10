package com.byt.services.user_system;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.data.user_system.Admin;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.byt.validation.user_system.UserValidator;
import com.byt.exception.ValidationException;
import com.byt.exception.ExceptionCode;


public class AdminService implements CRUDService<Admin> {

    private final SaveLoadService service;
    private List<Admin> admins;

    // cause Java will erase generics in runtime, gson won't be able to get what exactly
    // is inside List Admin. Via TypeToken we save FULL info about List type,
    // so thst later we could correctly deserialize json into list<Admin>
    private static final Type ADMIN_LIST_TYPE = new TypeToken<List<Admin>>() {
    }.getType();

    // Constructor, we just copy inputed List to not share references with external code
    public AdminService(SaveLoadService service, List<Admin> admins) {
        this.service = service;
        this.admins = admins != null ? copyList(admins) : new ArrayList<>();
    }

    public AdminService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<Admin> loaded = loadFromDb(); // raw objects from our 'DB'
        this.admins = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________
    // Next it's just our basic CRUD operations, nothing hard

    public Admin create(String firstName, String lastName, String familyName,
                        LocalDate dateOfBirth, String phoneNumber, String email,
                        LocalDate hireDate, LocalDateTime lastLoginTime, String superadminId) throws IOException {


        validateClassData(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime);

        // if superadminId == null - admin is superAdmin,
        // so we are checking if ordinary admin has his superAdmin's email
        if (superadminId != null && !exists(superadminId)) {
            throw new IllegalArgumentException("Superadmin with email = " + superadminId + " does not exist");
        }


        Admin admin = new Admin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime, superadminId
        );

        if (admin.getEmail() != null && exists(admin.getEmail())) {
            throw new IllegalStateException("Admin exists with this email already");
        }

        admins.add(admin);
        saveToDb();

        return copy(admin);
    }

    @Override
    public void create(Admin prototype) throws IllegalArgumentException, IOException {

        validateClass(prototype);

        if (prototype.getEmail() != null && exists(prototype.getEmail())) {
            throw new IllegalArgumentException("Admin with email = " + prototype.getEmail() + " already exists");
        }


        String superadminId = prototype.getSuperadminId();
        if (superadminId != null) {
            if (!exists(superadminId)) {
                throw new IllegalArgumentException("Superadmin with email = " + superadminId + " does not exist");
            }
            if (superadminId.equals(prototype.getEmail())) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
        }

        Admin toStore = copy(prototype);
        admins.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Admin> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        for (Admin admin : admins) {
            if (Objects.equals(admin.getEmail(), email)) {
                return Optional.of(copy(admin));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Admin> getAll() throws IOException {
        return copyList(admins);
    }

    @Override
    public void update(String email, Admin prototype) throws IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        validateClass(prototype);

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

        String superadminId = prototype.getSuperadminId();
        if (superadminId != null) {
            if (!exists(superadminId)) {
                throw new IllegalArgumentException("Superadmin with email = " + superadminId + " does not exist");
            }
            if (superadminId.equals(newEmail)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
        }

        if (!Objects.equals(newEmail, email)) {
            if (newEmail != null && exists(newEmail)) {
                throw new IllegalArgumentException("Admin with email= " + newEmail + " already exists");
            }

            for (Admin a : admins) {
                if (Objects.equals(a.getSuperadminId(), email)) {
                    a.setSuperadminId(newEmail);
                }
            }
        }
        Admin updatedCopy = copy(prototype);
        admins.set(index, updatedCopy);
        saveToDb();
    }

    // so rn if Admin does not have SuperAdminId in his class (it is null)
    // - it means that HE IS superAdmin. So when deleting -
    // you must provide new superAdminId that will be supervising admins
    // that are "under" Admin that we are deleting
    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }

        List<Admin> subordinates = getSubordinates(email);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException(
                    "You are trying to delete SuperAdmin. Use deleteSuperAdmin(id, newSuperAdminId)");
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

    public void deleteSuperAdmin(String email, String newSuperadminId) throws IllegalArgumentException, IOException {
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
            if (Objects.equals(a.getSuperadminId(), email)) {
                hasSubordinates = true;
                break;
            }
        }

        if (hasSubordinates) {
            if (newSuperadminId == null || newSuperadminId.isBlank()) {
                throw new IllegalArgumentException("You must provide new SuperAdmin Id");
            }
            if (!exists(newSuperadminId)) {
                throw new IllegalArgumentException(
                        "Admin with email = " + newSuperadminId + " not found. Assign valid new SuperAdmin");
            }
            if (Objects.equals(newSuperadminId, email)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }

            makeSuperAdmin(newSuperadminId);

            for (Admin a : admins) {
                if (Objects.equals(a.getSuperadminId(), email)) {
                    a.setSuperadminId(newSuperadminId);
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
                admin.setSuperadminId(null);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with email = " + email + " not found");
    }

    public void assignSupervisor(String email, String superadminId) throws IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be null or blank");
        }
        if (superadminId == null || superadminId.isBlank()) {
            throw new IllegalArgumentException("Superadmin email must not be null or blank");
        }
        if (email.equals(superadminId)) {
            throw new IllegalArgumentException("Admin cannot supervise himself");
        }

        if (!exists(superadminId)) {
            throw new IllegalArgumentException("Superadmin with email = " + superadminId + " does not exist");
        }

        Admin adm = null;
        for (Admin a : admins) {
            if (Objects.equals(a.getEmail(), email)) {
                adm = a;
                break;
            }
        }
        if (adm == null) {
            throw new IllegalArgumentException("Admin with email = " + email + " not found");
        }

        adm.setSuperadminId(superadminId);
        saveToDb();
    }


    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) {
            return false;
        }
        for (Admin admin : admins) {
            if (Objects.equals(admin.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    // _________________________________________________________

    // creating fully independent copy of Admin,
    // so that code from outside could not change internal objects of service
    private Admin copy(Admin adm) {
        if (adm == null) return null;

        Admin copy = new Admin(
                adm.getFirstName(),
                adm.getLastName(),
                adm.getFamilyName(),
                adm.getDateOfBirth(),
                adm.getPhoneNumber(),
                adm.getEmail(),
                adm.getHireDate(),
                adm.getLastLoginTime(),
                adm.getSuperadminId()
        );
        copy.setEmail(adm.getEmail());
        return copy;
    }

    private List<Admin> getSubordinates(String superadminId) {
        List<Admin> raw = new ArrayList<>();
        for (Admin admin : admins) {
            if (Objects.equals(admin.getSuperadminId(), superadminId)) {
                raw.add(admin);
            }
        }
        return copyList(raw);
    }

    private Optional<Admin> getSupervisor(String adminId) {
        Optional<Admin> superadmin = get(adminId);
        if (superadmin.isEmpty()) {
            return Optional.empty();
        }
        String superId = superadmin.get().getSuperadminId();
        if (superId == null) {
            return Optional.empty();
        }
        return get(superId);
    }

    // just a new list with copies of Admins.
    // It's a deep copy - changes of outer list or inside elements won't affect internal
    // list of admins
    private List<Admin> copyList(List<Admin> source) {
        List<Admin> result = new ArrayList<>();
        if (source == null) return result;
        for (Admin a : source) {
            result.add(copy(a));
        }
        return result;
    }

    private List<Admin> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.ADMINS)) {
            return new ArrayList<>();
        }

        // SaveLoadService via Gson is reading JSON from file and returns object
        Object loaded = service.load(DataSaveKeys.ADMINS, ADMIN_LIST_TYPE);

        // checking if loaded is indeed a List,
        // taking from him only Admin elements,
        // returning raw list of objects, just as they came from persistence
        if (loaded instanceof List<?> raw) {
            List<Admin> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Admin admin) {
                    result.add(admin); // RAW from DB
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    // just for seperating internal work with persistence from our CRUD methods.
    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.ADMINS, admins);
    }


    // VALIDATION METHODS
    private void validateClassData(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            LocalDate hireDate,
            LocalDateTime lastLoginTime
    ) {
        // general USER class validation
        UserValidator.validateUserFields(
                firstName,
                lastName,
                familyName,
                dateOfBirth,
                phoneNumber,
                email
        );

        //  only Admin validation
        if (hireDate == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Hire date must not be null"
            );
        }

        LocalDate today = LocalDate.now();
        int min_age_at_hire = 18;

        if (dateOfBirth != null) {
            LocalDate minHireDateByDob = dateOfBirth.plusYears(min_age_at_hire);
            if (hireDate.isBefore(minHireDateByDob)) {
                throw new ValidationException(
                        ExceptionCode.VALUE_OUT_OF_RANGE,
                        "Person must be at least " + min_age_at_hire + " years old at hire date"
                );
            }
        }

        if (hireDate.isAfter(today)) {
            throw new ValidationException(
                    ExceptionCode.VALUE_OUT_OF_RANGE,
                    "Hire date must not be in the future"
            );
        }

        if (dateOfBirth != null && hireDate.isBefore(dateOfBirth)) {
            throw new ValidationException(
                    ExceptionCode.VALUE_OUT_OF_RANGE,
                    "Hire date cannot be before date of birth"
            );
        }

        if (lastLoginTime != null) {
            LocalDateTime now = LocalDateTime.now();

            if (lastLoginTime.isAfter(now)) {
                throw new ValidationException(
                        ExceptionCode.VALUE_OUT_OF_RANGE,
                        "Last login time cannot be in the future"
                );
            }

            LocalDateTime hireStartLocalDateTime = hireDate.atStartOfDay(java.time.ZoneOffset.UTC).toLocalDateTime();
            if (lastLoginTime.isBefore(hireStartLocalDateTime)) {
                throw new ValidationException(
                        ExceptionCode.VALUE_OUT_OF_RANGE,
                        "Last login time cannot be before hire date"
                );
            }
        }
    }

    private void validateClass(Admin prototype) {
        if (prototype == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Admin prototype must not be null"
            );
        }

        validateClassData(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getHireDate(),
                prototype.getLastLoginTime()
        );
    }

}
