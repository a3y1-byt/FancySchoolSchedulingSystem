package com.byt.user_system.services;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import com.byt.user_system.data.FreeListener;
import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FreeListenerServiceTest extends CRUDServiceTest<FreeListener> {

    public FreeListenerServiceTest() {
        super(DataSaveKeys.FREE_LISTENERS, saveLoadService -> new FreeListenerService(saveLoadService, null));
    }

    @Override
    protected String getSampleObjectId() {
        return TEST_OBJECT_ID;
    }

    @Override
    protected FreeListener getSampleObject() {
        LocalDate dob = LocalDate.of(1997, 1, 1);

        FreeListener freeListener = new FreeListener(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                List.of(StudyLanguage.ENGLISH),
                "Some notes"
        );
        freeListener.setId(TEST_OBJECT_ID);
        return freeListener;
    }


    @Override
    protected void alterEntity(FreeListener entity) {
        entity.setFirstName(entity.getFirstName() + "_changed");
    }

    // ------------------- TESTS FOR FIELDS FROM USER -------------------

    // update test
    @Test
    public void updateFreeListenerWithValidData() throws IOException {
        FreeListenerService service = (FreeListenerService) serviceWithData;

        String id = getSampleObjectId();
        Optional<FreeListener> beforeOpt = service.get(id);
        assertTrue(beforeOpt.isPresent());

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        FreeListener prototype = new FreeListener(
                "Yumpa",
                "Hnatiukk",
                "Piess",
                dob,
                "3809691046",
                "yumiii@gmail.com",
                List.of(StudyLanguage.POLISH),
                "woof woof woof"
        );

        service.update(id, prototype);

        Optional<FreeListener> afterOpt = service.get(id);
        assertTrue(afterOpt.isPresent());
        FreeListener updated = afterOpt.get();

        assertEquals("Yumpa", updated.getFirstName());
        assertEquals("Hnatiukk", updated.getLastName());
        assertEquals("Piess", updated.getFamilyName());
        assertEquals(dob, updated.getDateOfBirth());
        assertEquals("3809691046", updated.getPhoneNumber());
        assertEquals("yumiii@gmail.com", updated.getEmail());
        assertEquals(List.of(StudyLanguage.POLISH), updated.getLanguagesOfStudies());
        assertEquals("woof woof woof", updated.getNotes());

        assertEquals(id, updated.getId());
    }

    // firstName contains ukrainian letters
    @Test
    public void createFreeListenerWithNonLatinFirstName() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // email = null
    @Test
    public void createFreeListenerWithNullEmail() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // email wrong format
    @Test
    public void createFreeListenerWithInvalidEmailFormat() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // phone contains letters
    @Test
    public void createFreeListenerWithPhoneContainingLetters() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // phone too short
    @Test
    public void createFreeListenerWithTooShortPhone() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // date of birth in future
    @Test
    public void createFreeListenerWithFutureDateOfBirth() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // too young
    @Test
    public void createFreeListenerWithTooYoungDateOfBirth() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // ------------------- TESTS FOR FREE LISTENER FIELDS -------------------

    // valid data
    @Test
    public void createFreeListenerWithValidData() throws IOException {
        FreeListenerService service = (FreeListenerService) emptyService;

        int before = service.getAll().size();

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);

        FreeListener created = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                List.of(StudyLanguage.ENGLISH, StudyLanguage.POLISH),
                "Woof"
        );

        assertNotNull(created);
        assertNotNull(created.getId());

        List<FreeListener> after = service.getAll();
        assertEquals(before + 1, after.size());
    }

    // languagesOfStudies = null
    @Test
    public void createFreeListenerWithNullLanguages() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // languagesOfStudies blank list
    @Test
    public void createFreeListenerWithEmptyLanguages() {
        FreeListenerService service = (FreeListenerService) emptyService;

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
                        "notes"
                )
        );
    }

    // notes too long
    @Test
    public void createFreeListenerWithTooLongNotes() {
        FreeListenerService service = (FreeListenerService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(21);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1111; i++) {
            sb.append("i");
        }
        String longNotes = sb.toString();

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
                        longNotes
                )
        );
    }

    // notes = null
    @Test
    public void createFreeListenerWithNullNotesIsAllowed() throws IOException {
        FreeListenerService service = (FreeListenerService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(21);

        FreeListener created = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                List.of(StudyLanguage.ENGLISH),
                null
        );

        assertNotNull(created);
        assertNotNull(created.getId());
    }


    // creating prototype with null
    @Test
    public void createWithNullPrototype() {
        FreeListenerService service = (FreeListenerService) emptyService;
        assertThrows(ValidationException.class, () -> service.create((FreeListener) null));
    }

    // create prototype and test for leackage
    @Test
    public void createWithPrototypeDoesNotLeakReferences() throws IOException {
        FreeListenerService service = (FreeListenerService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(30);

        List<StudyLanguage> langs = new ArrayList<>();
        langs.add(StudyLanguage.ENGLISH);

        FreeListener prototype = new FreeListener(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                langs,
                "Some notes"
        );

        service.create(prototype);

        // міняємо прототип після create()
        prototype.setFirstName("CHANGED");
        prototype.getLanguagesOfStudies().add(StudyLanguage.POLISH);

        List<FreeListener> all = service.getAll();
        assertEquals(1, all.size());
        FreeListener stored = all.getFirst();

        // ім’я не змінилось
        assertNotEquals("CHANGED", stored.getFirstName());
        assertEquals("Yumi", stored.getFirstName());

        // список мов також не повинен підтягнути нову мову з прототипу
        assertEquals(1, stored.getLanguagesOfStudies().size());
        assertFalse(stored.getLanguagesOfStudies().contains(StudyLanguage.POLISH));
    }
}
