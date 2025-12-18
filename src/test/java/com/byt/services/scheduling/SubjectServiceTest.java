package com.byt.services.scheduling;

import com.byt.data.scheduling.Subject;
import com.byt.enums.scheduling.SubjectType;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SubjectServiceTest extends CRUDServiceTest<Subject> {

    protected SubjectServiceTest() {
        super(DataSaveKeys.SUBJECTS, SubjectService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Introduction to Programming";
    }

    @Override
    protected Subject getSampleObject() {
        return Subject.builder()
                .name("Introduction to Programming")
                .hours(60)
                .types(Arrays.asList(SubjectType.NORMAL_SUBJECT, SubjectType.EXAM_SUBJECT))
                .lessons(null)
                .build();
    }

    @Override
    protected void alterEntity(Subject subject) {
        subject.setHours(subject.getHours() + 5);
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        SubjectService service = (SubjectService) emptyService;
        Subject subject = Subject.builder()
                .name("Placeholder Subject")
                .hours(30)
                .types(Arrays.asList(SubjectType.NORMAL_SUBJECT))
                .lessons(null)
                .build();
        service.create(subject);
        Optional<Subject> loaded = service.get("Placeholder Subject");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Subject", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        SubjectService service = (SubjectService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        SubjectService service = (SubjectService) serviceWithData;
        Subject duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() throws IOException {
        SubjectService service = (SubjectService) emptyService;
        Optional<Subject> result = service.get("Nonexistent Subject");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() throws IOException {
        SubjectService service = (SubjectService) serviceWithData;
        Optional<Subject> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllReturnsListOnInitializedEmptyService() throws IOException {
        SubjectService service = (SubjectService) emptyService;
        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        SubjectService service = (SubjectService) serviceWithData;
        Subject updated = Subject.builder()
                .name(getSampleObjectId())
                .hours(90)
                .types(Arrays.asList(SubjectType.NORMAL_SUBJECT))
                .lessons(null)
                .build();
        service.update(getSampleObjectId(), updated);
        Optional<Subject> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isPresent());
        assertEquals(90, loaded.get().getHours());
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        SubjectService service = (SubjectService) serviceWithData;
        assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), null));
    }

    @Test
    void testDeleteRemovesExistingEntity() throws IOException {
        SubjectService service = (SubjectService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<Subject> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentName() {
        SubjectService service = (SubjectService) emptyService;
        assertThrows(IllegalArgumentException.class, () -> service.delete("Nonexistent Subject"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() throws IOException {
        SubjectService service = (SubjectService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() throws IOException {
        SubjectService service = (SubjectService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }
    @Test
    void testCreateThrowsOnNullName() {
        SubjectService service = (SubjectService) emptyService;
        Subject subject = Subject.builder()
                .name(null)
                .hours(60)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(subject));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnEmptyName() {
        SubjectService service = (SubjectService) emptyService;
        Subject subject = Subject.builder()
                .name("")
                .hours(60)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(subject));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnZeroHours() {
        SubjectService service = (SubjectService) emptyService;
        Subject subject = Subject.builder()
                .name("Subject")
                .hours(0)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(subject));
        assertEquals(ExceptionCode.MIN_VALUE_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnNegativeHours() {
        SubjectService service = (SubjectService) emptyService;
        Subject subject = Subject.builder()
                .name("Subject")
                .hours(-10)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(subject));
        assertEquals(ExceptionCode.MIN_VALUE_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testUpdateThrowsOnInvalidHours() {
        SubjectService service = (SubjectService) serviceWithData;
        Subject invalid = Subject.builder()
                .name(getSampleObjectId())
                .hours(0)
                .build();
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), invalid));
        assertEquals(ExceptionCode.MIN_VALUE_VIOLATION, ex.getExceptionCode());
    }
}
