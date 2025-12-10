package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.user_system.data.Admin;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.byt.user_system.validation.UserValidator;
import com.byt.user_system.validation.ValidationException;

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
        // so we are checking if ordinary admin has his superAdmin's id
        if (superadminId != null && !exists(superadminId)) {
            throw new IllegalArgumentException("Superadmin with id = " +  superadminId + " does not exist");
        }


        Admin admin = new Admin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime, superadminId
        );

        if (admin.getId() != null && exists(admin.getId())) {
            throw new IllegalStateException("Admin exists with this id already");
        }

        admins.add(admin);
        saveToDb();

        return copy(admin);
    }

    @Override
    public void create(Admin prototype) throws IllegalArgumentException, IOException {
        if (prototype.getId() != null && exists(prototype.getId())) {
            throw new IllegalArgumentException("Admin with id = " + prototype.getId() + " already exists");
        }
        validateClass(prototype);


        String superadminId = prototype.getSuperadminId();
        if (superadminId != null ) {
            if (!exists(superadminId)) {
                throw new IllegalArgumentException("Superadmin with id = " +  superadminId + " does not exist");
            }
            if (superadminId.equals(prototype.getId())) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }
        }

        Admin toStore = copy(prototype);
        admins.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Admin> get(String id) throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Admin id must not be null or blank");
        }

        for (Admin admin : admins) {
            if (Objects.equals(admin.getId(), id)) {
                return Optional.of(copy(admin));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Admin> getAll() throws IOException{
        return copyList(admins);
    }

    @Override
    public void update(String id, Admin prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Admin id must not be null or blank");
        }

        validateClass(prototype);

        for (int i = 0; i < admins.size(); i++) {
            Admin current = admins.get(i);
            if (Objects.equals(current.getId(), id)) {
                Admin updatedCopy = copy(prototype);
                updatedCopy.setId(id);
                String superadminId = updatedCopy.getSuperadminId();
                if (superadminId != null ) {
                    if (!exists(superadminId)) {
                        throw new IllegalArgumentException("Superadmin with id = " +  superadminId + " does not exist");
                    }
                    if (superadminId.equals(updatedCopy.getId())) {
                        throw new IllegalArgumentException("Admin cannot supervise himself");
                    }
                }
                admins.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with id = " + id + " not found");
    }

    // so rn if Admin does not have SuperAdminId in his class (it is null)
    // - it means that HE IS superAdmin. So when deleting -
    // you must provide new superAdminId that will be supervising admins
    // that are "under" Admin that we are deleting
    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Admin id must not be null or blank");
        }

        List<Admin> subordinates = getSubordinates(id);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException(
                    "You are trying to delete SuperAdmin. Use deleteSuperAdmin(id, newSuperAdminId)");
        }

        for (int i = 0; i < admins.size(); i++) {
            if (Objects.equals(admins.get(i).getId(), id)) {
                admins.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with id = " + id + " not found");
    }

    public void deleteSuperAdmin(String id, String newSuperadminId) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Admin id must not be null or blank");
        }

        Admin adminToDelete = null;
        for (Admin a : admins) {
            if (Objects.equals(a.getId(), id)) {
                adminToDelete = a;
                break;
            }
        }
        if (adminToDelete == null) {
            throw new IllegalArgumentException("Admin with id = " + id + " not found");
        }

        List<Admin> subordinateAdmins = getSubordinates(id);

        if (!subordinateAdmins.isEmpty()) {
            if (newSuperadminId == null || newSuperadminId.isBlank()) {
                throw new IllegalArgumentException("You must provide new SuperAdmin Id");
            }
            if (!exists(newSuperadminId)) {
                throw new IllegalArgumentException(
                        "Admin with id = " + newSuperadminId + " not found. Assign valid new SuperAdmin");
            }
            if (Objects.equals(newSuperadminId, id)) {
                throw new IllegalArgumentException("Admin cannot supervise himself");
            }

            // нормалізуємо: новий керівник стає супер-адміном
            makeSuperAdmin(newSuperadminId);

            // перепризначаємо підлеглих
            for (Admin sub : subordinateAdmins) {
                sub.setSuperadminId(newSuperadminId);
            }
        }
        admins.remove(adminToDelete);
        saveToDb();
    }

    public void makeSuperAdmin(String id) throws IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Admin id must not be null or blank");
        }

        for (Admin admin : admins) {
            if (Objects.equals(admin.getId(), id)) {
                admin.setSuperadminId(null);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with id = " + id + " not found");
    }

    public void assignSupervisor(String id, String superadminId) throws IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Admin id must not be null or blank");
        }
        if (superadminId == null || superadminId.isBlank()) {
            throw new IllegalArgumentException("Superadmin id must not be null or blank");
        }
        if (id.equals(superadminId)) {
            throw new IllegalArgumentException("Admin cannot supervise himself");
        }

        if (!exists(superadminId)) {
            throw new IllegalArgumentException("Superadmin with id = " + superadminId + " does not exist");
        }

        Admin adm = null;
        for (Admin a : admins) {
            if (Objects.equals(a.getId(), id)) {
                adm = a;
                break;
            }
        }
        if (adm == null) {
            throw new IllegalArgumentException("Admin with id = " + id + " not found");
        }

        adm.setSuperadminId(superadminId);
        saveToDb();
    }


    @Override
    public boolean exists(String id) throws IOException{
        if (id == null || id.isBlank()) {
            return false;
        }
        for (Admin admin : admins) {
            if (Objects.equals(admin.getId(), id)) {
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
        copy.setId(adm.getId());
        return copy;
    }

    private List<Admin> getSubordinates(String superadminId){
        List<Admin> raw = new ArrayList<>();
        for (Admin admin : admins) {
            if (Objects.equals(admin.getSuperadminId(), superadminId)) {
                raw.add(admin);
            }
        }
        return copyList(raw);
    }

    private Optional<Admin> getSupervisor(String adminId){
        Optional<Admin> superadmin = get(adminId);
        if(superadmin.isEmpty()){
            return Optional.empty();
        }
        String superId = superadmin.get().getSuperadminId();
        if(superId == null){
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
            throw new ValidationException("Hire date must not be null");
        }

        LocalDate today = LocalDate.now();
        int min_age_at_hire = 18;

        if (dateOfBirth != null) {
            LocalDate minHireDateByDob = dateOfBirth.plusYears(min_age_at_hire);
            if (hireDate.isBefore(minHireDateByDob)) {
                throw new ValidationException(
                        "Person must be at least " + min_age_at_hire + " years old at hire date"
                );
            }
        }

        if (hireDate.isAfter(today)) {
            throw new ValidationException("Hire date must not be in the future");
        }

        if (dateOfBirth != null && hireDate.isBefore(dateOfBirth)) {
            throw new ValidationException("Hire date cannot be before date of birth");
        }

        if (lastLoginTime != null) {
            LocalDateTime now = LocalDateTime.now();

            if (lastLoginTime.isAfter(now)) {
                throw new ValidationException("Last login time cannot be in the future");
            }

            LocalDateTime hireStartLocalDateTime = hireDate.atStartOfDay(java.time.ZoneOffset.UTC).toLocalDateTime();
            if (lastLoginTime.isBefore(hireStartLocalDateTime)) {
                throw new ValidationException("Last login time cannot be before hire date");
            }
        }
    }

    private void validateClass(Admin prototype) {
        if (prototype == null) {
            throw new ValidationException("Admin prototype must not be null");
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
