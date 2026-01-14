package com.byt.data.scheduling;

import com.byt.enums.scheduling.DayOfWeek;
import com.byt.enums.scheduling.LessonMode;
import com.byt.enums.scheduling.LessonType;
import com.byt.enums.scheduling.WeekPattern;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class SemesterTest {
    private final Lesson sampleLesson = Lesson.builder()
            .name("SampleName")
            .note("SampleNote")
            .mode(LessonMode.OFFLINE)
            .type(LessonType.LECTURE)
            .dayOfWeek(DayOfWeek.MONDAY)
            .weekPattern(WeekPattern.NORMAL)
            .startTime(LocalTime.now().minusMinutes(10))
            .endTime(LocalTime.now().plusMinutes(10))
            .language(StudyLanguage.POLISH)
            .build();

    private final TestLesson testLesson = new TestLesson(sampleLesson);

    private Semester emptySemester = Semester.builder()
            .name("SampleName")
            .startDate(LocalDate.now().minusMonths(3))
            .endDate(LocalDate.now().plusMonths(3))
            .academicYear(LocalDate.now().getYear())
            .lessons(new HashSet<>())
            .build();

    private Semester filledSemester = Semester.builder()
            .name("SampleName")
            .startDate(LocalDate.now().minusMonths(3))
            .endDate(LocalDate.now().plusMonths(3))
            .academicYear(LocalDate.now().getYear())
            .lessons(new HashSet<>() {{ add(testLesson); }})
            .build();

    @Test
    public void testTriesRemovingItselfFromOther() {
        assertThrows(Success.class, () -> filledSemester.removeLesson(testLesson));
    }

    @Test
    public void testTriesAddingItselfToOthers() {
        assertThrows(Success.class, () -> emptySemester.addLesson(testLesson));
    }

    @Test
    public void testDoesNotAddTheSameEntityTwice() {
        assertDoesNotThrow(() -> filledSemester.addLesson(testLesson));
    }

    @Test
    public void testGettersDoNotHaveEscapingReferences() {
        assertFalse(filledSemester.getLessons() == filledSemester.getLessons());
    }
}
