package com.byt.data.scheduling;

import com.byt.data.user_system.FreeListener;
import com.byt.data.user_system.Student;
import com.byt.enums.scheduling.DayOfWeek;
import com.byt.enums.scheduling.LessonMode;
import com.byt.enums.scheduling.LessonType;
import com.byt.enums.scheduling.WeekPattern;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.exception.ValidationException;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {


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

    LocalDate dob = LocalDate.of(1997, 3, 7);

    private final Student sampleStudent = new Student(
            "Yumi",
            "Hnatiuk",
            "Pies",
            dob,
            "10203040",
            "yumi@gmail.com",
            Set.of(StudyLanguage.ENGLISH),
            StudyStatus.ACTIVE
    );

    private final Student sampleStudent2 = new Student(
            "Anna",
            "Hnatiuk",
            "Yumi",
            dob,
            "30201040",
            "yumi2@gmail.com",
            Set.of(StudyLanguage.ENGLISH),
            StudyStatus.ACTIVE
    );

    private final FreeListener sampleFreeListener = new FreeListener(
            "Yumi",
            "Hnatiuk",
            "Pies",
            dob,
            "10203040",
            "yumi3@gmail.com",
            Set.of(StudyLanguage.ENGLISH),
            "Some notes"
    );

    private final FreeListener sampleFreeListener2 = new FreeListener(
            "Anna",
            "Hnatiuk",
            "Yumi",
            dob,
            "30201040",
            "yumi4@gmail.com",
            Set.of(StudyLanguage.ENGLISH),
            "Some notes"
    );

    private final Lesson sampleLesson = Lesson.builder()
            .name("Algorithms Lecture")
            .type(LessonType.LECTURE)
            .mode(LessonMode.OFFLINE)
            .note("dfhngfsdasfds")
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 30))
            .language(StudyLanguage.ENGLISH)
            .weekPattern(WeekPattern.EVEN)
            .build();

    private final Lesson sampleLesson2 = Lesson.builder()
            .name("BYT")
            .type(LessonType.LECTURE)
            .mode(LessonMode.OFFLINE)
            .note("ahdfadfadfg")
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 30))
            .language(StudyLanguage.ENGLISH)
            .weekPattern(WeekPattern.EVEN)
            .build();


    // GROUP -------- STUDENT
    @Test
    public void testGetterReturnsCorrectContents_STUDENT() {
        Group group = Group.copy(sampleGroup);
        Student student = Student.copy(sampleStudent);

        group.addStudent(student);

        Set<Student> expected = new HashSet<>() {{
            add(student);
        }};
        Set<Student> received = group.getStudents();

        assertArrayEquals(expected.toArray(), received.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences_STUDENT() {
        Group group = Group.copy(sampleGroup);
        Student student = Student.copy(sampleStudent);

        group.addStudent(student);

        Set<Student> expected = new HashSet<>() {{
            add(student);
        }};

        Set<Student> receivedStudents = group.getStudents();
        receivedStudents.remove(student);

        assertArrayEquals(expected.toArray(), group.getStudents().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther_STUDENT() {
        Group group = Group.copy(sampleGroup);

        GroupTest.TestStudent anotherStudent = new GroupTest.TestStudent(sampleStudent2);

        assertThrows(Success.class, () -> group.addStudent(anotherStudent));
    }

    @Test
    public void testTriesRemovingItselfFromOther_STUDENT() {
        Group group = Group.copy(sampleGroup);

        GroupTest.TestStudent anotherStudent = new GroupTest.TestStudent(sampleStudent2);
        try {
            group.addStudent(anotherStudent);
        } catch (Success ignored) {
        }
        assertThrows(Success.class, () -> group.removeStudent(anotherStudent));
    }

    @Test
    public void shouldThrowWhenAddingNullStudent() {
        Group group = Group.copy(sampleGroup);
        assertThrows(ValidationException.class, () -> group.addStudent(null));
    }

    @Test
    public void shouldThrowWhenRemovingNullStudent() {
        Group group = Group.copy(sampleGroup);
        assertThrows(ValidationException.class, () -> group.removeStudent(null));
    }

    @Test
    public void shouldReturnEarlyWhenAddingDuplicateStudent() {
        Group group = Group.copy(sampleGroup);
        Student student = Student.copy(sampleStudent);

        group.addStudent(student);
        group.addStudent(student);

        assertEquals(1, group.getStudents().size());
        assertTrue(group.getStudents().contains(student));
    }

    // GROUP -------- FREELISTENER
    @Test
    public void testGetterReturnsCorrectContents_FREELISTENER() {
        Group group = Group.copy(sampleGroup);
        FreeListener freeListener = FreeListener.copy(sampleFreeListener);

        group.addFreeListener(freeListener);

        Set<FreeListener> expected = new HashSet<>() {{
            add(freeListener);
        }};
        Set<FreeListener> received = group.getFreeListeners();

        assertArrayEquals(expected.toArray(), received.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences_FREELISTENER() {
        Group group = Group.copy(sampleGroup);
        FreeListener freeListener = FreeListener.copy(sampleFreeListener);

        group.addFreeListener(freeListener);

        Set<FreeListener> expected = new HashSet<>() {{
            add(freeListener);
        }};

        Set<FreeListener> receivedFreeListeners = group.getFreeListeners();
        receivedFreeListeners.remove(freeListener);

        assertArrayEquals(expected.toArray(), group.getFreeListeners().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther_FREELISTENER() {
        Group group = Group.copy(sampleGroup);

        GroupTest.TestFreeListener anotherFreeListener = new GroupTest.TestFreeListener(sampleFreeListener2);

        assertThrows(Success.class, () -> group.addFreeListener(anotherFreeListener));
    }

    @Test
    public void testTriesRemovingItselfFromOther_FREELISTENER() {
        Group group = Group.copy(sampleGroup);

        GroupTest.TestFreeListener anotherFreeListener = new GroupTest.TestFreeListener(sampleFreeListener2);
        try {
            group.addFreeListener(anotherFreeListener);
        } catch (Success ignored) {
        }
        assertThrows(Success.class, () -> group.removeFreeListener(anotherFreeListener));
    }

    @Test
    public void shouldThrowWhenAddingNullFreeListener() {
        Group group = Group.copy(sampleGroup);
        assertThrows(ValidationException.class, () -> group.addFreeListener(null));
    }

    @Test
    public void shouldThrowWhenRemovingNullFreeListener() {
        Group group = Group.copy(sampleGroup);
        assertThrows(ValidationException.class, () -> group.removeFreeListener(null));
    }

    @Test
    public void shouldReturnEarlyWhenAddingDuplicateFreeListener() {
        Group group = Group.copy(sampleGroup);
        FreeListener fl = FreeListener.copy(sampleFreeListener);

        group.addFreeListener(fl);
        group.addFreeListener(fl);

        assertEquals(1, group.getFreeListeners().size());
        assertTrue(group.getFreeListeners().contains(fl));
    }

    // GROUP -------- LESSON
    @Test
    public void testGetterReturnsCorrectContents_LESSON() {
        Group group = Group.copy(sampleGroup);
        Lesson lesson = Lesson.copy(sampleLesson);

        group.addLesson(lesson);

        Set<Lesson> expected = new HashSet<>() {{
            add(lesson);
        }};
        Set<Lesson> received = group.getLessons();

        assertArrayEquals(expected.toArray(), received.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences_LESSON() {
        Group group = Group.copy(sampleGroup);
        Lesson lesson = Lesson.copy(sampleLesson);

        group.addLesson(lesson);

        Set<Lesson> expected = new HashSet<>() {{
            add(lesson);
        }};

        Set<Lesson> receivedLessons = group.getLessons();
        receivedLessons.remove(lesson);

        assertArrayEquals(expected.toArray(), group.getLessons().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther_LESSON() {
        Group group = Group.copy(sampleGroup);

        GroupTest.TestLesson anotherLesson = new GroupTest.TestLesson(sampleLesson2);

        assertThrows(Success.class, () -> group.addLesson(anotherLesson));
    }

    @Test
    public void testTriesRemovingItselfFromOther_LESSON() {
        Group group = Group.copy(sampleGroup);

        Lesson lesson = new TestLessonRemoveThrows(sampleLesson2);

        group.addLesson(lesson);

        assertThrows(Success.class, () -> group.removeLesson(lesson));
    }

    @Test
    public void shouldThrowWhenAddingNullLesson() {
        Group group = Group.copy(sampleGroup);
        assertThrows(ValidationException.class, () -> group.addLesson(null));
    }

    @Test
    public void shouldThrowWhenRemovingNullLesson() {
        Group group = Group.copy(sampleGroup);
        assertThrows(ValidationException.class, () -> group.removeLesson(null));
    }

    @Test
    public void shouldReturnEarlyWhenAddingDuplicateLesson() {
        Group group = Group.copy(sampleGroup);
        Lesson lesson = Lesson.copy(sampleLesson);

        group.addLesson(lesson);
        group.addLesson(lesson);

        assertEquals(1, group.getLessons().size());
        assertTrue(group.getLessons().contains(lesson));
    }

    static class TestStudent extends Student {
        public TestStudent(Student sample) {
            super(
                    sample.getFirstName(),
                    sample.getLastName(),
                    sample.getFamilyName(),
                    sample.getDateOfBirth(),
                    sample.getPhoneNumber(),
                    sample.getEmail(),
                    sample.getLanguagesOfStudies(),
                    sample.getStudiesStatus()
            );
        }

        @Override
        public void addGroup(Group group) {
            throw new Success();
        }

        @Override
        public void removeGroup(Group group) {
            throw new Success();
        }
    }

    static class TestFreeListener extends FreeListener {
        public TestFreeListener(FreeListener sample) {
            super(
                    sample.getFirstName(),
                    sample.getLastName(),
                    sample.getFamilyName(),
                    sample.getDateOfBirth(),
                    sample.getPhoneNumber(),
                    sample.getEmail(),
                    sample.getLanguagesOfStudies(),
                    sample.getNotes()
            );
        }

        @Override
        public void addGroup(Group group) {
            throw new Success();
        }

        @Override
        public void removeGroup(Group group) {
            throw new Success();
        }
    }

    static class TestLesson extends Lesson {
        public TestLesson(Lesson sample) {
            super();
            this.setName(sample.getName());
            this.setType(sample.getType());
            this.setMode(sample.getMode());
            this.setNote(sample.getNote());
            this.setDayOfWeek(sample.getDayOfWeek());
            this.setStartTime(sample.getStartTime());
            this.setEndTime(sample.getEndTime());
            this.setLanguage(sample.getLanguage());
            this.setWeekPattern(sample.getWeekPattern());
        }

        @Override
        public void addGroup(Group group) {
            throw new Success();
        }

        @Override
        public void removeGroup(Group group) {
            throw new Success();
        }
    }

    static class TestLessonRemoveThrows extends Lesson {
        public TestLessonRemoveThrows(Lesson sample) {
            super();
            this.setName(sample.getName());
            this.setType(sample.getType());
            this.setMode(sample.getMode());
            this.setNote(sample.getNote());
            this.setDayOfWeek(sample.getDayOfWeek());
            this.setStartTime(sample.getStartTime());
            this.setEndTime(sample.getEndTime());
            this.setLanguage(sample.getLanguage());
            this.setWeekPattern(sample.getWeekPattern());
        }

        @Override
        public void removeGroup(Group group) {
            throw new Success();
        }
    }
}
