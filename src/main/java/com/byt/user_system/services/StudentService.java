package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.user_system.data.Student;
import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StudentService implements CRUDService<Student> {
    // comments explaining how everything works are in Student Service
    private final SaveLoadService service;
    private List<Student> students;

    private static final Type STUDENT_LIST_TYPE = new TypeToken<List<Student>>() {
    }.getType();

    public StudentService(SaveLoadService service, List<Student> students) {
        this.service = service;
        this.students = students != null ? copyList(students) : new ArrayList<>();
    }


    public void init() throws IOException {
        List<Student> loaded = loadFromDb(); // raw objects from our 'DB'
        this.students = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________

    public Student create(String firstName, String lastName, String familyName,
           LocalDate dateOfBirth, String phoneNumber, String email,
           List<StudyLanguage> languagesOfStudies,
           StudyStatus studiesStatus) throws IOException {
        Student student = new Student(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), studiesStatus
        );


        students.add(student);
        saveToDb();

        return copy(student);
    }

    @Override
    public void create(Student prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) {
            throw new IllegalArgumentException("Student prototype must not be null");
        }
        Student toStore = copy(prototype);
        students.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Student> get(String id) throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (Student student : students) {
            if (Objects.equals(student.getId(), id)) {
                return Optional.of(copy(student));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Student> getAll() {
        return copyList(students);
    }

    @Override
    public void update(String id, Student prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (prototype == null) {
            throw new IllegalArgumentException("Student prototype must not be null");
        }

        for (int i = 0; i < students.size(); i++) {
            Student current = students.get(i);
            if (Objects.equals(current.getId(), id)) {
                Student updatedCopy = copy(prototype);
                // не довіряємо prototype.getId(), використовуємо параметр id
                updatedCopy.setId(id);
                students.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Student with id=" + id + " not found");
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getId(), id)) {
                students.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Student with id=" + id + " not found");
    }

    @Override
    public boolean exists(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        for (Student student : students) {
            if (Objects.equals(student.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    // _________________________________________________________

    private Student copy(Student adm) {
        if (adm == null) return null;

        List<StudyLanguage> langs = adm.getLanguagesOfStudies();
        List<StudyLanguage> langsCopy = langs != null
                ? new ArrayList<>(langs)
                : new ArrayList<>();

        Student copy = new Student(
                adm.getFirstName(),
                adm.getLastName(),
                adm.getFamilyName(),
                adm.getDateOfBirth(),
                adm.getPhoneNumber(),
                adm.getEmail(),
                langsCopy,
                adm.getStudiesStatus()
        );
        copy.setId(adm.getId());
        return copy;
    }

    private List<Student> copyList(List<Student> source) {
        List<Student> result = new ArrayList<>();
        if (source == null) return result;
        for (Student a : source) {
            result.add(copy(a));
        }
        return result;
    }

    private List<Student> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.STUDENTS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.STUDENTS, STUDENT_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<Student> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Student student) {
                    result.add(student);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.STUDENTS, students);
    }
}

