package com.byt.data.user_system;

import com.byt.data.scheduling.Group;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// FREELISTENER -------- GROUP
public class FreeListenerTest {

    private final Group sampleGroup = Group.builder()
            .name("Group-12")
            .language(StudyLanguage.ENGLISH)
            .maxCapacity(20)
            .yearOfStudy(3)
            .notes(null)
            .build();

    private final Group sampleGroup2 = Group.builder()
            .name("Group-13")
            .language(StudyLanguage.ENGLISH)
            .maxCapacity(20)
            .yearOfStudy(3)
            .notes(null)
            .build();

    private static final String SAMPLE_EMAIL = "yumi@gmail.com";
    LocalDate dob = LocalDate.of(1997, 3, 7);

    private final FreeListener sampleFreeListener = new FreeListener(
            "Yumi",
            "Hnatiuk",
            "Pies",
            dob,
            "10203040",
            SAMPLE_EMAIL,
            List.of(StudyLanguage.ENGLISH),
            "Some notes"
    );

    @Test
    public void testGetterReturnsCorrectContents_GROUP() {
        FreeListener freeListener = FreeListener.copy(sampleFreeListener);
        Group group = Group.copy(sampleGroup);

        freeListener.addGroup(group);

        Set<Group> expected = new HashSet<>() {{
            add(group);
        }};
        Set<Group> received = freeListener.getGroups();

        assertArrayEquals(expected.toArray(), received.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences_GROUP() {
        FreeListener freeListener = FreeListener.copy(sampleFreeListener);
        Group group = Group.copy(sampleGroup);

        freeListener.addGroup(group);

        Set<Group> expected = new HashSet<>() {{
            add(group);
        }};

        Set<Group> receivedGroups = freeListener.getGroups();
        receivedGroups.remove(group);

        assertArrayEquals(expected.toArray(), freeListener.getGroups().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther_GROUP() {
        FreeListener freeListener = FreeListener.copy(sampleFreeListener);

        FreeListenerTest.TestGroup anotherGroup = new FreeListenerTest.TestGroup(sampleGroup2);

        assertThrows(Success.class, () -> freeListener.addGroup(anotherGroup));
    }

    @Test
    public void testTriesRemovingItselfFromOther_GROUP() {
        FreeListener freeListener = FreeListener.copy(sampleFreeListener);

        FreeListenerTest.TestGroup anotherGroup = new FreeListenerTest.TestGroup(sampleGroup2);
        try {
            freeListener.addGroup(anotherGroup);
        } catch (Success ignored) {
        }
        assertThrows(Success.class, () -> freeListener.removeGroup(anotherGroup));
    }

    static class TestGroup extends Group {

        public TestGroup(Group sample) {
            super();
            this.setName(sample.getName());
            this.setLanguage(sample.getLanguage());
            this.setMaxCapacity(sample.getMaxCapacity());
            this.setYearOfStudy(sample.getYearOfStudy());
            this.setNotes(sample.getNotes());
        }

        @Override
        public void addFreeListener(FreeListener freeListener) {
            throw new Success();
        }

        @Override
        public void removeFreeListener(FreeListener freeListener) {
            throw new Success();
        }
    }
}
