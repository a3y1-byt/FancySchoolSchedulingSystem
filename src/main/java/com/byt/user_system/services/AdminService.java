package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.user_system.data.Admin;
import com.byt.user_system.data.Student;
import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminService {

    private final SaveLoadService service;
    private List<Admin> admins;

    private static final Type ADMIN_LIST_TYPE =
            new TypeToken<List<Admin>>() {}.getType();

    public AdminService(SaveLoadService service, List<Admin> admins) {
        this.service = service;
        this.admins = admins != null ? copyList(admins) : new ArrayList<>();
    }


    // ADDINg COPY - like this?

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

    private List<Admin> copyList(List<Admin> source) {
        List<Admin> result = new ArrayList<>();
        if (source == null) return result;
        for (Admin a : source) {
            result.add(copy(a));
        }
        return result;
    }

    private void persist() throws IOException {
        // So here I am passing to our 'database' copy of obj?
        service.save(DataSaveKeys.ADMINS, copyList(admins));
    }

    public void init() throws IOException {
        if (!service.canLoad(DataSaveKeys.ADMINS)) {
            admins = new ArrayList<>();
            return;
        }

        Object loaded = service.load(DataSaveKeys.ADMINS, ADMIN_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<Admin> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Admin) {
                    result.add(copy((Admin) o));
                }
            }
            admins = result;
        } else {
            admins = new ArrayList<>();
        }
    }

    public Admin create(String firstName, String lastName, String familyName,
                        LocalDate dateOfBirth, String phoneNumber, String email,
                        LocalDate hireDate, LocalDateTime lastLoginTime) throws IOException {

        Admin admin = new Admin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime
        );

        admins.add(admin);
        persist();

        return copy(admin);
    }

    public Admin create(Admin admin) throws IOException {
        Admin toStore = copy(admin);
        admins.add(toStore);
        persist();
        return copy(toStore);
    }

    public List<Admin> getAll() {
        return copyList(admins);
    }

    public Admin getById(String id) {
        for (Admin admin : admins) {
            if (Objects.equals(admin.getId(), id)) {
                return copy(admin);
            }
        }
        return null;
    }

    public void update(Admin updated) throws IOException {
        for (int i = 0; i < admins.size(); i++) {
            Admin current = admins.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                admins.set(i, copy(updated));
                persist();
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < admins.size(); i++) {
            if (Objects.equals(admins.get(i).getId(), id)) {
                admins.remove(i);
                persist();
                return;
            }
        }
    }

}
