package com.byt.services.user_system;
import com.byt.validation.user_system.TeacherValidator;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.data.user_system.Teacher;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeacherService implements CRUDService<Teacher> {
    // comments explaining how everything works are in Admin Service
    private final SaveLoadService service;
    private List<Teacher> teachers;

    private static final Type TEACHER_LIST_TYPE = new TypeToken<List<Teacher>>() {
    }.getType();

    public TeacherService(SaveLoadService service, List<Teacher> teachers) {
        this.service = service;
        this.teachers = teachers != null ? copyList(teachers) : new ArrayList<>();
    }

    public TeacherService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<Teacher> loaded = loadFromDb(); // raw objects from our 'DB'
        this.teachers = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________

    public Teacher create(String firstName, String lastName, String familyName,
                          LocalDate dateOfBirth, String phoneNumber, String email,
                          LocalDate hireDate, String title,
                          String position) throws IOException {

        TeacherValidator.validateTeacher(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position);


        Teacher teacher = new Teacher(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position
        );
        if (teacher.getEmail() != null && exists(teacher.getEmail())) {
            throw new IllegalStateException("teacher exists with this email already");
        }
        teachers.add(teacher);
        saveToDb();

        return copy(teacher);
    }

    @Override
    public void create(Teacher prototype) throws IllegalArgumentException, IOException {

        TeacherValidator.validateClass(prototype);

        if (prototype.getEmail() != null && exists(prototype.getEmail())) {
            throw new IllegalArgumentException("teacher with email = " + prototype.getEmail() + " already exists");
        }

        Teacher toStore = copy(prototype);
        teachers.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Teacher> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getEmail(), email)) {
                return Optional.of(copy(teacher));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Teacher> getAll() throws IOException {
        return copyList(teachers);
    }

    @Override
    public void update(String email, Teacher prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        TeacherValidator.validateClass(prototype);

        int index = -1;
        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getEmail(), email)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Teacher with email=" + email + " not found");
        }

        String newEmail = prototype.getEmail();

        if (!Objects.equals(newEmail, email)) {
            if (newEmail != null && exists(newEmail)) {
                throw new IllegalArgumentException("Teacher with email=" + newEmail + " already exists");
            }
            teachers.remove(index);

            Teacher toStore = copy(prototype);
            teachers.add(toStore);
        } else {
            Teacher updatedCopy = copy(prototype);
            teachers.set(index, updatedCopy);
        }

        saveToDb();
    }

    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getEmail(), email)) {
                teachers.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Teacher with email=" + email + " not found");
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) {
            return false;
        }
        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getEmail(), email)) {
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
        copy.setEmail(adm.getEmail());
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
