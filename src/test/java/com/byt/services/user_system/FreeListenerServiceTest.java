package com.byt.services.user_system;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import com.byt.data.user_system.FreeListener;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.exception.ValidationException;
import com.byt.exception.ExceptionCode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FreeListenerServiceTest extends CRUDServiceTest<FreeListener> {

    private static final String SAMPLE_EMAIL = "yumi@gmail.com";

    public FreeListenerServiceTest() {
        super(DataSaveKeys.FREE_LISTENERS, saveLoadService -> new FreeListenerService(saveLoadService));
    }

    @Override
    protected String getSampleObjectId() {
        return SAMPLE_EMAIL;
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
                SAMPLE_EMAIL,
                Set.of(StudyLanguage.ENGLISH),
                "Some notes"
        );
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

        String oldEmail = getSampleObjectId();
        Optional<FreeListener> beforeOpt = service.get(oldEmail);
        assertTrue(beforeOpt.isPresent());

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        String newEmail = "yumiii@gmail.com";
        FreeListener prototype = new FreeListener(
                "Yumpa",
                "Hnatiukk",
                "Piess",
                dob,
                "3809691046",
                newEmail,
                Set.of(StudyLanguage.POLISH),
                "woof woof woof"
        );

        service.update(oldEmail, prototype);
        assertTrue(service.get(oldEmail).isEmpty());
        Optional<FreeListener> afterOpt = service.get(newEmail);
        assertTrue(afterOpt.isPresent());
        FreeListener updated = afterOpt.get();

        assertEquals("Yumpa", updated.getFirstName());
        assertEquals("Hnatiukk", updated.getLastName());
        assertEquals("Piess", updated.getFamilyName());
        assertEquals(dob, updated.getDateOfBirth());
        assertEquals("3809691046", updated.getPhoneNumber());
        assertEquals(newEmail, updated.getEmail());
        assertEquals(Set.of(StudyLanguage.POLISH), updated.getLanguagesOfStudies());
        assertEquals("woof woof woof", updated.getNotes());
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
                        Set.of(StudyLanguage.ENGLISH),
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
                        Set.of(StudyLanguage.ENGLISH),
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
                        Set.of(StudyLanguage.ENGLISH),
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
                        Set.of(StudyLanguage.ENGLISH),
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
                        Set.of(StudyLanguage.ENGLISH),
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
                        Set.of(StudyLanguage.ENGLISH),
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
                        Set.of(StudyLanguage.ENGLISH),
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
                Set.of(StudyLanguage.ENGLISH, StudyLanguage.POLISH),
                "Woof"
        );

        assertNotNull(created);
        assertNotNull(created.getEmail());

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
                        Set.of(),
                        "notes"
                )
        );
    }

    // list contains only null element
    @Test
    public void createFreeListenerWithNullLanguageElement() {
        FreeListenerService service = (FreeListenerService) emptyService;

        LocalDate dob = LocalDate.now().minusYears(21);

        Set<StudyLanguage> langs = new HashSet<>();
        langs.add(StudyLanguage.ENGLISH);
        langs.add(null);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi@gmail.com",
                        langs,
                        "Woof"
                )
        );
    }

    // list contains duplicate
    @Test
    public void languagesGetterReturnsCopy() throws IOException {
        FreeListenerService service = (FreeListenerService) emptyService;
        LocalDate dob = LocalDate.now().minusYears(21);

        FreeListener created = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                Set.of(StudyLanguage.ENGLISH),
                "Woof"
        );

        Set<StudyLanguage> langs = created.getLanguagesOfStudies();
        langs.add(StudyLanguage.POLISH);

        // must not affect internal state
        assertEquals(1, created.getLanguagesOfStudies().size());
        assertFalse(created.getLanguagesOfStudies().contains(StudyLanguage.POLISH));
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
                        Set.of(StudyLanguage.ENGLISH),
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
                Set.of(StudyLanguage.ENGLISH),
                null
        );

        assertNotNull(created);
        assertNotNull(created.getEmail());
    }

    // notes is blank
    @Test
    public void createFreeListenerWithBlankNotes() {
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
                        Set.of(StudyLanguage.ENGLISH),
                        " "
                )
        );
    }

    // notes is ""
    @Test
    public void createFreeListenerWithEmptyNotes() {
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
                        Set.of(StudyLanguage.ENGLISH),
                        ""
                )
        );
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

        Set<StudyLanguage> langs = new HashSet<>();
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

        prototype.setFirstName("CHANGED");
        prototype.getLanguagesOfStudies().add(StudyLanguage.POLISH);

        List<FreeListener> all = service.getAll();
        assertEquals(1, all.size());
        FreeListener stored = all.getFirst();

        assertNotEquals("CHANGED", stored.getFirstName());
        assertEquals("Yumi", stored.getFirstName());

        assertEquals(1, stored.getLanguagesOfStudies().size());
        assertFalse(stored.getLanguagesOfStudies().contains(StudyLanguage.POLISH));
    }
}
