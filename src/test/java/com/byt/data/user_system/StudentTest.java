package com.byt.data.user_system;

import com.byt.data.scheduling.Group;
import com.byt.data.scheduling.Specialization;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
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
            List.of(StudyLanguage.ENGLISH),
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

    // TODO: watahell1
    static class TestGroup extends Group {

        public TestGroup(Group sample) {
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

    // TODO: watahell2
    static class TestSpecialization extends Specialization {
        public TestSpecialization(Specialization sample) {
            super(
                    sample.getName(),
                    sample.getDescription(),
                    sample.getSubjects(),
                    sample.getStudyPrograms(),
                    sample.getStudents()
            );
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


