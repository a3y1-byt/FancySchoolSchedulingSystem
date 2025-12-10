package com.byt.services.user_system;

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

import com.byt.validation.user_system.UserValidator;
import com.byt.validation.user_system.ValidationException;


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

        validateClassData(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position);


        Teacher teacher = new Teacher(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position
        );
        if (teacher.getId() != null && exists(teacher.getId())) {
            throw new IllegalStateException("teacher exists with this id already");
        }
        teachers.add(teacher);
        saveToDb();

        return copy(teacher);
    }

    @Override
    public void create(Teacher prototype) throws IllegalArgumentException, IOException {

        validateClass(prototype);

        if (prototype.getId() != null && exists(prototype.getId())) {
            throw new IllegalArgumentException("teacher with id = " + prototype.getId() + " already exists");
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
    public List<Teacher> getAll() throws IOException{
        return copyList(teachers);
    }

    @Override
    public void update(String id, Teacher prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        validateClass(prototype);

        for (int i = 0; i < teachers.size(); i++) {
            Teacher current = teachers.get(i);
            if (Objects.equals(current.getId(), id)) {
                Teacher updatedCopy = copy(prototype);
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
    public boolean exists(String id) throws IOException{
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


    // VALIDATION METHODS
    private void validateClassData(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            LocalDate hireDate,
            String title,
            String position
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

        //  only Teacher validation
        if (hireDate == null) {
            throw new ValidationException("Hire date must not be null");
        }

        LocalDate today = LocalDate.now();
        LocalDate earliest_hire_date = LocalDate.of(2000, 1, 1);
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

        if (title == null || title.isBlank()) {
            throw new ValidationException("Title must not be empty");
        }

        if (position == null || position.isBlank()) {
            throw new ValidationException("Position must not be empty");
        }
    }

    private void validateClass(Teacher prototype) {
        if (prototype == null) {
            throw new ValidationException("Teacher prototype must not be null");
        }

        validateClassData(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getHireDate(),
                prototype.getTitle(),
                prototype.getPosition()
        );
    }

}
