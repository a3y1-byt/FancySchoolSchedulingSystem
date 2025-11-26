package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.user_system.data.Teacher;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TeacherService {
    // comments explaining how everything works are in Admin Service
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
                               LocalDate hireDate,String title,
                               String position) throws IOException {
        Teacher teacher = new Teacher(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title,  position
        );

        teachers.add(teacher);
        saveToDb();

        return copy(teacher);
    }

    public Teacher create(Teacher teacher) throws IOException {
        Teacher toStore = copy(teacher);
        teachers.add(toStore);
        saveToDb();
        return copy(toStore);
    }

    public List<Teacher> getAll() {
        return copyList(teachers);
    }

    public Teacher getById(String id) {
        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getId(), id)) {
                return copy(teacher);
            }
        }
        return null;
    }

    public void update(Teacher updated) throws IOException {
        for (int i = 0; i < teachers.size(); i++) {
            Teacher current = teachers.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                teachers.set(i, copy(updated));
                saveToDb();
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getId(), id)) {
                teachers.remove(i);
                saveToDb();
                return;
            }
        }
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
