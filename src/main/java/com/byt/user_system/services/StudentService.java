package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
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

public class StudentService {
    // comments explaining how everything works are in Admin Service
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

    public Student create(Student student) throws IOException {
        Student toStore = copy(student);
        students.add(toStore);
        saveToDb();
        return copy(toStore);
    }

    public List<Student> getAll() {
        return copyList(students);
    }

    public Student getById(String id) {
        for (Student student : students) {
            if (Objects.equals(student.getId(), id)) {
                return copy(student);
            }
        }
        return null;
    }

    public void update(Student updated) throws IOException {
        for (int i = 0; i < students.size(); i++) {
            Student current = students.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                students.set(i, copy(updated));
                saveToDb();
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getId(), id)) {
                students.remove(i);
                saveToDb();
                return;
            }
        }
    }
    // _________________________________________________________

    private Student copy(Student adm) {
        if (adm == null) return null;

        Student copy = new Student(
                adm.getFirstName(),
                adm.getLastName(),
                adm.getFamilyName(),
                adm.getDateOfBirth(),
                adm.getPhoneNumber(),
                adm.getEmail(),
                adm.getLanguagesOfStudies(),
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

