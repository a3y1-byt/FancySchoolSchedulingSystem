package com.byt.services.scheduling;

import com.byt.data.scheduling.Lesson;
import com.byt.enums.scheduling.DayOfWeek;
import com.byt.enums.scheduling.LessonMode;
import com.byt.enums.scheduling.LessonType;
import com.byt.enums.scheduling.WeekPattern;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LessonServiceTest extends CRUDServiceTest<Lesson> {

    protected LessonServiceTest() {
        super(DataSaveKeys.LESSONS, LessonService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Algorithms Lecture";
    }

    @Override
    protected Lesson getSampleObject() {
        return Lesson.builder()
                .name("Algorithms Lecture")
                .type(LessonType.LECTURE)
                .mode(LessonMode.OFFLINE)
                .note("")
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 30))
                .language(StudyLanguage.ENGLISH)
                .weekPattern(WeekPattern.EVEN)
                .build();
    }

    @Override
    protected void alterEntity(Lesson lesson) {
        lesson.setNote("Updated note");
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        LessonService service = (LessonService) emptyService;
        Lesson lesson = Lesson.builder()
                .name("Placeholder Lesson")
                .type(LessonType.EXERCISE)
                .mode(LessonMode.ONLINE)
                .note("Placeholder")
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 30))
                .language(StudyLanguage.ENGLISH)
                .weekPattern(WeekPattern.ODD)
                .build();
        service.create(lesson);
        Optional<Lesson> loaded = service.get("Placeholder Lesson");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Lesson", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        LessonService service = (LessonService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        LessonService service = (LessonService) serviceWithData;
        Lesson duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() {
        LessonService service = (LessonService) emptyService;
        Optional<Lesson> result = service.get("Nonexistent Lesson");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() {
        LessonService service = (LessonService) serviceWithData;
        Optional<Lesson> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllDoesNotThrowWhenInitialized() {
        LessonService service = (LessonService) serviceWithData;
        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateThrowsOnNonExistentName() {
        LessonService service = (LessonService) emptyService;
        Lesson updated = Lesson.builder()
                .name("Nonexistent Lesson")
                .type(LessonType.LECTURE)
                .mode(LessonMode.OFFLINE)
                .note("")
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .language(StudyLanguage.ENGLISH)
                .weekPattern(WeekPattern.ODD)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> service.update("Nonexistent Lesson", updated));
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        LessonService service = (LessonService) serviceWithData;
        assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), null));
    }


    @Test
    void testDeleteThrowsOnNonExistentName() {
        LessonService service = (LessonService) emptyService;
        assertThrows(IllegalArgumentException.class,
                () -> service.delete("Nonexistent Lesson"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() throws IOException {
        LessonService service = (LessonService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() throws IOException {
        LessonService service = (LessonService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }
}
