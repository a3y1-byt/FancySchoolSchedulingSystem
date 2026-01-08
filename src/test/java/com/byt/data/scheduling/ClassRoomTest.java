package com.byt.data.scheduling;

import com.byt.enums.scheduling.*;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ClassRoomTest {

    private ClassRoom classRoom;
    private Building building1;
    private Building building2;
    private Lesson lesson1;
    private Lesson lesson2;

    @BeforeEach
    void setUp() {
        classRoom = ClassRoom.builder()
                .name("Room 101")
                .floor(1)
                .capacity(30)
                .lessons(new HashSet<>())
                .build();

        building1 = Building.builder()
                .name("Main Building")
                .address("123 Main St")
                .description("Main academic building")
                .classRooms(new HashSet<>())
                .build();

        building2 = Building.builder()
                .name("Science Building")
                .address("456 Science Ave")
                .description("Science labs")
                .classRooms(new HashSet<>())
                .build();

        lesson1 = Lesson.builder()
                .name("Math 101")
                .type(LessonType.LECTURE)
                .mode(LessonMode.OFFLINE)
                .note("Introduction to Calculus")
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .language(StudyLanguage.ENGLISH)
                .weekPattern(WeekPattern.ODD)
                .classRooms(new HashSet<>())
                .semesters(new HashSet<>())
                .build();

        lesson2 = Lesson.builder()
                .name("Physics 101")
                .type(LessonType.LAB)
                .mode(LessonMode.OFFLINE)
                .note("Introduction to Physics")
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 30))
                .language(StudyLanguage.ENGLISH)
                .weekPattern(WeekPattern.EVEN)
                .classRooms(new HashSet<>())
                .semesters(new HashSet<>())
                .build();
    }

    @Test
    @Disabled
    void shouldCreateBidirectionalConnectionWhenAddingBuilding() {
        classRoom.addBuilding(building1);

        assertEquals(building1, classRoom.getBuilding());
        assertTrue(building1.getClassRooms().contains(classRoom));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullBuilding() {
        assertThrows(ValidationException.class, () -> classRoom.addBuilding(null));
    }

    @Test
    @Disabled
    void shouldSwitchBuildingsWhenAddingNewBuilding() {
        classRoom.addBuilding(building1);
        classRoom.addBuilding(building2);

        assertEquals(building2, classRoom.getBuilding());
        assertTrue(building2.getClassRooms().contains(classRoom));
        assertFalse(building1.getClassRooms().contains(classRoom));
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingBuilding() {
        building1.addClassRoom(classRoom);
        ClassRoom classRoom2 = ClassRoom.builder()
                .name("Room 102")
                .floor(1)
                .capacity(30)
                .lessons(new HashSet<>())
                .build();
        building1.addClassRoom(classRoom2);

        classRoom.removeBuilding(building1);

        assertNull(classRoom.getBuilding());
        assertFalse(building1.getClassRooms().contains(classRoom));
    }

    @Test
    @Disabled
    void shouldReturnEarlyWhenRemovingNonMatchingBuilding() {
        classRoom.addBuilding(building1);

        assertDoesNotThrow(() -> classRoom.removeBuilding(building2));
        assertEquals(building1, classRoom.getBuilding());
    }

    @Test
    void shouldCreateBidirectionalConnectionWhenAddingLesson() {
        classRoom.addLesson(lesson1);

        assertTrue(classRoom.getLessons().contains(lesson1));
        assertTrue(lesson1.getClassRooms().contains(classRoom));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullLesson() {
        assertThrows(ValidationException.class, () -> classRoom.addLesson(null));
    }

    @Test
    void shouldReturnEarlyWhenAddingDuplicateLesson() {
        classRoom.addLesson(lesson1);
        classRoom.addLesson(lesson1);

        assertEquals(1, classRoom.getLessons().size());
    }

    @Test
    void shouldAddMultipleLessons() {
        classRoom.addLesson(lesson1);
        classRoom.addLesson(lesson2);

        assertEquals(2, classRoom.getLessons().size());
        assertTrue(classRoom.getLessons().contains(lesson1));
        assertTrue(classRoom.getLessons().contains(lesson2));
        assertTrue(lesson1.getClassRooms().contains(classRoom));
        assertTrue(lesson2.getClassRooms().contains(classRoom));
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingLesson() {
        classRoom.addLesson(lesson1);
        classRoom.addLesson(lesson2);

        classRoom.removeLesson(lesson1);

        assertFalse(classRoom.getLessons().contains(lesson1));
        assertFalse(lesson1.getClassRooms().contains(classRoom));
        assertTrue(classRoom.getLessons().contains(lesson2));
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonExistentLesson() {
        classRoom.addLesson(lesson1);

        assertDoesNotThrow(() -> classRoom.removeLesson(lesson2));
        assertEquals(1, classRoom.getLessons().size());
    }
}