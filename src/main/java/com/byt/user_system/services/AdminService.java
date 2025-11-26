package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.user_system.data.Admin;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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


    public void init() throws IOException {
        List<Admin> loaded = loadFromDb(); // raw objects from our 'DB'
        this.admins = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________
    // Next it's just our basic CRUD operations, nothing hard

    public Admin create(String firstName, String lastName, String familyName,
                        Instant dateOfBirth, String phoneNumber, String email,
                        Instant hireDate, Instant lastLoginTime) throws IOException {

        Admin admin = new Admin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime
        );

        admins.add(admin);
        saveToDb();

        return copy(admin);
    }

    @Override
    public void create(Admin prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) {
            throw new IllegalArgumentException("Admin prototype must not be null");
        }
        Admin toStore = copy(prototype);
        admins.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Admin> get(String id) throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (Admin admin : admins) {
            if (Objects.equals(admin.getId(), id)) {
                return Optional.of(copy(admin));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Admin> getAll() {
        return copyList(admins);
    }

    @Override
    public void update(String id, Admin prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (prototype == null) {
            throw new IllegalArgumentException("Admin prototype must not be null");
        }

        for (int i = 0; i < admins.size(); i++) {
            Admin current = admins.get(i);
            if (Objects.equals(current.getId(), id)) {
                Admin updatedCopy = copy(prototype);
                // не довіряємо prototype.getId(), використовуємо параметр id
                updatedCopy.setId(id);
                admins.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with id=" + id + " not found");
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (int i = 0; i < admins.size(); i++) {
            if (Objects.equals(admins.get(i).getId(), id)) {
                admins.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Admin with id=" + id + " not found");
    }

    @Override
    public boolean exists(String id) {
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
                adm.getLastLoginTime()
        );
        copy.setId(adm.getId());
        return copy;
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

}
