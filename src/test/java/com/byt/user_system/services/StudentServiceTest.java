package com.byt.user_system.services;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import com.byt.user_system.data.Student;
import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
import com.byt.user_system.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest extends CRUDServiceTest<Student> {

    public StudentServiceTest() {
        super(DataSaveKeys.STUDENTS, saveLoadService -> new StudentService(saveLoadService, null));
    }

    @Override
    protected String getSampleObjectId() {
        return TEST_OBJECT_ID;
    }

    @Override
    protected Student getSampleObject() {
        LocalDate dob = LocalDate.of(1997, 1, 1);

        Student student = new Student(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                List.of(StudyLanguage.ENGLISH),
                StudyStatus.ACTIVE
        );
        student.setId(TEST_OBJECT_ID);
        return student;
    }


    @Override
    protected void alterEntity(Student entity) {
        entity.setFirstName(entity.getFirstName() + "_changed");
    }

    // ------------------- TESTS FOR FIELDS FROM USER -------------------

    // firstName contains ukrainian letters
    @Test
    public void createStudentWithNonLatinFirstName() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Юмі",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // email = null
    @Test
    public void createStudentWithNullEmail() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        null,
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // email wrong format
    @Test
    public void createStudentWithInvalidEmailFormat() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi_at_gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // phone contains letters
    @Test
    public void createStudentWithPhoneContainingLetters() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "48505ab505",
                        "yumi@gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // phone too short
    @Test
    public void createStudentWithTooShortPhone() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "4850",
                        "yumi@gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // date of birth in future
    @Test
    public void createStudentWithFutureDateOfBirth() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.plusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // too young
    @Test
    public void createStudentWithTooYoungDateOfBirth() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(10);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        StudyStatus.ACTIVE
                )
        );
    }

    // ------------------- TESTS FOR STUDENT FIELDS -------------------

    // valid data
    @Test
    public void createStudentWithValidData() throws IOException {
        StudentService service = (StudentService) emptyService;

        int before = service.getAll().size();

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);

        Student created = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                List.of(StudyLanguage.ENGLISH, StudyLanguage.POLISH),
                StudyStatus.ACTIVE
        );

        assertNotNull(created);
        assertNotNull(created.getId());

        List<Student> after = service.getAll();
        assertEquals(before + 1, after.size());
    }

    // languagesOfStudies = null
    @Test
    public void createStudentWithNullLanguages() {
        StudentService service = (StudentService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(21);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        null,
                        StudyStatus.ACTIVE
                )
        );
    }

    // languagesOfStudies blank list
    @Test
    public void createStudentWithEmptyLanguages() {
        StudentService service = (StudentService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(21);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        List.of(),
                        StudyStatus.ACTIVE
                )
        );
    }

    // study status = null
    @Test
    public void createStudentWithNullStudyStatus() {
        StudentService service = (StudentService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(20);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        List.of(StudyLanguage.ENGLISH),
                        null
                )
        );
    }


    // creating prototype with null
    @Test
    public void createWithNullPrototype() {
        StudentService service = (StudentService) emptyService;
        assertThrows(ValidationException.class, () -> service.create((Student) null));
    }

    // create prototype and test for leackage
    @Test
    public void createWithPrototypeDoesNotLeakReferences() throws IOException {
        StudentService service = (StudentService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(30);

        List<StudyLanguage> langs = new ArrayList<>();
        langs.add(StudyLanguage.ENGLISH);

        Student prototype = new Student(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                langs,
                StudyStatus.ACTIVE
        );

        service.create(prototype);

        // міняємо прототип після create()
        prototype.setFirstName("CHANGED");
        prototype.getLanguagesOfStudies().add(StudyLanguage.POLISH);

        List<Student> all = service.getAll();
        assertEquals(1, all.size());
        Student stored = all.getFirst();

        // ім’я не змінилось
        assertNotEquals("CHANGED", stored.getFirstName());
        assertEquals("Yumi", stored.getFirstName());

        // список мов також не повинен підтягнути нову мову з прототипу
        assertEquals(1, stored.getLanguagesOfStudies().size());
        assertFalse(stored.getLanguagesOfStudies().contains(StudyLanguage.POLISH));
    }
}
