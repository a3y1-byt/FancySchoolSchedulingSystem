package com.byt.data.user_system;

import com.byt.data.scheduling.Group;
import com.byt.data.scheduling.Specialization;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.exception.ValidationException;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

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

    private final Student sampleStudent = new Student(
            "Yumi",
            "Hnatiuk",
            "Pies",
            dob,
            "10203040",
            SAMPLE_EMAIL,
            Set.of(StudyLanguage.ENGLISH),
            StudyStatus.ACTIVE
    );

    private final Specialization sampleSpecialization = Specialization.builder()
            .name("ComputerSCience")
            .description("hello")
            .subjects(new HashSet<>())
            .studyPrograms(new HashSet<>())
            .students(new HashSet<>())
            .build();

    private final Specialization sampleSpecialization2 = Specialization.builder()
            .name("DataScience")
            .description("hey")
            .subjects(new HashSet<>())
            .studyPrograms(new HashSet<>())
            .students(new HashSet<>())
            .build();


    // STUDENT -------- GROUP
    @Test
    public void testGetterReturnsCorrectContents_GROUP() {
        Student student = Student.copy(sampleStudent);
        Group group = Group.copy(sampleGroup);

        student.addGroup(group);

        Set<Group> expected = new HashSet<>() {{
            add(group);
        }};
        Set<Group> received = student.getGroups();

        assertArrayEquals(expected.toArray(), received.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences_GROUP() {
        Student student = Student.copy(sampleStudent);
        Group group = Group.copy(sampleGroup);

        student.addGroup(group);

        Set<Group> expected = new HashSet<>() {{
            add(group);
        }};

        Set<Group> receivedGroups = student.getGroups();
        receivedGroups.remove(group);

        assertArrayEquals(expected.toArray(), student.getGroups().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther_GROUP() {
        Student student = Student.copy(sampleStudent);

        TestGroup anotherGroup = new TestGroup(sampleGroup2);

        assertThrows(Success.class, () -> student.addGroup(anotherGroup));
    }

    @Test
    public void testTriesRemovingItselfFromOther_GROUP() {
        Student student = Student.copy(sampleStudent);

        TestGroup anotherGroup = new TestGroup(sampleGroup2);
        try {
            student.addGroup(anotherGroup);
        } catch (Success ignored) {
        }
        assertThrows(Success.class, () -> student.removeGroup(anotherGroup));
    }

    @Test
    public void shouldThrowWhenAddingNullGroup() {
        Student student = Student.copy(sampleStudent);
        assertThrows(ValidationException.class, () -> student.addGroup(null));
    }

    @Test
    public void shouldThrowWhenRemovingNullGroup() {
        Student student = Student.copy(sampleStudent);
        assertThrows(ValidationException.class, () -> student.removeGroup(null));
    }

    @Test
    public void shouldReturnEarlyWhenAddingDuplicateGroup() {
        Student student = Student.copy(sampleStudent);
        Group group = Group.copy(sampleGroup);

        student.addGroup(group);
        student.addGroup(group);

        assertEquals(1, student.getGroups().size());
        assertTrue(student.getGroups().contains(group));
    }

    // STUDENT -------- SPECIALIZATION
    @Test
    public void testGetterReturnsCorrectContents_SPECIALIZATION() {
        Student student = Student.copy(sampleStudent);
        student.addSpecialization(sampleSpecialization);

        Set<Specialization> expected = new HashSet<>() {{
            add(sampleSpecialization);
        }};
        Set<Specialization> specializations = student.getSpecializations();

        assertArrayEquals(expected.toArray(), specializations.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences_SPECIALIZATION() {
        Student student = Student.copy(sampleStudent);
        student.addSpecialization(sampleSpecialization);

        Set<Specialization> expected = new HashSet<>() {{
            add(sampleSpecialization);
        }};

        Set<Specialization> receivedSpecializations = student.getSpecializations();
        receivedSpecializations.remove(sampleSpecialization);

        assertArrayEquals(expected.toArray(), student.getSpecializations().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther_SPECIALIZATION() {
        Student student = Student.copy(sampleStudent);
        TestSpecialization anotherSpecialization = new TestSpecialization(sampleSpecialization2);

        assertThrows(Success.class, () -> student.addSpecialization(anotherSpecialization));
    }

    @Test
    public void testTriesRemovingItselfFromOther_SPECIALIZATION() {
        Student student = Student.copy(sampleStudent);
        TestSpecialization anotherSpecialization = new TestSpecialization(sampleSpecialization2);

        try {
            student.addSpecialization(anotherSpecialization);
        } catch (Success ignored) {
        }
        assertThrows(Success.class, () -> student.removeSpecialization(anotherSpecialization));
    }

    @Test
    public void shouldThrowWhenAddingNullSpecialization() {
        Student student = Student.copy(sampleStudent);
        assertThrows(ValidationException.class, () -> student.addSpecialization(null));
    }

    @Test
    public void shouldThrowWhenRemovingNullSpecialization() {
        Student student = Student.copy(sampleStudent);
        assertThrows(ValidationException.class, () -> student.removeSpecialization(null));
    }

    @Test
    public void shouldReturnEarlyWhenAddingDuplicateSpecialization() {
        Student student = Student.copy(sampleStudent);
        Specialization specialization = Specialization.builder()
                .name("ComputerScience")
                .description("hello")
                .subjects(new HashSet<>())
                .studyPrograms(new HashSet<>())
                .students(new HashSet<>())
                .build();

        student.addSpecialization(specialization);
        student.addSpecialization(specialization);

        assertEquals(1, student.getSpecializations().size());
        assertTrue(student.getSpecializations().contains(specialization));
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
        public void addStudent(Student student) {
            throw new Success();
        }

        @Override
        public void removeStudent(Student student) {
            throw new Success();
        }
    }

    static class TestSpecialization extends Specialization {

        public TestSpecialization(Specialization sample) {
            super();
            this.setName(sample.getName());
            this.setDescription(sample.getDescription());
        }

        @Override
        public void addStudent(Student student) {
            throw new Success();
        }

        @Override
        public void removeStudent(Student student) {
            throw new Success();
        }
    }

}

