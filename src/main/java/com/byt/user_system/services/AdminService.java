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
        this.admins = admins;
    }

    @SuppressWarnings("unchecked")
    public void init() throws IOException {
        if (!service.canLoad(DataSaveKeys.ADMINS)) {
            admins = new ArrayList<>();
            return;
        }

        Object loaded = service.load(DataSaveKeys.ADMINS, ADMIN_LIST_TYPE);

        if (loaded instanceof List<?>) {
            admins = (List<Admin>) loaded;
        } else {
            admins = new ArrayList<>();
        }
    }

    public void create(String firstName, String lastName, String familyName,
                       LocalDate dateOfBirth, String phoneNumber, String email,
                       LocalDate hireDate, LocalDateTime lastLoginTime) throws IOException {
        Admin admin = new Admin(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, lastLoginTime
        );

        admins.add(admin);
        service.save(DataSaveKeys.ADMINS, admins);
    }

    public void create(Admin admin) throws IOException {
        admins.add(admin);
        service.save(DataSaveKeys.ADMINS, admins);
    }

    public List<Admin> getAll() {
        return new ArrayList<>(admins);
    }

    public Admin getById(String id) {
        for (Admin admin : admins) {
            if (Objects.equals(admin.getId(), id)) {
                return admin;
            }
        }
        return null;
    }

    public void update(Admin updated) throws IOException {
        for (int i = 0; i < admins.size(); i++) {
            Admin current = admins.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                admins.set(i, updated);
                service.save(DataSaveKeys.ADMINS, admins);
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < admins.size(); i++) {
            if (Objects.equals(admins.get(i).getId(), id)) {
                admins.remove(i);
                service.save(DataSaveKeys.ADMINS, admins);
                return;
            }
        }
    }
}
