package com.byt.services.user_system;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.byt.validation.user_system.UserValidator;
import com.byt.exception.ValidationException;
import com.byt.exception.ExceptionCode;

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

    public StudentService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<Student> loaded = loadFromDb(); // raw objects from our 'DB'
        this.students = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________

    public Student create(String firstName, String lastName, String familyName,
                          LocalDate dateOfBirth, String phoneNumber, String email,
                          List<StudyLanguage> languagesOfStudies,
                          StudyStatus studiesStatus) throws IOException {

        validateClassData(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies, studiesStatus);


        Student student = new Student(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), studiesStatus
        );

        if (student.getEmail() != null && exists(student.getEmail())) {
            throw new IllegalStateException("student exists with this email already");
        }

        students.add(student);
        saveToDb();

        return copy(student);
    }

    @Override
    public void create(Student prototype) throws IllegalArgumentException, IOException {

        validateClass(prototype);

        if (prototype.getEmail() != null && exists(prototype.getEmail())) {
            throw new IllegalArgumentException("student with email = " + prototype.getEmail() + " already exists");
        }

        Student toStore = copy(prototype);
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
                return Optional.of(copy(student));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Student> getAll() throws IOException {
        return copyList(students);
    }

    @Override
    public void update(String email, Student prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        validateClass(prototype);

        int index = -1;
        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getEmail(), email)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Student with email=" + email + " not found");
        }

        String newEmail = prototype.getEmail();

        if (!Objects.equals(newEmail, email)) {
            if (newEmail != null && exists(newEmail)) {
                throw new IllegalArgumentException("Student with email=" + newEmail + " already exists");
            }
            students.remove(index);

            Student toStore = copy(prototype);
            students.add(toStore);
        } else {
            Student updatedCopy = copy(prototype);
            students.set(index, updatedCopy);
        }

        saveToDb();
    }

    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getEmail(), email)) {
                students.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("Student with email=" + email + " not found");
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
        copy.setEmail(adm.getEmail());
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


    // VALIDATION METHODS
    private void validateClassData(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            List<StudyLanguage> languagesOfStudies,
            StudyStatus studiesStatus
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

        //  only Student validation
        if (languagesOfStudies == null || languagesOfStudies.isEmpty()) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Student must have at least one study language"
            );
        }

        if (studiesStatus == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Study status must not be null"
            );
        }
    }

    private void validateClass(Student prototype) {
        if (prototype == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Student prototype must not be null"
            );
        }

        validateClassData(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getLanguagesOfStudies(),
                prototype.getStudiesStatus()
        );
    }

}

