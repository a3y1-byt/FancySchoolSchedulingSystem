package com.byt.data.scheduling;

import com.byt.data.user_system.Teacher;
import com.byt.enums.scheduling.*;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SubjectTest {
    private final Specialization sampleSpecialization = Specialization.builder()
            .name("SampleSpecialization")
            .description("SampleDescription")
            .subjects(new HashSet<>())
            .studyPrograms(new HashSet<>())
            .students(new HashSet<>())
            .build();

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

    private TestSpecialization testSpecialization = new TestSpecialization(sampleSpecialization);
    private TestLesson testLesson = new TestLesson(sampleLesson);

    private Subject filledSubject = Subject.builder()
            .name("SampleName")
            .types(new ArrayList<>() {{
                add(SubjectType.ELECTIVE_SUBJECT);
            }})
            .hours(180)
            .specializations(new HashSet<>() {{ add(testSpecialization); }})
            .lessons(new HashSet<>() {{ add(testLesson); }})
            .build();

    private Subject emptySubject = Subject.builder()
            .name("SampleName")
            .types(new ArrayList<>() {{
                add(SubjectType.ELECTIVE_SUBJECT);
            }})
            .hours(180)
            .specializations(new HashSet<>())
            .lessons(new HashSet<>())
            .build();

    @Test
    public void testTriesRemovingItselfFromOther() {
        assertAll(
                () -> assertThrows(Success.class, () -> filledSubject.removeSpecialization(testSpecialization)),
                () -> assertThrows(Success.class, () -> filledSubject.removeLesson(testLesson))
        );
    }

    @Test
    public void testTriesAddingItselfToOthers() {
        assertAll(
                () -> assertThrows(Success.class, () -> emptySubject.addSpecialization(testSpecialization)),
                () -> assertThrows(Success.class, () -> emptySubject.addLesson(testLesson))
        );
    }

    @Test
    public void testDoesNotAddTheSameEntityTwice() {
        assertAll(
                () -> assertDoesNotThrow(() -> filledSubject.addSpecialization(testSpecialization)),
                () -> assertDoesNotThrow(() -> filledSubject.addLesson(testLesson))
        );
    }

    @Test
    public void testGettersDoNotHaveEscapingReferences() {
        assertAll(
                () -> assertFalse(filledSubject.getSpecializations() == filledSubject.getSpecializations()),
                () -> assertFalse(filledSubject.getLessons() == filledSubject.getLessons())
        );
    }
}

class TestLesson extends Lesson {
    public TestLesson(Lesson prototype) {
        super(prototype.getName(), prototype.getType(), prototype.getMode(), prototype.getNote(),
                prototype.getDayOfWeek(), prototype.getStartTime(), prototype.getEndTime(), prototype.getLanguage(),
                prototype.getWeekPattern(), prototype.getSubject(), prototype.getGroup(), prototype.getTeacher(),
                prototype.getClassRooms(), prototype.getSemesters());
    }

    @Override
    public void addSubject(Subject subject) {
        throw new Success();
    }

    @Override
    public void removeSubject(Subject subject) {
        throw new Success();
    }

    @Override
    public void addGroup(Group group) {
        throw new Success();
    }

    @Override
    public void removeGroup(Group group) {
        throw new Success();
    }

    @Override
    public void addTeacher(Teacher teacher) {
        throw new Success();
    }

    @Override
    public void removeTeacher(Teacher teacher) {
        throw new Success();
    }

    @Override
    public void addSemester(Semester semester) {
        throw new Success();
    }

    @Override
    public void removeSemester(Semester semester) {
        throw new Success();
    }

    @Override
    public void addClassRoom(ClassRoom classRoom) {
        throw new Success();
    }

    @Override
    public void removeClassRoom(ClassRoom classRoom) {
        throw new Success();
    }
}