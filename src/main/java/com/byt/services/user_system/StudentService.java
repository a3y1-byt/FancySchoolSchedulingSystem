package com.byt.services.user_system;

import com.byt.data.scheduling.Group;
import com.byt.data.scheduling.Specialization;
import com.byt.data.user_system.Teacher;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.StudentValidator;
import com.byt.services.reporting.IssueReportService;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.data.user_system.Student;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class StudentService implements CRUDService<Student> {
    // comments explaining how everything works are in Student Service
    private final SaveLoadService service;
    private final IssueReportService issueReportService;
    private List<Student> students;

    private static final Type STUDENT_LIST_TYPE = new TypeToken<List<Student>>() {
    }.getType();

    public StudentService(SaveLoadService service, List<Student> students, IssueReportService issueReportService) {
        this.service = service;
        this.students = students != null
                ? new ArrayList<>(students)
                : new ArrayList<>();
        this.issueReportService = issueReportService;
    }

    public StudentService(SaveLoadService service) {
        this(service, null, null);
    }

    @Override
    public void initialize() throws IOException {
        List<Student> loaded = loadFromDb(); // raw objects from our 'DB'
        this.students = new ArrayList<>(loaded);
    }

    // _________________________________________________________

    public Student create(String firstName, String lastName, String familyName,
                          LocalDate dateOfBirth, String phoneNumber, String email,
                          Set<StudyLanguage> languagesOfStudies,
                          StudyStatus studiesStatus) throws IOException {

        StudentValidator.validateStudent(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies, studiesStatus);

        Student student = new Student(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new HashSet<>(languagesOfStudies),
                studiesStatus
        );

        if (student.getEmail() != null && exists(student.getEmail())) {
            throw new IllegalStateException("student exists with this email already");
        }

        students.add(student);
        saveToDb();
        return Student.copy(student);
    }

    @Override
    public void create(Student prototype) throws IllegalArgumentException, IOException {

        StudentValidator.validateClass(prototype);

        if (prototype.getEmail() != null && exists(prototype.getEmail())) {
            throw new IllegalArgumentException("student with email = " + prototype.getEmail() + " already exists");
        }

        Student toStore = Student.copy(prototype);
        students.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<Student> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (Student student : students) {
            if (Objects.equals(student.getEmail(), email)) {
                return Optional.of(Student.copy(student));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Student> getAll() throws IOException {
        return new ArrayList<>(students.stream().map(Student::copy).toList());
    }

    @Override
    public void update(String email, Student prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        StudentValidator.validateClass(prototype);

        String oldEmail = email;

        // firstly we find the old stored student
        int index = -1;
        Student oldStored = null;

        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getEmail(), email)) {
                index = i;
                oldStored = students.get(i);
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Student with email=" + email + " not found");
        }

        // then, we validate email change, in order to avoid possible duplicates
        String newEmail = prototype.getEmail();
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("new email must not be null or blank");
        }

        if (!Objects.equals(newEmail, email)) {
            if (exists(newEmail)) {
                throw new IllegalArgumentException("Student with email=" + newEmail + " already exists");
            }

            students.remove(index);
            students.add(Student.copy(prototype));
        } else {
            students.set(index, Student.copy(prototype));
        }

        // then, we collect references (STUDENT - GROUP; STUDENT - SPECIALIZATION)
        Set<Group> oldGroups = oldStored.getGroups();
        Set<Specialization> oldSpecs = oldStored.getSpecializations();


        // theen, we remove connection with old instances from references
        for (Group g : oldGroups) {
            g.removeStudent(oldStored);
        }
        for (Specialization sp : oldSpecs) {
            sp.removeStudent(oldStored);
        }

        // finally, we are creating a new student instance (just a copy)
        Student newStored = Student.copy(prototype);

        // AND ----- attaching new instance to the same reference the old one was attached to
        students.set(index, newStored);

        for (Group g : oldGroups) {
            g.addStudent(newStored);
        }
        for (Specialization sp : oldSpecs) {
            sp.addStudent(newStored);
        }
        saveToDb();

        if (issueReportService != null && !Objects.equals(oldEmail, newEmail)) {
            issueReportService.updateReporterEmail(oldEmail, newEmail);
        }
    }


    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        // firstly we find the old stored student
        int index = -1;
        Student oldStored = null;

        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getEmail(), email)) {
                index = i;
                oldStored = students.get(i);
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Student with email=" + email + " not found");
        }

        // then, we collect references (STUDENT - GROUP; STUDENT - SPECIALIZATION)
        Set<Group> oldGroups = oldStored.getGroups();
        Set<Specialization> oldSpecs = oldStored.getSpecializations();


        // theen, we remove connection with old instances from references
        for (Group g : oldGroups) {
            g.removeStudent(oldStored);
        }
        for (Specialization sp : oldSpecs) {
            sp.removeStudent(oldStored);
        }

        students.remove(index);

        saveToDb();
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) {
            return false;
        }
        for (Student student : students) {
            if (Objects.equals(student.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    // _________________________________________________________

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

