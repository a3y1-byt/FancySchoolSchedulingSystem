package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.user_system.data.Teacher;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class TeacherService implements CRUDService<Teacher> {
    // comments explaining how everything works are in Teacher Service
    private final SaveLoadService service;
    private List<Teacher> teachers;

    private static final Type TEACHER_LIST_TYPE = new TypeToken<List<Teacher>>() {
    }.getType();

    public TeacherService(SaveLoadService service, List<Teacher> teachers) {
        this.service = service;
        this.teachers = teachers != null ? copyList(teachers) : new ArrayList<>();
    }


    public void init() throws IOException {
        List<Teacher> loaded = loadFromDb(); // raw objects from our 'DB'
        this.teachers = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________

    public Teacher create(String firstName, String lastName, String familyName,
                          LocalDate dateOfBirth, String phoneNumber, String email,
                          LocalDate hireDate, String title,
                          String position) throws IOException {
        Teacher teacher = new Teacher(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position
        );

        teachers.add(teacher);
        saveToDb();

        return copy(teacher);
    }

    @Override
    public void create(Teacher prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) {
            throw new IllegalArgumentException("Teacher prototype must not be null");
        }
        Teacher toStore = copy(prototype);
        teachers.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Teacher> get(String id) throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getId(), id)) {
                return Optional.of(copy(teacher));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Teacher> getAll() {
        return copyList(teachers);
    }

    @Override
    public void update(String id, Teacher prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (prototype == null) {
            throw new IllegalArgumentException("Teacher prototype must not be null");
        }

        for (int i = 0; i < teachers.size(); i++) {
            Teacher current = teachers.get(i);
            if (Objects.equals(current.getId(), id)) {
                Teacher updatedCopy = copy(prototype);
                // не довіряємо prototype.getId(), використовуємо параметр id
                updatedCopy.setId(id);
                teachers.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Teacher with id=" + id + " not found");
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getId(), id)) {
                teachers.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Teacher with id=" + id + " not found");
    }

    @Override
    public boolean exists(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getId(), id)) {
                return true;
            }
        }
        return false;
    }
    // _________________________________________________________

    private Teacher copy(Teacher adm) {
        if (adm == null) return null;

        Teacher copy = new Teacher(
                adm.getFirstName(),
                adm.getLastName(),
                adm.getFamilyName(),
                adm.getDateOfBirth(),
                adm.getPhoneNumber(),
                adm.getEmail(),
                adm.getHireDate(),
                adm.getTitle(),
                adm.getPosition()
        );
        copy.setId(adm.getId());
        return copy;
    }

    private List<Teacher> copyList(List<Teacher> source) {
        List<Teacher> result = new ArrayList<>();
        if (source == null) return result;
        for (Teacher a : source) {
            result.add(copy(a));
        }
        return result;
    }

    private List<Teacher> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.TEACHERS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.TEACHERS, TEACHER_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<Teacher> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Teacher teacher) {
                    result.add(teacher);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.TEACHERS, teachers);
    }
}
