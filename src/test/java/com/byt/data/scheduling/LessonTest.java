package com.byt.data.scheduling;

import com.byt.data.user_system.Teacher;
import com.byt.enums.scheduling.*;
import com.byt.enums.user_system.StudyLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LessonTest {

    private Lesson lesson;
    private Subject subject1;
    private Subject subject2;
    private Group group1;
    private Group group2;
    private Teacher teacher1;
    private Teacher teacher2;
    private Semester semester1;
    private Semester semester2;
    private ClassRoom classRoom1;
    private ClassRoom classRoom2;

    @BeforeEach
    void setUp() {
        lesson = Lesson.builder()
                .name("Math 101")
                .type(LessonType.LECTURE)
                .mode(LessonMode.OFFLINE)
                .note("Introduction to Calculus")
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .language(StudyLanguage.ENGLISH)
                .weekPattern(WeekPattern.NORMAL)
                .build();

        subject1 = Subject.builder()
                .name("Mathematics")
                .hours(100)
                .types(new ArrayList<>())
                .build();

        subject2 = Subject.builder()
                .name("Physics")
                .hours(75)
                .types(new ArrayList<>())
                .build();

        group1 = Group.builder()
                .name("Group A")
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(20)
                .yearOfStudy(1)
                .build();

        group2 = Group.builder()
                .name("Group B")
                .language(StudyLanguage.POLISH)
                .maxCapacity(20)
                .yearOfStudy(2)
                .build();

        teacher1 = new Teacher(
                "John", "Doe", "Smith",
                LocalDate.of(1980, 1, 1), "123456789", "john.doe@example.com",
                LocalDate.of(2020, 1, 1), "Dr.", "Professor"
        );

        teacher2 = new Teacher(
                "Jane", "Smith", "Johnson",
                LocalDate.of(1985, 5, 15), "987654321", "jane.smith@example.com",
                LocalDate.of(2019, 9, 1), "Dr.", "Associate Professor"
        );

        semester1 = Semester.builder()
                .name("Fall 2024")
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .academicYear(2024)
                .build();

        semester2 = Semester.builder()
                .name("Spring 2025")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 5, 31))
                .academicYear(2025)
                .build();

        classRoom1 = ClassRoom.builder()
                .name("Room 101")
                .floor(1)
                .capacity(30)
                .build();

        classRoom2 = ClassRoom.builder()
                .name("Room 202")
                .floor(2)
                .capacity(50)
                .build();
    }

    @Test
    void shouldCreateBidirectionalConnectionWhenAddingSubject() {

        lesson.addSubject(subject1);

        assertEquals(subject1, lesson.getSubject());
        assertTrue(subject1.getLessons().contains(lesson));
    }

    @Test
    void shouldReplaceOldSubjectWhenAddingNewSubject() {
        lesson.addSubject(subject1);
        lesson.addSubject(subject2);

        assertEquals(subject2, lesson.getSubject());
        assertTrue(subject2.getLessons().contains(lesson));
        assertFalse(subject1.getLessons().contains(lesson));
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingSubject() {
        lesson.addSubject(subject1);

        lesson.removeSubject(subject1);

        assertNull(lesson.getSubject());
        assertFalse(subject1.getLessons().contains(lesson));
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonMatchingSubject() {
        lesson.addSubject(subject1);

        assertDoesNotThrow(() -> lesson.removeSubject(subject2));
        assertEquals(subject1, lesson.getSubject());
    }

    @Test
    void shouldHandleRemovingSubjectWhenNoSubjectSet() {
        assertDoesNotThrow(() -> lesson.removeSubject(subject1));
        assertNull(lesson.getSubject());
    }

    @Test
    void shouldCreateBidirectionalConnectionWhenAddingGroup() {
        lesson.addGroup(group1);

        assertEquals(group1, lesson.getGroup());
        assertTrue(group1.getLessons().contains(lesson));
    }

    @Test
    void shouldReplaceOldGroupWhenAddingNewGroup() {
        lesson.addGroup(group1);
        lesson.addGroup(group2);

        assertEquals(group2, lesson.getGroup());
        assertTrue(group2.getLessons().contains(lesson));
        assertFalse(group1.getLessons().contains(lesson));
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingGroup() {
        lesson.addGroup(group1);

        lesson.removeGroup(group1);

        assertNull(lesson.getGroup());
        assertFalse(group1.getLessons().contains(lesson));
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonMatchingGroup() {
        lesson.addGroup(group1);

        assertDoesNotThrow(() -> lesson.removeGroup(group2));
        assertEquals(group1, lesson.getGroup());
    }

    @Test
    void shouldHandleRemovingGroupWhenNoGroupSet() {
        assertDoesNotThrow(() -> lesson.removeGroup(group1));
        assertNull(lesson.getGroup());
    }

    @Test
    void shouldSetTeacherWhenAddingTeacher() {
        lesson.addTeacher(teacher1);

        assertEquals(teacher1, lesson.getTeacher());
    }

    @Test
    void shouldThrowExceptionWhenAddingNullTeacher() {
        assertThrows(Exception.class, () -> lesson.addTeacher(null));
    }

    @Test
    void shouldReplaceOldTeacherWhenAddingNewTeacher() {
        lesson.addTeacher(teacher1);
        lesson.addTeacher(teacher2);

        assertEquals(teacher2, lesson.getTeacher());
    }

    @Test
    void shouldRemoveTeacherWhenRemovingTeacher() {
        lesson.addTeacher(teacher1);

        lesson.removeTeacher(teacher1);

        assertNull(lesson.getTeacher());
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonMatchingTeacher() {
        lesson.addTeacher(teacher1);

        assertDoesNotThrow(() -> lesson.removeTeacher(teacher2));
        assertEquals(teacher1, lesson.getTeacher());
    }

    @Test
    void shouldHandleRemovingTeacherWhenNoTeacherSet() {
        assertDoesNotThrow(() -> lesson.removeTeacher(teacher1));
        assertNull(lesson.getTeacher());
    }

    @Test
    void shouldCreateBidirectionalConnectionWhenAddingSemester() {
        lesson.addSemester(semester1);

        assertTrue(lesson.getSemesters().contains(semester1));
        assertTrue(semester1.getLessons().contains(lesson));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullSemester() {
        assertThrows(Exception.class, () -> lesson.addSemester(null));
    }

    @Test
    void shouldAddMultipleSemesters() {
        lesson.addSemester(semester1);
        lesson.addSemester(semester2);

        assertEquals(2, lesson.getSemesters().size());
        assertTrue(lesson.getSemesters().contains(semester1));
        assertTrue(lesson.getSemesters().contains(semester2));
        assertTrue(semester1.getLessons().contains(lesson));
        assertTrue(semester2.getLessons().contains(lesson));
    }

    @Test
    void shouldReturnEarlyWhenAddingDuplicateSemester() {
        lesson.addSemester(semester1);
        lesson.addSemester(semester1);

        assertEquals(1, lesson.getSemesters().size());
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingSemester() {
        lesson.addSemester(semester1);
        lesson.addSemester(semester2);

        lesson.removeSemester(semester1);

        assertFalse(lesson.getSemesters().contains(semester1));
        assertFalse(semester1.getLessons().contains(lesson));
        assertTrue(lesson.getSemesters().contains(semester2));
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonExistentSemester() {
        lesson.addSemester(semester1);

        assertDoesNotThrow(() -> lesson.removeSemester(semester2));
        assertEquals(1, lesson.getSemesters().size());
    }

    @Test
    void shouldReturnEmptySetWhenNoSemestersAdded() {
        assertTrue(lesson.getSemesters().isEmpty());
    }

    @Test
    void shouldCreateBidirectionalConnectionWhenAddingClassRoom() {
        lesson.addClassRoom(classRoom1);

        assertTrue(lesson.getClassRooms().contains(classRoom1));
        assertTrue(classRoom1.getLessons().contains(lesson));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullClassRoom() {
        assertThrows(Exception.class, () -> lesson.addClassRoom(null));
    }

    @Test
    void shouldReturnEarlyWhenAddingDuplicateClassRoom() {
        lesson.addClassRoom(classRoom1);
        lesson.addClassRoom(classRoom1);

        assertEquals(1, lesson.getClassRooms().size());
    }

    @Test
    void shouldAddMultipleClassRooms() {
        lesson.addClassRoom(classRoom1);
        lesson.addClassRoom(classRoom2);

        assertEquals(2, lesson.getClassRooms().size());
        assertTrue(lesson.getClassRooms().contains(classRoom1));
        assertTrue(lesson.getClassRooms().contains(classRoom2));
        assertTrue(classRoom1.getLessons().contains(lesson));
        assertTrue(classRoom2.getLessons().contains(lesson));
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingClassRoom() {
        lesson.addClassRoom(classRoom1);
        lesson.addClassRoom(classRoom2);

        lesson.removeClassRoom(classRoom1);

        assertFalse(lesson.getClassRooms().contains(classRoom1));
        assertFalse(classRoom1.getLessons().contains(lesson));
        assertTrue(lesson.getClassRooms().contains(classRoom2));
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonExistentClassRoom() {
        lesson.addClassRoom(classRoom1);

        assertDoesNotThrow(() -> lesson.removeClassRoom(classRoom2));
        assertEquals(1, lesson.getClassRooms().size());
    }

    @Test
    void shouldReturnEmptySetWhenNoClassRoomsAdded() {
        assertTrue(lesson.getClassRooms().isEmpty());
    }

    @Test
    void shouldReturnCopyOfClassRoomsNotOriginalSet() {
        lesson.addClassRoom(classRoom1);

        var classRooms1 = lesson.getClassRooms();
        var classRooms2 = lesson.getClassRooms();

        assertNotSame(classRooms1, classRooms2);
    }

    @Test
    void shouldReturnCopyOfSemestersNotOriginalSet() {
        lesson.addSemester(semester1);

        var semesters1 = lesson.getSemesters();
        var semesters2 = lesson.getSemesters();

        assertNotSame(semesters1, semesters2);
    }

    @Test
    void shouldHandleComplexSubjectSwitchingScenario() {
        lesson.addSubject(subject1);
        assertEquals(subject1, lesson.getSubject());
        assertTrue(subject1.getLessons().contains(lesson));

        lesson.addSubject(subject2);
        assertEquals(subject2, lesson.getSubject());
        assertTrue(subject2.getLessons().contains(lesson));
        assertFalse(subject1.getLessons().contains(lesson));

        lesson.addSubject(subject1);
        assertEquals(subject1, lesson.getSubject());
        assertTrue(subject1.getLessons().contains(lesson));
        assertFalse(subject2.getLessons().contains(lesson));
    }

    @Test
    void shouldHandleComplexGroupSwitchingScenario() {
        lesson.addGroup(group1);
        assertEquals(group1, lesson.getGroup());
        assertTrue(group1.getLessons().contains(lesson));

        lesson.addGroup(group2);
        assertEquals(group2, lesson.getGroup());
        assertTrue(group2.getLessons().contains(lesson));
        assertFalse(group1.getLessons().contains(lesson));
    }
}
