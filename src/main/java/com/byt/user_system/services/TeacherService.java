package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.user_system.data.Admin;
import com.byt.user_system.data.Teacher;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeacherService {

    private final SaveLoadService service;
    private List<Teacher> teachers;

    private static final Type TEACHER_LIST_TYPE =
            new TypeToken<List<Teacher>>() {}.getType();

    public TeacherService(SaveLoadService service, List<Teacher> teachers) {
        this.service = service;
        this.teachers = teachers;
    }

    @SuppressWarnings("unchecked")
    public void init() throws IOException {
        if (!service.canLoad(DataSaveKeys.TEACHERS)) {
            teachers = new ArrayList<>();
            return;
        }

        Object loaded = service.load(DataSaveKeys.TEACHERS, TEACHER_LIST_TYPE);

        if (loaded instanceof List<?>) {
            teachers = (List<Teacher>) loaded;
        } else {
            teachers = new ArrayList<>();
        }
    }

    public void create(String firstName, String lastName, String familyName,
                       LocalDate dateOfBirth, String phoneNumber, String email,
                       LocalDate hireDate,String title,
                       String position) throws IOException {
        Teacher teacher = new Teacher(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title,  position
        );

        teachers.add(teacher);
        service.save(DataSaveKeys.ADMINS, teachers);
    }

    public void create(Teacher teacher) throws IOException {
        teachers.add(teacher);
        service.save(DataSaveKeys.TEACHERS, teachers);
    }

    public List<Teacher> getAll() {
        return new ArrayList<>(teachers);
    }

    public Teacher getById(String id) {
        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getId(), id)) {
                return teacher;
            }
        }
        return null;
    }

    public void update(Teacher updated) throws IOException {
        for (int i = 0; i < teachers.size(); i++) {
            Teacher current = teachers.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                teachers.set(i, updated);
                service.save(DataSaveKeys.TEACHERS, teachers);
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getId(), id)) {
                teachers.remove(i);
                service.save(DataSaveKeys.TEACHERS, teachers);
                return;
            }
        }
    }
}
