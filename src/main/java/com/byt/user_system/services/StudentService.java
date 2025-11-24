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

    private final SaveLoadService service;
    private List<Student> students;
    private static final Type STUDENT_LIST_TYPE =
            new TypeToken<List<Student>>() {}.getType();


    public StudentService(SaveLoadService service, List<Student> students) {
        this.service = service;
        this.students = students;
    }


    @SuppressWarnings("unchecked")
    public void init() throws IOException {
        if (!service.canLoad(DataSaveKeys.STUDENTS)) {
            students = new ArrayList<>();
            return;
        }

        Object loaded = service.load(DataSaveKeys.STUDENTS, STUDENT_LIST_TYPE);

        if (loaded instanceof List<?>) {
            students = (List<Student>) loaded;
        } else {
            students = new ArrayList<>();
        }
    }


    public void create(String firstName, String lastName, String familyName,
                       LocalDate dateOfBirth, String phoneNumber, String email,
                       List<StudyLanguage> languagesOfStudies,
                       StudyStatus studiesStatus) throws IOException {
        Student student = new Student(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), studiesStatus
        );

        students.add(student);
        service.save(DataSaveKeys.STUDENTS, students);
    }

    public void create(Student student) throws IOException {
        students.add(student);
        service.save(DataSaveKeys.STUDENTS, students);
    }

    public List<Student> getAll() {
        return new ArrayList<>(students);
    }

    public Student getById(String id) {
        for (Student student : students) {
            if (Objects.equals(student.getId(), id))
                return student;
        }
        return null;
    }

    public void update(Student updated) throws IOException {
        for (int i = 0; i < students.size(); i++) {
            Student current = students.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                students.set(i, updated);
                service.save(DataSaveKeys.STUDENTS, students);
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getId(), id)) {
                students.remove(i);
                service.save(DataSaveKeys.STUDENTS, students);
                return;
            }
        }
    }

}

