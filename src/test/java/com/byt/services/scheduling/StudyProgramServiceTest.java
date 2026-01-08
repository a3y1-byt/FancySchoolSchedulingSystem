package com.byt.services.scheduling;

import com.byt.data.scheduling.StudyProgram;
import com.byt.enums.scheduling.StudyProgramLevel;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudyProgramServiceTest extends CRUDServiceTest<StudyProgram> {

    protected StudyProgramServiceTest() {
        super(DataSaveKeys.STUDY_PROGRAMS, StudyProgramService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Computer Science";
    }

    @Override
    protected StudyProgram getSampleObject() {
        return StudyProgram.builder()
                .name("Computer Science")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(new HashSet<>())
                .build();
    }

    @Override
    protected void alterEntity(StudyProgram studyProgram) {
        studyProgram.setLevel(StudyProgramLevel.MASTER);
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        StudyProgramService service = (StudyProgramService) emptyService;
        StudyProgram program = StudyProgram.builder()
                .name("Placeholder Program")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(new HashSet<>())
                .build();
        service.create(program);
        Optional<StudyProgram> loaded = service.get("Placeholder Program");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Program", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        StudyProgramService service = (StudyProgramService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        StudyProgram duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() {
        StudyProgramService service = (StudyProgramService) emptyService;
        Optional<StudyProgram> result = service.get("Nonexistent Program");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        Optional<StudyProgram> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllDoesNotThrowWhenInitialized() {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        StudyProgram updated = StudyProgram.builder()
                .name(getSampleObjectId())
                .level(StudyProgramLevel.MASTER)
                .specializations(new HashSet<>())
                .build();
        service.update(getSampleObjectId(), updated);
        Optional<StudyProgram> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isPresent());
        assertEquals(StudyProgramLevel.MASTER, loaded.get().getLevel());
    }

    @Test
    void testUpdateThrowsOnNonExistentName() {
        StudyProgramService service = (StudyProgramService) emptyService;
        StudyProgram updated = StudyProgram.builder()
                .name("Nonexistent Program")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(null)
                .build();
        assertThrows(IllegalArgumentException.class, () -> service.update("Nonexistent Program", updated));
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), null));
    }

    @Test
    void testDeleteRemovesExistingEntity() throws IOException {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<StudyProgram> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentName() {
        StudyProgramService service = (StudyProgramService) emptyService;
        assertThrows(IllegalArgumentException.class, () -> service.delete("Nonexistent Program"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() throws IOException {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() throws IOException {
        StudyProgramService service = (StudyProgramService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }

    @Test
    void testCreateThrowsOnNullName() {
        StudyProgramService service = (StudyProgramService) emptyService;
        StudyProgram program = StudyProgram.builder()
                .name(null)
                .level(StudyProgramLevel.BACHELOR)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(program));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnEmptyName() {
        StudyProgramService service = (StudyProgramService) emptyService;
        StudyProgram program = StudyProgram.builder()
                .name("  ")
                .level(StudyProgramLevel.BACHELOR)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(program));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnNullLevel() {
        StudyProgramService service = (StudyProgramService) emptyService;
        StudyProgram program = StudyProgram.builder()
                .name("Program")
                .level(null)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(program));
        assertEquals(ExceptionCode.NOT_NULL_VIOLATION, ex.getExceptionCode());
    }

}
