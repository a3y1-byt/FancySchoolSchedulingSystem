package com.byt.services.user_system;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import com.byt.data.user_system.Teacher;
import com.byt.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherServiceTest extends CRUDServiceTest<Teacher> {

    public TeacherServiceTest() {
        super(DataSaveKeys.TEACHERS, saveLoadService -> new TeacherService(saveLoadService));
    }

    @Override
    protected String getSampleObjectId() {
        return TEST_OBJECT_ID;
    }

    @Override
    protected Teacher getSampleObject() {
        LocalDate dob = LocalDate.of(1997, 1, 1);
        LocalDate hireDate = LocalDate.of(2024, 1, 1);

        Teacher teacher = new Teacher(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                hireDate,
                "DogTeacher",
                "Dean"
        );
        teacher.setEmail(TEST_OBJECT_ID);
        return teacher;
    }


    @Override
    protected void alterEntity(Teacher entity) {
        entity.setFirstName(entity.getFirstName() + "_changed");
    }

    // ------------------- TESTS FOR FIELDS FROM USER -------------------

    // update test
    @Test
    public void updateTeacherWithValidData() throws IOException {
        TeacherService service = (TeacherService) serviceWithData;

        String id = getSampleObjectId();
        Optional<Teacher> beforeOpt = service.get(id);
        assertTrue(beforeOpt.isPresent());

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);

        Teacher prototype = new Teacher(
                "Yumpa",
                "Hnatiukk",
                "Piess",
                dob,
                "3809691046",
                "yumiii@gmail.com",
                hireDate,
                "DogTeacher",
                "Dean"
        );

        service.update(id, prototype);

        Optional<Teacher> afterOpt = service.get(id);
        assertTrue(afterOpt.isPresent());
        Teacher updated = afterOpt.get();

        assertEquals("Yumpa", updated.getFirstName());
        assertEquals("Hnatiukk", updated.getLastName());
        assertEquals("Piess", updated.getFamilyName());
        assertEquals(dob, updated.getDateOfBirth());
        assertEquals("3809691046", updated.getPhoneNumber());
        assertEquals("yumiii@gmail.com", updated.getEmail());
        assertEquals(hireDate, updated.getHireDate());
        assertEquals("DogTeacher", updated.getTitle());
        assertEquals("Dean", updated.getPosition());

        assertEquals(id, updated.getEmail());
    }

                // firstName contains ukrainian letters
    @Test
    public void createTeacherWithNonLatinFirstName() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Юмі",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // email = null
    @Test
    public void createTeacherWithNullEmail() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        null,
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // email wrong format
    @Test
    public void createTeacherWithInvalidEmailFormat() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi_at_gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // phone contains letters
    @Test
    public void createTeacherWithPhoneContainingLetters() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "48505ab505",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // phone too short
    @Test
    public void createTeacherWithTooShortPhone() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "4850",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // date of birth in future
    @Test
    public void createTeacherWithFutureDateOfBirth() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.plusDays(1);
        LocalDate hireDate = today;

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // too young
    @Test
    public void createTeacherWithTooYoungDateOfBirth() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(10);
        LocalDate hireDate = today;

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // ------------------- TESTS FOR TEACHER FIELDS -------------------

    // valid data
    @Test
    public void createTeacherWithValidData() throws IOException {
        TeacherService service = (TeacherService) emptyService;

        int before = service.getAll().size();

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);

        Teacher created = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                hireDate,
                "DogTeacher",
                "Dean"
        );

        assertNotNull(created);
        assertNotNull(created.getEmail());

        List<Teacher> after = service.getAll();
        assertEquals(before + 1, after.size());
    }

    // hire date = null
    @Test
    public void createTeacherWithNullHireDate() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);

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
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // hire date is in future
    @Test
    public void createTeacherWithFutureHireDate() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.plusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // person is younger than 18 at hire date
    @Test
    public void createTeacherTooYoungAtHireDate() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(17);
        LocalDate hireDate = today;

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // hire date is before person's birth
    @Test
    public void createTeacherWithHireDateBeforeBirth() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = dob.minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        "Dean"
                )
        );
    }

    // title = null
    @Test
    public void createTeacherWithNullTitle() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        null,          // title
                        "Dean"
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("title"));
    }

    // title is " "
    @Test
    public void createTeacherWithBlankTitle() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        " ",
                        "Dean"
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("title"));
    }

    // position = null
    @Test
    public void createTeacherWithNullPosition() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        null
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("position"));
    }

    // position is " "
    @Test
    public void createTeacherWithBlankPosition() {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        hireDate,
                        "DogTeacher",
                        " "
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("position"));
    }

    // creating prototype with null
    @Test
    public void createWithNullPrototype() {
        TeacherService service = (TeacherService) emptyService;
        assertThrows(ValidationException.class, () -> service.create((Teacher) null));
    }

    // create prototype and test for leackage
    @Test
    public void createWithPrototypeDoesNotLeakReferences() throws IOException {
        TeacherService service = (TeacherService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);

        Teacher prototype = new Teacher(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                hireDate,
                "DogTeacher",
                "Dean"
        );

        service.create(prototype);

        prototype.setFirstName("CHANGED");
        List<Teacher> all = service.getAll();
        assertEquals(1, all.size());
        Teacher stored = all.getFirst();
        assertNotEquals("CHANGED", stored.getFirstName());
        assertEquals("Yumi", stored.getFirstName());
    }
}
